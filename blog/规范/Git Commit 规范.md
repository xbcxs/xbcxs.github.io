[toc]
# Git Commit 规范
我们在日常使用Git提交代码时经常会写 commint message，否则就不允许提交。一般来说，commit message 应该清晰明了，说明本次提交的目的。

目前，社区有多种 Commit message 的写法规范。本文介绍Angular 规范，这是目前使用最广的写法，比较合理和系统化，并且有配套的工具。

格式化的Commit message 有什么好处？
- 提供更多的历史信息，方便快速浏览。
- 可以过滤某些commit（比如文档改动），便于快速查找信息。
- 可以直接从commit生成Change log。
> Change Log 是发布新版本时，用来说明与上一个版本差异的文档。

## 50/72 Formatting由来

Tim Pope argues for a particular Git commit message style in his blog post: http://www.tpope.net/node/106.

Here is a quick summary of what he recommends:
- First line is 50 characters or less.

- Then a blank line.

- Remaining text should be wrapped at 72 characters.

## Google AnguarJS 规范参考

Commit message包括三个部分：Header，Body 和 Footer，示例如下。
```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```
其中，Header 是必需的，Body 和 Footer 可以省略。不管是哪一个部分，任何一行都不得超过72个字符，其中第一行不超过50个字符。这是为了避免自动换行影响美观。

### Header
Header部分只有一行，包括三个字段：type（必需）、scope（可选）和subject（必需）。

#### type

type用于说明commit的类别，常用的标识如下：
- **feat：新功能（feature）**
- **fix：修补bug**
- docs：文档（documentation）
- style： 格式（不影响代码运行的变动,空格,格式化,等等）
- refactor：重构（即不是新增功能，也不是修改bug的代码变动
- perf: 性能 (提高代码性能的改变)
- test：增加测试或者修改测试
- build: 影响构建系统或外部依赖项的更改(maven,gradle,npm 等等)
- ci: 对CI配置文件和脚本的更改
- chore：对非src和test目录的修改，其他杂项修改。
- revert: Revert a commit

最常用的就是feat合fix两种type；

#### scope

scope用于说明 commit 影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。

#### subject

subject是 commit 目的的简短描述，不超过50个字符。
- 以动词开头，使用第一人称现在时，比如change，而不是changed或changes
- 第一个字母小写
- 结尾不加句号（.）

### Body
Body 部分是对本次 commit 的详细描述，可以分成多行。下面是一个范例。


```
- 增加订单号字段；
- 增加了订单退款接口；
```


有两个注意点：
1. 使用第一人称现在时，比如使用change而不是changed或changes。
2. 应该说明代码变动的动机，以及与以前行为的对比。

### Footer
#### 不兼容变动

如果当前代码与上一个版本不兼容，则 Footer 部分以BREAKING CHANGE开头，后面是对变动的描述、以及变动理由和迁移方法。
#### 关闭Issue
如果当前 commit 针对某个issue，那么可以在 Footer 部分关闭这个 issue 。也可以一次关闭多个 issue 。

```
Closes #123, #245, #992
```
### 特殊类型示例
还有一种特殊情况，如果当前 commit 用于撤销以前的 commit，则必须以revert:开头，后面跟着被撤销 Commit 的 Header。

```
revert: feat(pencil): add 'graphiteWidth' option

This reverts commit 667ecc1654a317a13331b17617d973392f415f02.
```
Body部分的格式是固定的，必须写成This reverts commit &lt;hash>.，其中的hash是被撤销 commit 的 SHA 标识符。

如果当前 commit 与被撤销的 commit，在同一个发布（release）里面，那么它们都不会出现在 Change log 里面。如果两者在不同的发布，那么当前 commit，会出现在 Change log 的Reverts小标题下面。

### 完整示例

```
fix(登录模块): 登录超时BUG

- 修改登录验证逻辑
- 修复登录缓存

BREAKING CHANGE: 代码变动，不兼容老版本

Closes #112，#223
```
## Validate-commit-msg
commitizen工具可以保证自己本地的commit message规范，但是无法保证队友也是规范的，所以需要其他工具来检测队友的提交记录是否规范。使用validate-commit-msg 检查队友的commit message规范

## 生成Change log

如果你的所有Commit都符合Angular格式，那么发布新版本时，Change log（参考使用commitizen工具）就可以用脚本自动生成（例1，例2，例3）。

生成的文档包括以下三个部分。

> **Bug Fixes**
> 
> - glossary:fix bad things in the giossary (9e572e9)
> - itro: fix issue with the intro not working (9d89ds)
> 
> **Features**
> 
> - part1:add better veggie lorem ipsum text (8d9a8f)
> - part2:add musch better lorem ipsum text (3fa332)
> 
> **Breaking Change**s
> 
> - xxxxxx
> - yyyyyy

## IDEA Git Commit Template插件
如果使用 IDEA 开发，我们可以先装个插件： Git Commit Template装完之后重启IDEA，如果我们提交代码会发现多了一个按钮，点击之后就会出现一个 Commit Template，主要分三个部分： Header， Body，Footer 类似上面讲到的模板格式。

## 参考
1. https://stackoverflow.com/questions/2290016/git-commit-messages-50-72-formatting
2. https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html
3. http://www.ruanyifeng.com/blog/2016/01/commit_message_change_log.html
4. https://blog.csdn.net/noaman_wgs/article/details/103429171?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight