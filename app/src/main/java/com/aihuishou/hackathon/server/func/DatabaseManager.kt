package com.aihuishou.hackathon.server.func


import android.content.Context
import android.database.sqlite.SQLiteDatabase


class DatabaseManager(private val dbFilePath: String) {
    private val mContext: Context? = null
    private var mDB: SQLiteDatabase = SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)

    fun queryAllTableNames(): List<String> {
        val tableNames = arrayListOf<String>()
        try {
            val sql = "select name from sqlite_master where type='table' order by name"
            val cursor = mDB.rawQuery(sql, null)
            while (cursor.moveToNext()) {
                tableNames.add(cursor.getString(0))
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tableNames
    }

    fun queryTableColumns(tableName: String): List<String> {
        val columnNames = arrayListOf<String>()
        try {
            val sql = "PRAGMA table_info($tableName)"
            val cursor = mDB.rawQuery(sql, null)
            while (cursor.moveToNext()) {
                columnNames.add(cursor.getString(0))
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return columnNames
    }

    /**
    //查询选择题
    fun queryUser(): List<User>? {
        val userList: MutableList<User> = ArrayList()
        var user: User? = null
        openDB()
        try {
            val sql =
                " select a.id,a.name,a.age,a.phoneNum,b.name as sexName from user a,gender b where a.sex= b.type"
            val cursor = mDB!!.rawQuery(sql, null)
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val sex = cursor.getString(cursor.getColumnIndex("sexName"))
                val age = cursor.getString(cursor.getColumnIndex("age"))
                val phoneNum = cursor.getString(cursor.getColumnIndex("phoneNum"))
                user = User()
                user.id = id
                user.setName(name)
                user.setAge(age)
                user.setPhoneNum(phoneNum)
                user.setSex(sex)
                userList.add(user)
            }
            cursor.close()
            return userList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun updateUser() {
        openDB()
        val sql = " update  user  set name = '李四' where name = '王杰' "
        mDB!!.execSQL(sql)
    }

    fun deleteUser(id: String) {
        openDB()
        mDB!!.delete("user", " id = ? ", arrayOf(id))
    }

    **/

}