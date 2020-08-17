# 远程调用另一台windows程序

## 需求背景
需要从一台机器上通过传参远程调用另一台机器上的exe程序进行计算。

## 将远程机器上exe注册成widonws服务
### 方式1

注册服务

```

sc create testService binPath= "D:\1WinSoft\360\360sd\360rps.exe"  displayname= "360Test Services"
```

删除服务

```
sc delete testService 
```

### 方式2

使用srvany.exe将任何程序作为Windows服务运行。

## 开启远程机器配置
1. 打开电脑；
2. 点击“我的电脑”或者“此电脑”图标，进入属性设置页面；
3. 设置远程协助或者远程桌面功能；
4. 点击应用、确定即可。

## CMD调用命令
编写测试bat

```
echo service start!!!

net use \\192.168.10.96\ipc$ "pw" /user:"account"

sc \\192.168.10.96  start dayan
pause
```

## 参考
[1] https://www.cnblogs.com/yepei/p/6218887.html  
[2] http://wangye.org/blog/archives/42/  
[3] https://blog.csdn.net/qq_40001362/article/details/80867481