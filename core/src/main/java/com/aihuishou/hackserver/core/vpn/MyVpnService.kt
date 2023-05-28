package com.aihuishou.hackserver.core.vpn

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.aihuishou.hackserver.core.HackServer
import com.aihuishou.hackserver.core.func.LogFunc
import kotlinx.io.core.buildPacket
import kotlinx.io.core.discardExact
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

class MyVpnService : VpnService() {

    companion object {
        var isStarted = false
        var packageDataPath = ""

        fun createDataFile(context: Context) {
            val folderPath = context.filesDir.absolutePath + "/capture"
            val folder = File(folderPath)
            if(!folder.exists()) {
                folder.mkdirs()
            }
            val filePath = "$folderPath/" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(Date(System.currentTimeMillis())) + ".txt"
            packageDataPath = filePath
        }
    }

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onCreate() {
        super.onCreate()
        // 创建VPN接口
        val builder = Builder()
        builder.addAddress("10.0.0.2", 24)
        builder.addRoute("0.0.0.0", 0)
        vpnInterface = builder.establish()

        vpnInterface?.let {
            // 开启线程处理网络数据包
            Thread(PacketHandler(it.fileDescriptor)).start()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        vpnInterface?.close()
        isStarted = false
        packageDataPath = ""
    }

    inner class PacketHandler(val fd: FileDescriptor) : Runnable {
        override fun run() {
            val inputStream = FileInputStream(fd)
            val outputStream = FileOutputStream(fd)
            val datagramSocket = DatagramSocket()

            // 保护套接字，以便其流量不会被VPN服务处理
            protect(datagramSocket)

            val packet = DatagramPacket(ByteArray(1024), 1024)

            while (true) {
                // 从VPN接口读取数据包
                val length = inputStream.read(packet.data)
                Log.i("nbox", String(packet.data))
                if (length > 0) {
                    val ipPacket = buildPacket {
                        writeFully(packet.data, 0, length)
                    }

                    // 解析IP数据包
                    val versionAndHeaderLength = ipPacket.readByte()
                    val version = versionAndHeaderLength.toInt() shr 4
                    val headerLength = (versionAndHeaderLength.toInt() and 0x0F) * 4
                    ipPacket.discardExact(8)
                    val protocol = ipPacket.readByte()
                    ipPacket.discardExact(2)
                    val sourceAddress = ipPacket.readInt()
                    val destinationAddress = ipPacket.readInt()

                    // 解析TCP/UDP数据包
                    if (protocol.toInt() == 6 || protocol.toInt() == 17) {
                        ipPacket.discardExact(headerLength - 20)
                        val sourcePort = ipPacket.readShort().toInt() and 0xFFFF
                        val destinationPort = ipPacket.readShort().toInt() and 0xFFFF

                        // 将数据包发送到目标地址和端口
                        packet.length = length
                        packet.socketAddress = InetSocketAddress(
                            InetAddress.getByAddress(destinationAddress.toByteArray()), destinationPort)
                        datagramSocket.send(packet)

                        // 接收响应数据包
                        datagramSocket.receive(packet)

                        // 将响应数据包写回VPN接口
                        outputStream.write(packet.data, 0, packet.length)
                    }
                }
            }
        }
    }
}

fun Int.toByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )
}
