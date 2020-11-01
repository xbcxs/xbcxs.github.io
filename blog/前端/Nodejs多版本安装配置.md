# Nodejs多版本安装配置
## NVM简介
nvm全名node.js version management，顾名思义是一个nodejs的版本管理工具。通过它可以安装和切换不同版本的nodejs。下面列出下载、安装及使用方法。
## 下载
前往 https://github.com/coreybutler/nvm-windows/releases 下载

- nvm-noinstall.zip 绿色免安装版，但使用时需进行配置。
- nvm-setup.zip 安装版，推荐使用

## 安装
**注意：nvm的安装目录不能有汉字和空格，否则会报错**

1. 解压nvm-setup.zip进行安装  
2. 选择nvm安装路径，例如：D:\dev\nvm
3. 选择nodejs安装路径，例如：D:\dev\nvm\nodejs
4. 确认安装

打开CMD
```
C:\users\ac>nvm
Running version 1.1.7
...
```
## 配置node_mirror、nvm npm_mirror[可选]
将npm镜像改为淘宝的镜像，可以提高下载速度

命令配置
```
nvm node_mirror https://npm.taobao.org/mirrors/node
nvm npm_mirror https://npm.taobao.org/mirrors/npm
```
或者在安装目录settings.txt文件查看修改。

```
root: D:\dev\nvm
path: D:\dev\nvm\nodejs
node_mirror: https://npm.taobao.org/mirrors/node
npm_mirror: https://npm.taobao.org/mirrors/npm
```

## 安装nodejs

```
# 查看可安装nodejs版本
C:\Users\ac>nvm list [available]
# 安装nodejs
C:\Users\ac>nvm install <version> [arch]
# 卸载nodejs
C:\Users\ac>nvm uninstall <version>
```
nvm node安装示例
```
C:\Users\SN-515>nvm install 10.9.0
Downloading node.js version 10.9.0 (64-bit)...
Complete
Creating D:\1DevSoft\nvm\temp

Downloading npm version 6.2.0... Complete
Installing npm v6.2.0...

Installation complete. If you want to use this version, type

nvm use 10.9.0

C:\Users\SN-515>nvm use 10.9.0
Now using node v10.9.0 (64-bit)

C:\Users\SN-515>node -v
v10.9.0

C:\Users\SN-515>npm -v
6.2.0
```
安装完node后续需要使用`nvm use 10.9.0`命令来切换node版本

安装成功后nvm跟目录多出以下文件/目录

```
2020/10/29  20:31    <SYMLINKD>     nodejs [D:\dev\nvm\v10.9.0]
2020/10/29  20:31    <DIR>          v10.9.0
```
- "v10.9.0"是刚才下载版本为v10.9.0的node
- "nodejs"是一个“快捷方式”指向D:\dev\nvm\v10.9.0。`nvm use 10.9.0`命令来切换该指向，达到node版本切换的效果。

## NVM相关命令

```
C:\Users\ac>nvm --hlep

Running version 1.1.7.

Usage:

  nvm arch                     : Show if node is running in 32 or 64 bit mode.
  nvm install <version> [arch] : The version can be a node.js version or "latest" for the latest stable version.
                                 Optionally specify whether to install the 32 or 64 bit version (defaults to system arch).
                                 Set [arch] to "all" to install 32 AND 64 bit versions.
                                 Add --insecure to the end of this command to bypass SSL validation of the remote download server.
  nvm list [available]         : List the node.js installations. Type "available" at the end to see what can be installed. Aliased as ls.
  nvm on                       : Enable node.js version management.
  nvm off                      : Disable node.js version management.
  nvm proxy [url]              : Set a proxy to use for downloads. Leave [url] blank to see the current proxy.
                                 Set [url] to "none" to remove the proxy.
  nvm node_mirror [url]        : Set the node mirror. Defaults to https://nodejs.org/dist/. Leave [url] blank to use default url.
  nvm npm_mirror [url]         : Set the npm mirror. Defaults to https://github.com/npm/cli/archive/. Leave [url] blank to default url.
  nvm uninstall <version>      : The version must be a specific version.
  nvm use [version] [arch]     : Switch to use the specified version. Optionally specify 32/64bit architecture.
                                 nvm use <arch> will continue using the selected version, but switch to 32/64 bit mode.
  nvm root [path]              : Set the directory where nvm should store different versions of node.js.
                                 If <path> is not set, the current root will be displayed.
  nvm version                  : Displays the current running version of nvm for Windows. Aliased as v.
```
