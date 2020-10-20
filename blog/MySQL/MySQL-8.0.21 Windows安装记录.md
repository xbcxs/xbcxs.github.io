[toc]
# MySQL-8.0.21 Windows安装

## 下载安装包
https://dev.mysql.com/downloads/mysql/

将解压文件解压到你安装的目录：E:\mysql-8.0.21-winx64。

mysql-8.0.11 不再有 my.ini 配置文件了. 通过mysqld --initialize --console 会自动生成MYSQL的初始化配置(data文件目录等).

## 系统环境配置
在path中添加%MYSQL_HOME%\bin。配置环境变量后启动MySQL就不必到MySQL的bin目录下启动了，直接在cmd中直接输入net start mysql 启动服务了

## 开始安装

- **以管理员的身份**打开cmd窗口跳转路径到E:\mysql-8.0.21-winx64\bin。
- 初始化命令mysqld --initialize --user=mysql --console。初始化完成之后，会生成一个临时密码这里需要注意把临时密码记住。
- 输入mysqld -install进行安装。
- 输入net start mysql启动服务。
- 命令行登录mysql。
```
mysql -u root -p
Enter password: ************
```
- 修改密码语句：ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
- 刷新：FLUSH PRIVILEGES;

## 可能出现问题及解决方案

### 提示缺少vcruntime140.dll
下载微软常用运行库合集_2019.07.20_X64.exe解决

### net start mysql 发生系统错误2 系统找不到指定的文件

**以管理员身份运行**，在命令行输入cd+mySQL的bin目录的安装路径

C:\Windows\system32>cd C:\Program Files\MySQL\MySQL Server5.6\bin

C:\Program Files\MySQL\MySQL Server5.6\bin>mysqld --remove

Service successfully removed.

C:\Program Files\MySQL\MySQL Server5.6\bin>mysqld --install

Service successfully installed.

C:\Program Files\MySQL\MySQL Server5.6\bin>net start mysql

MySQL 服务正在启动 .

MySQL 服务已经启动成功。

