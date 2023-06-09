![image](https://github.com/Nbox1989/HackServer/assets/18026755/21f6ec55-6841-4169-9de4-93a563a132e4)

# What is HackServer

HackServer is an sdk that can be integerted into an Android application. By starting up an http server, HackServer allow you to view some detail information in application throw a web brower. For example, you can view and manage files in application's internal directory.
And of course, database management is also implemented. If you want to take a look at some local fields' values in the top activity, HackServer will help you, too.

More functions are coming!

# Integretion and usage

0. integret

The sdk is published to maven central repository. You can simply add this sentence into your android module's build.gradle file:

`implementation 'io.github.nbox1989:hackserver:1.0.0'`

Besides, the no-opt version is also published. For some case we donot want to provide such functions in our release version of application, we can make changes like this:

```
debugImplementation 'io.github.nbox1989:hackserver:1.0.0' 
releaseImplementation 'io.github.nbox1989:hackserver-no-opt:1.0.0'
```

1. initialize

In your application code, call this method to initialize sdk.

`HackServer.init(Application)`

2. start server

Call this method to startup an http server which will listen on port 9999.

`HackServer.startServer(Context)`

3. shutdown server

Call this method to shutdown server.

`HackServer.shutdownServer()`

4. view server

When the http server is started, **visit "http://[ip]:9999" on a browser within the same local area network as your Android device**. Note: replace 'ip' with the local area network IP address of your phone.

# Functions developed

1. Clipboard
2. Top Activity View
3. File Management
4. Database Management
5. Log Capture
6. Code Execution (By Reflection)
7. Http Package Capture (By VpnService). **(in progress)**

