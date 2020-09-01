# Tomcat日志、项目中Log4j日志、控制台日志之间关系
本文基于Linux环境
## Tomcat日志
Tomcat自带日志实现功能，也可配置其他如Log4j日志实现。

Tomcat有五类日志：catalina、localhost、manager、admin、host-manager

Tomcat目录下的/conf/logging.properties
```
# 可配置项(5类日志)：catalina、localhost、manager、host-manager、Console
handlers = 1catalina.org.apache.juli.AsyncFileHandler, 2localhost.org.apache.juli.AsyncFileHandler, 3manager.org.apache.juli.AsyncFileHandler, 4host-manager.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

# 日志输出为输出到文件和输出到控制台
.handlers = 1catalina.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

############################################################
# Handler specific properties.
# Describes specific configuration info for Handlers.
############################################################

# 日志输出级别：SEVERE (最高级别) > WARNING > INFO > CONFIG > FINE > FINER(精心) > FINEST (所有内容,最低级别)
1catalina.org.apache.juli.AsyncFileHandler.level = FINE
1catalina.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
1catalina.org.apache.juli.AsyncFileHandler.prefix = catalina.
1catalina.org.apache.juli.AsyncFileHandler.encoding = UTF-8

2localhost.org.apache.juli.AsyncFileHandler.level = FINE
2localhost.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
2localhost.org.apache.juli.AsyncFileHandler.prefix = localhost.
2localhost.org.apache.juli.AsyncFileHandler.encoding = UTF-8

3manager.org.apache.juli.AsyncFileHandler.level = FINE
3manager.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
3manager.org.apache.juli.AsyncFileHandler.prefix = manager.
3manager.org.apache.juli.AsyncFileHandler.encoding = UTF-8

4host-manager.org.apache.juli.AsyncFileHandler.level = FINE
4host-manager.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
4host-manager.org.apache.juli.AsyncFileHandler.prefix = host-manager.
4host-manager.org.apache.juli.AsyncFileHandler.encoding = UTF-8

java.util.logging.ConsoleHandler.level = FINE
java.util.logging.ConsoleHandler.formatter = org.apache.juli.OneLineFormatter
java.util.logging.ConsoleHandler.encoding = UTF-8

############################################################
# Facility specific properties.
# Provides extra control for each logger.
############################################################

org.apache.catalina.core.ContainerBase.[Catalina].[localhost].level = INFO
org.apache.catalina.core.ContainerBase.[Catalina].[localhost].handlers = 2localhost.org.apache.juli.AsyncFileHandler

org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].level = INFO
org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].handlers = 3manager.org.apache.juli.AsyncFileHandler

org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/host-manager].level = INFO
org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/host-manager].handlers = 4host-manager.org.apache.juli.AsyncFileHandler

```
### catalina.out  
catalina.out即标准输出和标准出错，所有输出到这两个位置的都会进入catalina.out，这里包含**tomcat运行自己输出的日志**以及**应用里向console输出的日志**。

### catalina.YYYY-MM-DD.log

catalina.{yyyy-MM-dd}.log是tomcat自己运行的一些日志，这些日志还会输出到catalina.out。

### localhost.YYYY-MM-DD.log
localhost.{yyyy-MM-dd}.log主要是应用初始化(listener, filter, servlet)未处理的异常最后被tomcat捕获而输出的日志,它只是记录了部分日志。

### localhost_access_log.YYYY-MM-DD.txt
访问tomcat的日志，请求时间和资源，状态码都有记录。

###  host-manager.YYYY-MM-DD.log
tomcat的自带的manager项目的日志信息。

### manager.YYYY-MM-DD.log
tomcat manager项目专有的日志文件

## 项目中日志
项目中如果采用Log4j，则日志指定打印到什么地方（控制台或者文件），便会打印到什么地方。

如果项目中的log4j指定打印到控制台，并且你开启的tomcat有控制台，那么这个时候你可以在控制台观察到日志。==同时日志也会记录到tomcat的catalina.out。==

如果Tomcat采用Log4j实现日志（具体需要你自己配置tomcat的配置文件），那么Tomcat便会使用log4j来打印，但是这和你项目中的log4j仍然不是同一个log4j。

## 控制台日志
Tomcat默认会往控制台输出日志，程序中的syso、e.printStackTrace()，以及项目log4j如果配置的往控制台输出日志，他们全部都会出现在控制台上，

控制台上的日志，可能是不同日志记录器输出的。

## 日志之间关系

```
graph TB
程序/Log4j-->程序Log4j文件目录
程序/Log4j-->控制台
程序System.out.println-->控制台
程序e.printStackTrace-->控制台
控制台-->Catalina.out
Tomcat/Log4j-->Catalina.out
Tomcat/Log4j-->catalina.YYYY-MM-DD.log,tomcat自己运行的一些日志
Tomcat/Log4j-->localhost.YYYY-MM-DD.log,未处理的异常最后被tomcat捕获而输出的日志

```
## 按天切割catalina.out


## 参考
https://www.cnblogs.com/operationhome/p/9680040.html  
https://www.cnblogs.com/flying607/p/6293970.html  
https://blog.csdn.net/chenshijie2011/article/details/78624480  
https://www.west.cn/docs/51491.html  