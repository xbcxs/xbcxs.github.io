# Oracle startup ORA-27102 out of memory

# 问题1
linux下Oracel 11g启动报错

```
SQL> startup
ORA-27102: out of memory
Linux-x86_64 Error: 28: No space left on device
```

# 解决过程

- 查看系统内存

```
[root@test ~] free -m
```
- 查看 getconf PAGE_SIZE)

```
[root@test ~] getconf PAGE_SIZE)
```


- 设置kernel.shm相关参数
```
[root@test ~] /etc/sysctl.conf
// 配置为物理内存一半
kernel.shmmax = 15618062894
// 配置参数大小为：shmmax/pagesize
kernel.shmall =8388608
```
- 保存修改立刻生效
```
[root@test ~] sysctl -p
```

- 切换数据库账户重启数据库
```
SQL>startup
```

# 问题2

- 想修改数据库连接数
```
SQL> alter system set processes=300 scope=spfile;  
alter system set processes=300 scope=spfile;  
```
<font color=red>
ORA-32001: 已请求写入 SPFILE,但是没有偶正在使用的SPFILE  
</font>

# 解决过程
要动态修改一定要用spfile启动。如果现在是用pfile启动，可以这样切换成spfile启动：  

```
SQL>create spfile from pfile;  
SQL>shutdown immediate;  
SQL>startup;
```
 
# 参考
[1] https://www.cnblogs.com/vipsoft/archive/2012/06/05/2537270.html  
[2] https://blog.csdn.net/haiross/article/details/41696035  
[3] https://blog.csdn.net/bapinggaitianli/article/details/46705881
