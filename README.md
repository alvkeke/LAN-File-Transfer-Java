# FileTP-Java

## 用法
这个程序编译之后能够打包成一个jar文件，利用这个文件运行在电脑的后台，就能够接收到其他设备发送过来的文件，启动服务的方式为：
```shell
java -jar xxx.jar [your-name] [port you want to use]
```

注意，这里的端口如非必要，最好使用10000，因为Android客户端的默认端口是10000
如果真的有必要修改，则需要将其他设备的端口全部修改成你指定的端口，否则会出现找不到设备或者无法发送文件的情况

## 前端
这个程序只是运行在后台的一个服务，如果需要发送文件请使用[C控制端](https://github.com/alvkeke/fileTP-C-controller)对服务进行控制。
详情请跳转到对应仓库进行查看

### 关于图形界面前端的问题
因为这个软件只是方便我本人使用，故没有特意开发图形界面的前端，如果有图形界面前端的需求，请按照如下要求进行编写
1. 使用tcp连接到`指定端口+1`
2. 按照CommandHandler.java文件中的接收要求进行命令收发编写

## 其他客户端
[Android](https://github.com/alvkeke/FileTP-Android)
