# HTTP请求参数为目录引发的安全问题

## 背景

一个请求图片资源的服务接口，请求参数上设计了参数值为相对路径的参数，服务接口根据参数相对路径返回后台对应的资源信息。

Acunetix进行WEB漏洞扫描检测出该接口问题如下：

> /xxx/config/img/replace.action
> Alert group	Directory traversal
> Severity	High
> Description	This script is possibly vulnerable to directory traversal attacks.
> 
> Directory Traversal is a vulnerability which allows attackers to access restricted directories and read files outside of the web server's root directory.
> Recommendations	Your script should filter metacharacters from user input.
> Alert variants
> Details	URL encoded GET input imgUrl was set to ../../../../../../../../../../etc/passwd
> 
> File contents found:
> root:x:0:0:root:/root:/bin/bash

该接口会导致后台服务器所有资源文件都被加载返回给接口调用着非常危险。

## 改进
- 参数脱敏。
- 接口参数逻辑避免直接对应后台资源返回。
- 后台服务资源返回必须为业务逻辑可控。
