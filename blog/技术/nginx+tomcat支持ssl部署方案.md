# Nginx+Tomcat支持SSL部署方案

## 摘要
由于项目部署需要到外网，为了提高系统访问安全，要求所有对外请求必须提供HTTPS支持。本文介绍实现方式为Nginx部署SSL证书，Tomcat增加https配置支持。该方式不需要Tomcat配置SSL证书，最大程度减少项目部署配置。

## SSL证书

正式部署环境SSL证书需要到SSL证书厂商申请购买。本文用于测试验证，SSL证书是自己生成，由于自己生成的证书不是浏览器厂商认可的受信机构颁发，所以浏览器会打红叉。
 
以Windows10系统为例，生成证书。
 
### 安装OpenSSL
到http://slproweb.com/products/Win32OpenSSL.html去下载OpenSSL（根据系统选择32位或者64位版本下载安装）。然后安装到指定目录下。

### 安装Nginx
- 到Nginx官网下载Nginx，我这里下载的是 nginx/Windows-1.12.0 这个版本。
- 把下载好的压缩包解压出来，拷贝其中的nginx-1.12.0目录到c:\下。并将文件夹名字修改为nginx。这样，Nginx就被安装到了c:\nginx目录下。
- 进入到C:\nginx目录下，双击nginx.exe文件即可启动服务器。在浏览器地址栏输入http://localhost，如果可以成功访问到Nginx的欢迎界面，则说明安装成功。

### 生成证书
**1. 创建ssl文件夹用于存放证书**

在控制台中执行：
```
cd C:\nginx\ssl
```
**2. 创建私钥**

在命令行中执行命令：

```
openssl genrsa -des3 -out privatekey.key 1024
```
privatekey文件名是自己随便起即可
输入密码后，再次重复输入确认密码。记住此密码，后面会用到。

**3. 创建csr证书**

在命令行中执行命令：

```
openssl req -new -key privatekey.key -out privatekey.csr
```

其中key文件为刚才生成的文件。

执行上述命令后，需要输入一系列的信息。输入的信息中最重要的为Common Name，这里输入的域名即为我们要使用https访问的域名 ，比如我输入的是localhost。其它的内容随便填即可。

以上步骤完成后，ssl文件夹内出现两个文件：privatekey.csr 和 buduhuis.key

**4. 去除密码**

在加载SSL支持的Nginx并使用上述私钥时除去必须的口令，否则会在启动nginx的时候需要输入密码。

复制privatekey.key并重命名为privatekey.key.org。

在命令行中执行如下命令以去除口令：

```
openssl rsa -in privatekey.key.org -out privatekey.key
```

然后输入密码，这个密码就是上文中在创建私钥的时候输入的密码。

**5. 生成crt证书**

在命令行中执行此命令：

```
openssl x509 -req -days 365 -in privatekey.csr -signkey privatekey.key -out privatekey.crt
```

至此，证书生成完毕。我们发现，ssl文件夹中一共生成了4个文件。下面，配置https服务器的时候，我们需要用到的是其中的privatekey.crt和privatekey.key这两个文件。

## Nginx配置SSL证书

```
# HTTPS server
server {
    # 系统默认443，这里设置为1234
    listen       1234 ssl;
    server_name  localhost;

    ssl_certificate      C://nginx//ssl//privatekey.crt;  # 这个是证书的crt文件所在目录
    ssl_certificate_key  C://nginx//ssl//privatekey.key;  # 这个是证书key文件所在目录

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_ciphers  HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    location / {
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $http_host;
        proxy_set_header X-Forwarded-Proto https;
        proxy_redirect off;
        proxy_connect_timeout      240;
        proxy_send_timeout         240;
        proxy_read_timeout         240;
        # note, there is not SSL here! plain HTTP is used
        proxy_pass http://127.0.0.1:8081;
    }
}
```

### Nginx常用操作命令
在继续后面的内容之前，先简单介绍下Windows命令行中操作Nginx的几个常用的语句。以下命令如果没有配置nginx环境变量则需要进入到nginx安装目录下执行。
```
start nginx.exe           # 启动Nginx
nginx.exe -s stop         # 快速停止Nginx，可能并不保存相关信息
nginx.exe -s quit         # 完整有序的停止Nginx，并保存相关信息
nginx.exe -s reload       # 重新载入Nginx，当配置信息修改，需要重新载入这些配置时使用此命令。
nginx.exe -s reopen       # 重新打开日志文件
nginx -v                  # 查看Nginx版本
```

因为修改了配置文件，所以需要退出控制台，并重新打开一个控制台。执行如下命令：


```
cd c:\nginx
nginx.exe -s quit
start nginx.exe
```

即退出Nginx，然后再重新启动它。这时候，在浏览器地址栏输入https://localhost并回车。

这时候，你可能看到“您的连接不是私密连接”的提示，单击页面中的“高级”，并接着单击“继续前往（不安全）”，就可以看到Nginx的欢迎界面了。说明https服务器已经配置成功了。

## Tomcat增加对https支持

1. Connector节点加入 redirectPort="1234" proxyPort="1234"
2. 加入新的Value节点 
3. 示例参考
```
<?xml version='1.0' encoding='utf-8'?>
<Server port="8005" shutdown="SHUTDOWN">
    <Service name="Catalina">
        <Connector port="8081" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="1234"
                   proxyPort="1234"/>

        <Engine name="Catalina" defaultHost="localhost">

            <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
                <Valve className="org.apache.catalina.valves.RemoteIpValve"
                       internalProxies="172\.1[6-9]{1}\.\d{1,3}\.\d{1,3}|172\.2[0-9]{1}\.\d{1,3}\.\d{1,3}|172\.3[0-1]{1}\.\d{1,3}\.\d{1,3}"
                       remoteIpHeader="x-forwarded-for"
                       remoteIpProxiesHeader="x-forwarded-by"
                       protocolHeader="x-forwarded-proto"
                />
                <Context path="" docBase="/home/test" reloadable="false"/>
            </Host>
        </Engine>
    </Service>
</Server>
```
PS: 若服务器是172代理过来的ip，必须设置internalProxies属性，请参照tomcat7版本的问题。参考官网说明  
http://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/catalina/valves/RemoteIpValve.html

## 参考

https://www.cnblogs.com/micro-chen/p/11794248.html  
https://www.cnblogs.com/zhanghaoh/p/5293158.html  
https://www.cnblogs.com/swbzmx/p/8845810.html  
http://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/valves/RemoteIpValve.html  
