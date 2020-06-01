# Java Exception理解与运用
## 引言
本文简单分析在开发过程中如何使用异常机制更合理。
## 异常分类

```
Throwable
    Error
        VirtualMachineError 虚拟机错误
        OutOfMemoryError 内存溢出
        ThreadDeath 线程死锁
    Exception
        RuntimeException 不受检测异常
            NullPointerException
            ArrayIndexOutOfBoundsException
            ArithmeticExeption
            ClassCasException
            
        IO异常 受检测异常
        
        SQL异常 受检测异常
```

## 代码示例

```
    public void test1() throws Exception  {
        throw new Exception("模拟Exception");
        System.out.println("异常后"); // 编译错误，「无法访问的语句」
    }

    public void test2() throws Exception  {
        throw new RuntimeException("模拟RuntimeException");
        System.out.println("todo"); // 编译错误，「无法访问的语句」
    }

    public void test3() {
        int a = 1, b = 0;
        a = a / b; // 分母为零，运行时异常
        System.out.println("todo"); // 抛出异常，不会执行
    }

    public void test4() {
        try {
            // 异常代码
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("todo"); // 可以执行
    }

    public void test5(int a) {
        if(a > 0) {
            throw new RuntimeException("参数越界");
        }
        System.out.println("todo"); // 编译通过，如果a > 0本行不会执行
    }

    public void test6(int a) throws Exception {
        if(a > 0) {
            throw new Exception("参数越界");
        }
        System.out.println("todo"); // 编译通过，如果a > 0本行不会执行
    }
```


1. 代码前有throw new Exception()抛出异常，并且这个异常没有被捕获，这段代码将产生编译时错误。
2. 代码前有throw new Exception()抛出异常，并且这个异常被try...catch所捕获，若此时catch语句中没有抛出新的异常，则这段代码能够被执行。


## 理解与运用

异常主要用于表达代码执行过程中出现了正常预期结果范围外的情况。  
执行方法对正常结果返回与异常报错需要区分开来提供给上游调用者。

### 运用建议

1. 异常避免用于业务逻辑判断；
2. 异常可以按需转换成更好理解的异常抛出；
3. 单线程同步执行的情况下，直接抛出异常，或捕捉异常后做一些类似记录日志的操作后包装异常再抛出；
4. 在统一应用层捕捉处理异常，并给浏览器响应一个友好的错误提示信息；
5. 特殊场景异常需要补偿处理让程序继续执行的可记录异常到日志；
6. 对异常进行文档说明
7. 对抛出的异常进行明确的说明&包装异常时不要抛弃原始异常
8. 使用标准 Java API 提供的异常,若不满足要求，可创建自己的定制异常。
9. 不要记录并抛出异常
10. 不要使用异常控制程序的流程
11. 不要直接catch大段代码，并抛出大异常。
12. 在调用RPC、二方包、或动态生成类的相关方法时，捕捉异常必须使用Throwable类来进行拦截。


### Springboot中自定义顶层异常处理
业务层将异常统一向上报出，由顶层拦截器统一处理异常。

```
@ControllerAdvice
public class ExceptionAdvice {

    private Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({CheckedException.class,})
    @ResponseBody
    public void handException(HttpServletResponse response, CheckedException e) {
        e.printStackTrace();
        // 参数信息
        log.error(e.getMessage());
        // 日志记录到磁盘
        log.debug("异常:", e);
        ResponseWriter.writer(response, HttpResult.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler({UncheckedException.class})
    @ResponseBody
    public void handException(HttpServletResponse response, UncheckedException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        log.debug("异常:", e);
        ResponseWriter.writer(response, HttpResult.error(e.getCode(), e.getMessage()));
    }
}
```

### 如何处理正常业务返回与异常返回

**业务预期正常返回**

```
{
    code : 1, // 1表示后台服务数据结果正常。
    msg : "操作成功",
    data : {}
}
```

**程序异常结果返回**
```
{
    code : -1,  // < 0 服务异常类错误，表示后台服务内部出现了异常。
    msg : "服务器异常",
    exception : "xxx异常信息"
}
```

code为1表示请求的服务业务执行正常返回了业务预期的结果，code为-1表示请求的服务业务执行出现了不可预期的异常，无法返回预期的业务结果。
