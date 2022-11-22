<h1>部署</h1>

<img src="../../../assets/depoly.png" alt="rice集群部署">

## Mysql

[https://github.com/gaojiayi/RICE/tree/master/db](https://github.com/gaojiayi/RICE/tree/master/db)   将db目录下的sql脚本在mysql中执行，初始化数据库

## 搭建控制器

**以rice-bin.tar.gz为例**

```
解压  tar -zxvf rice-bin.tar.gz

cd conf
```

编辑配置文件 rice-controller.properties，配置所有控制器的ip地址以及业务端口
```
allControllerAddressStr=127.0.0.1:8900,127.0.0.1:8901,127.0.0.1:8902
```

启动控制器
```
cd  ../bin

启动  ./ricecontroller
```

如果看到这样的输出信息 说明已经启动
![controller-start-ok-log](../../../assets/controller-start-ok-log.png)

```
关闭控制器 ./riceshutdown controller
```

接下来在其他的控制器的host上做相同的操作部署，组成控制器集群，控制器数量不少于3台。

后续关于控制器的配置 将在后续介绍。

## 搭建管理控制台

在每一个控制器的主机上

* **安装nginx** 

    此处自行搭建

* **build rice-manage-ui**

```
cd rice-manage-ui

pnpm install

pnpm run build
```

看到这个输出说明已经成功编译

![manage-ui-build-ok-log](../../../assets/manage-ui-build-ok-log.png)

* **部署至nginx**

`1 将dist整个文件夹上传到服务Nginx的/usr/local/nginx/html 这个目录里`

`2 vi nginx.conf 进入nginx配置文件中，location里的root就是运行index.html的位置`

`3 nginx -s reload`

`4 systemctl nginx restart`

*   **打开浏览器，访问nginx配置端口**

    ****

****![](../../../assets/manage-ui-login.png)****

## 搭建调度器集群

**以rice-bin.tar.gz为例**

```
解压  tar -zxvf rice-bin.tar.gz

cd conf
```

编辑配置文件 rice-dispatcher.properties，配置所有控制器的ip地址以及业务端口
```
allControllerAddressStr=127.0.0.1:8900,127.0.0.1:8901,127.0.0.1:8902
```

启动调度器
```
cd  ../bin

启动  ./ricedispatcher
```

启动成功后，日志如下：

```
2022-10-30 15:18:00 INFO main - allControllerElectionAddressStr=127.0.0.1:8898,127.0.0.1:8899,127.0.0.1:8900
2022-10-30 15:18:00 INFO main - JMXManagePort=9090
2022-10-30 15:18:00 INFO main - riceHome=/Users/gaojiayi/RICE/rice-distribution/src
2022-10-30 15:18:00 INFO main - configStorePath=/Users/gaojiayi/RICE/rice-distribution/src/conf/rice-dispatcher.properties
2022-10-30 15:18:01 INFO MLog-Init-Reporter - MLog clients using slf4j logging.
2022-10-30 15:18:01 INFO main - Initializing c3p0-0.9.5.2 [built 08-December-2015 22:06:04 -0800; debug? true; trace: 10]
2022-10-30 15:18:01 INFO NettyEventExecutor - NettyEventExecutor service started
2022-10-30 15:18:01 INFO PullTaskService - PullTaskService service started
2022-10-30 15:18:01 INFO main - [37m         _____                   _                _____                              __       _ _[m
2022-10-30 15:18:01 INFO main - [37m        |  __ \                 (_)              / ____|                            / _|     | | |[m
2022-10-30 15:18:01 INFO main - [37m        | |__) |   _ _ __  _ __  _ _ __   __ _  | (___  _   _  ___ ___ ___  ___ ___| |_ _   _| | |_   _[m
2022-10-30 15:18:01 INFO main - [37m        |  _  / | | | '_ \| '_ \| | '_ \ / _` |  \___ \| | | |/ __/ __/ _ \/ __/ __|  _| | | | | | | | |[m
2022-10-30 15:18:01 INFO main - [37m        | | \ \ |_| | | | | | | | | | | | (_| |  ____) | |_| | (_| (_|  __/\__ \__ \ | | |_| | | | |_| |[m
2022-10-30 15:18:01 INFO main - [37m        |_|  \_\__,_|_| |_|_| |_|_|_| |_|\__, | |_____/ \__,_|\___\___\___||___/___/_|  \__,_|_|_|\__, |[m
2022-10-30 15:18:01 INFO main - [37m                                          __/ |                                                    __/ |[m
2022-10-30 15:18:01 INFO main - [37m                                         |___/                                                    |___/[m
2022-10-30 15:18:01 INFO main - [37m###################################_____  _____ _____ ______  #####################################################[m
2022-10-30 15:18:01 INFO main - [37m###################################|  __ \|_   _/ ____|  ____|#####################################################[m
2022-10-30 15:18:01 INFO main - [37m###################################| |__) | | || |    | |__   #####################################################[m
2022-10-30 15:18:01 INFO main - [37m###################################|  _  /  | || |    |  __|  #####################################################[m
2022-10-30 15:18:01 INFO main - [37m###################################| | \ \ _| || |____| |____ #####################################################[m
2022-10-30 15:18:01 INFO main - [37m###################################|_|  \_\_____\_____|______|#####################################################[m
2022-10-30 15:18:01 INFO main - [37m########################################################+-+-+-+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+-+[m
2022-10-30 15:18:01 INFO main - [37m########################################################|D|i|s|t|r|i|b|u|t|e|d| |C|l|u|s|t|e|r| |S|c|h|e|d|u|l|e|r|[m
2022-10-30 15:18:01 INFO main - [37m########################################################+-+-+-+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+-+[m
2022-10-30 15:18:01 INFO main - The RICE Dispatcher boot success. serializeType=JSON

```

```
关闭控制器 ./riceshutdown dispatcher
```

## 处理器集群

对于rice而言，每一个处理器可以是一个业务服务，比如一个springboot服务或者是一个tomacat应用。
