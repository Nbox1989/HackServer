![image](https://github.com/Nbox1989/HackServer/assets/18026755/21f6ec55-6841-4169-9de4-93a563a132e4)

# What is HackServer

HackServer is an sdk that can be integerted into an Android application. By starting up an http server, HackServer allow you to view some detail information in application throw a web brower. For example, you can view and manage files in application's internal directory.
And of course, database management is also implemented. If you want to take a look at some local fields' values in the top activity, HackServer will help you, too.

More functions are coming!

# How to use
1. initialize

call this method to initialize sdk

`HackServer.init(Application)`

2. start server

call this method to startup an http server listening on port 9999

`HackServer.startServer(Context)`

3. shutdown server

call this method to shutdown server

`HackServer.shutdownServer()`

4. view server

When the http server is started, visit "http://[ip]:9999" on a browser within the same local area network as your Android device. Note: replace 'ip' with the local area network IP address of your phone.

# Functions developed

1. Clipboard
2. Top Activity View
3. File Management
4. Database Management
5. Log Capture
6. Code Execution (By Reflection)
7. Http Package Capture (By VpnService). **(in progress)**

# How to integret

to be continued...
