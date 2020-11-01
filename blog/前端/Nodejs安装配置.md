# Nodejs安装配置

## 下载安装
前往官网下载 https://nodejs.org 下载安装，按需点击下一步。

出现步骤 Custom Setup 选择要安装的功能特性时，全部保持默。

选项 | 说明
---|---
Node.js runtime | 表示运行环境
npm package manager | 	表示npm包管理器
online documentation shortcuts | 在线文档快捷方式
Add to PATH	| 添加到环境变量

- add to PATH会自动在系统变量PATH里添加nodejs安装目录
- npm package manager会在安装nodejs自带安装npm

建议将nodejs安装在非系统盘，例如:D:\dev\nodejs

## 安装验证

进入CMD，如下表示安装成功。

```
C:\Users\ac>node -v
v12.13.1
C:\Users\ac>npm -v
v6.12.1
```

## 修改默认环境变量(可选)

### 修改npm获取资源的远程仓库地址
npm获取资源的远程仓库地址默认地址为 https://registry.npmjs.org

基于以下需求我们可能需要修改默认远程仓库链接地址
- 改用国内镜像仓库提升下载速度
- 改用个人/公司私有仓库地址下载私有组件资源

**示例**： 修改远程仓库链接为 https://registry.npm.taobao.org
```
npm config set registry https://registry.npm.taobao.org
```
### 修改npm下载modules默认目录
上一步我们修改了从哪里下载资源，这里修改资源下载到本地存放的位置。

> 为什么要修改默认路径?
> 1. 如果默认在C盘会导致(系统盘)越来越臃肿，响电脑运行速度
> 2. 有时可能会因为系统权限的问题，导致不能正常成功的安装某些工具
> 3. 杀毒工具误清理等

默认目录地址
```
C:\\Users\\用户名\\AppData\\Roaming\\npm
C:\\Users\\用户名\\AppData\\Roaming\\npm-cache
```

对应修改npm下载modules默认目录

```
npm config set prefix "D:\dev\nodejs\node_global"
npm config set cache "D:\dev\nodejs\node_cache"
```
验证修改后的效果测试

```
npm install express -g
```

执行命令后express模块将会安装在D:\dev\nodejs\node_cache\node_modules\express

### 配置npm执行下载模块的环境变量
上一步如果修改了npm下载modules默认目录，这里需要新增/修改相应的系统变量。

#### 配置用户变量PATH
**配置步骤**

我的电脑->高级系统设置->环境变量->用户变量(U)->编辑变量

变量 | 追加值
---|---
Path | D:\dev\nodejs\node_global

**作用**

用于保障系统可识别下载的module命令。以vue示例

**示例**

安装vue
```
C:\Users\用户名>npm install -g @vue/cli
...
```
安装完毕后(未配置path)测试vue命令，出现命令无法识别错误
```
C:\Users\用户名>vue
'vue' 不是内部或外部命令，也不是可运行的程序
或批处理文件。
```
配置path D:\dev\nodejs\node_global后，执行测试成功。

```
C:\Users\用户名>vue
Usage: vue <command> [options]

Options:
  -V, --version                              output the version number
  -h, --help                                 output usage information

Commands:
  create [options] <app-name>                create a new project powered by vue-cli-service
  add [options] <plugin> [pluginOptions]     install a plugin and invoke its generator in an already created project
  invoke [options] <plugin> [pluginOptions]  invoke the generator of a plugin in an already created project
  inspect [options] [paths...]               inspect the webpack config in a project with vue-cli-service
  serve [options] [entry]                    serve a .js or .vue file in development mode with zero config
  build [options] [entry]                    build a .js or .vue file in production mode with zero config
  ui [options]                               start and open the vue-cli ui
  init [options] <template> <app-name>       generate a project from a remote template (legacy API, requires @vue/cli-init)
  config [options] [value]                   inspect and modify the config
  outdated [options]                         (experimental) check for outdated vue cli service / plugins
  upgrade [options] [plugin-name]            (experimental) upgrade vue cli service / plugins
  migrate [options] [plugin-name]            (experimental) run migrator for an already-installed cli plugin
  info                                       print debugging information about your environment

  Run vue <command> --help for detailed usage of given command.
```
#### 新增系统变量NODE_PATH
**配置步骤**

我的电脑->高级系统设置->环境变量->系统变量(S)->新增变量
变量 | 值
---|---
NODE_PATH | D:\dev\nodejs\node_global\node_modules

**作用**

用于保障可引用到模块资源。

**示例**

未配置NODE_PATH，node下加载第三方模块express出现如下错误。
```
C:\Users\用户名>node
> require('express')
Error: Cannot find module 'express'
    at Function.Module._resolveFilename (internal/modules/cjs/loader.js:581:15)
    at Function.Module._load (internal/modules/cjs/loader.js:507:25)
    at Module.require (internal/modules/cjs/loader.js:637:17)
    at require (internal/modules/cjs/helpers.js:20:18)
```
配置后成功效果

```
C:\Users\用户名>node
> require('express')
{ [Function: createApplication]
  application:
   { init: [Function: init],
     defaultConfiguration: [Function: defaultConfiguration],
     lazyrouter: [Function: lazyrouter],
     handle: [Function: handle],
     use: [Function: use],
     route: [Function: route],
     engine: [Function: engine],
     param: [Function: param],
     set: [Function: set],
     path: [Function: path],
     enabled: [Function: enabled],
     disabled: [Function: disabled],
     enable: [Function: enable],
     ...
```
## npm 相关命令

```
C:\Users\用户名>npm --help

Usage: npm <command>

where <command> is one of:
    access, adduser, audit, bin, bugs, c, cache, ci, cit,
    completion, config, create, ddp, dedupe, deprecate,
    dist-tag, docs, doctor, edit, explore, get, help,
    help-search, hook, i, init, install, install-test, it, link,
    list, ln, login, logout, ls, outdated, owner, pack, ping,
    prefix, profile, prune, publish, rb, rebuild, repo, restart,
    root, run, run-script, s, se, search, set, shrinkwrap, star,
    stars, start, stop, t, team, test, token, tst, un,
    uninstall, unpublish, unstar, up, update, v, version, view,
    whoami

npm <command> -h     quick help on <command>
npm -l           display full usage info
npm help <term>  search for help on <term>
npm help npm     involved overview

Specify configs in the ini-formatted file:
    C:\Users\SN-515\.npmrc
or on the command line via: npm <command> --key value
Config info can be viewed via: npm help config
```
