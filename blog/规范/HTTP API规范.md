# HTTP API规范

## 简介
基于Restful进行定制补充形成的HTTP API试用规范

## 使用场景
HTTP/JSON调用

## HTTP API规范
> 参照restful等规约，根据团队情况定制了自用风格。  

### URL命名规范

示例：/服务标识/接口类型/{版本}/顶级命名空间/../子级命名空间/资源[/动作-结果等描述补充]?paramsKey=value

eg：https://www.exemple.com/api/{v}/library/book[/page-list]

1. 全部小写。
2. 用中杠-不用下杠_。
3. 参数列表要encode。
4. URI中的名词表示资源集合。没有[/动作-结果等描述补充复数]时资源用复数表示，否则单数。
5. 命名空间描述：单个名词不满足描述的情况下可多词连写（牺牲一定可阅读性）。eg.mywork  
6. [/动作-结果等描述补充]描述：多词用-连接（保证可阅读性）。例如：page-list

**命名示例**

1. 获取书籍列表 GET /api/v1/book/list `行参数：查询参数`
2. 获取书籍分页 GET /api/v1/book/page-list `行参数：size和page为分页参数`
3. 获取书籍详情 GET /api/v1/book/info?id= `行参数：id为主键`
4. 新增书籍资源 POST /api/v1/book/save `body参数：主键之外的参数`
5. 新增返回结果 POST /api/v1/book/back-save `body参数：主键之外的参数`
6. 批量新增书籍 POST /api/v1/book/batch-save `body参数：主键之外的参数`
7. 修改书籍资源 PUT  /api/v1/book/update `body参数：更新依据主键id,以及其他参数`
8. 批量修改书籍 PUT  /api/v1/book/batch-edit `body参数：更新依据主键 id,以及其他参数`
9. 删除书籍资源 DELETE /api/v1/book/delete `body参数：主键id`
10. 批量删除书籍 DELETE /api/v1/book/batch-delete `body参数：主键id`

### 请求规范
1. 请求Request Headers  
普通数据:Content-Type:application/json;charset=UTF-8  
文件数据:Content-Type:multipart/form-data
2. 响应Response Headers  
普通数据：Content-Type:application/json;charset=UTF-8
3. URI的末尾不要添加“/”，多一个斜杠，语义完全不同，究竟是目录，还是资源。 

### 数据格式规范
1.基本响应格式

```
{
    code: 200,
    message: "success",
    data: {
    }
}
```

2.分页响应格式
```
{
    code: 200,
    message: "success",
    data: {
        recordCount: 2,
        totalCount: 2,
        pageNo: 1,
        pageSize: 10,
        list: [
            {
                id: 1,
                name: "XXX",
                code: "H001"
            },
            {
                id: 2,
                name: "XXX",
                code: "H001"
            } ],
        totalPage: 1
    }
}
```
### 响应状态规范
**HTTP Status 请求状态**
1. HTTP: Status 200 请求成功
2. HTTP: Status 500 服务响应异常

**Code 数据状态**
1. code: 1 数据操作成功。
2. code: 0 数据操作失败。
3. code: <0(范围) 数据操作失败--具体数据校验失败。

**Message**  
对应code的消息描述

**Data**  
操作成功(code=200)的数据

### 版本管理
**格式：** https://www.example.com/api/{v}  
1. 应该将API的版本号放入URL。
2. 采用多版本并存，增量发布的方式。
3. n代表版本号，分为整型和浮点型  
   整型：大功能版本，如v1、v2、v3 ...  
   浮点型：补充功能版本，如v1.1、v1.2、v2.1、v2.2 ...
4. 对于一个 API 或服务，应在生产中最多保留 3 个最详细的版本

### 幂等性
一个操作，不论执行多少次，产生的效果和返回的结果都是一样。

# 参考
[1] https://mp.weixin.qq.com/s?__biz=MzIzMzgxOTQ5NA==&mid=2247489981&idx=2&sn=963b1fb0184d2a689c7eefdfab16338d&chksm=e8fe85b4df890ca23345be6c56ff1e7468b440e7b58a06a7fca37ea6a0d849e6e0cc750eed27&mpshare=1&scene=1&srcid=&sharer_sharetime=1576476661332&sharer_shareid=c38dcf31fd198b2a05ce849f329d73ed&exportkey=A5uyEyMAkmMuZMfizymrl4E%3D&pass_ticket=vFdSBAWnKHWmT5TIgML%2F89g6dcPfsSr1a1CXtzuxa85a5hNz6b5hrlHWReLxP2rB#rd  
[2] https://www.jianshu.com/p/3f8953f73a79