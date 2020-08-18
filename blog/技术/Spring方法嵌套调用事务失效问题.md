
# Spring方法嵌套调用事务失效问题

## 场景描述
在同一个类中，一个方法调用另外一个有(eg:@Transational)注解，注解无效。示例代码如下：

```
@Service
public class TestServiceImpl implements TestService{

    @Override
    public void save1(Object object) {
        save2(object);
    }

    @Transactional
    @Override
    public void save2(Object object) {

    }
    
}

@Controller
@RequestMapping("test")
public class TestController {

    @Autowired
    TestService testService;

    @RequestMapping("test")
    public void test() {
        testService.save1(new Object());
    }

}
```
## 原因分析
spring 在扫描bean的时候会扫描方法上是否包含@Transactional注解，如果包含，spring会为这个bean动态地生成一个子类（即代理类，proxy），代理类是继承原来那个bean的。此时，当这个有注解的方法被调用的时候，实际上是由代理类来调用的，代理类在调用之前就会启动transaction。然而，如果这个有注解的方法是被同一个类中的其他方法调用的，那么该方法的调用并没有通过代理类，而是直接通过原来的那个bean，所以就不会启动transaction，我们看到的现象就是@Transactional注解无效。

为什么一个方法a()调用同一个类中另外一个方法b()的时候，b()不是通过代理类来调用的呢？可以看下面的例子（为了简化，用伪代码表示）：

```
@Service
class A{
    @Transactinal
    method b(){...}
    
    method a(){    //标记1
        b();
    }
}
 
//Spring扫描注解后，创建了另外一个代理类，并为有注解的方法插入一个startTransaction()方法：
class proxy$A{
    A objectA = new A();
    method b(){    //标记2
        startTransaction();
        objectA.b();
    }
 
    method a(){    //标记3
        objectA.a();    //由于a()没有注解，所以不会启动transaction，而是直接调用A的实例的a()方法
    }
}

```
当我们调用A的bean的a()方法的时候，也是被proxy$A拦截，执行proxy$A.a()（标记3），然而，由以上代码可知，这时候它调用的是objectA.a()，也就是由原来的bean来调用a()方法了，所以代码跑到了“标记1”。由此可见，“标记2”并没有被执行到，所以startTransaction()方法也没有运行。

## 结论与解决方案

### 结论

在一个Service内部，事务方法之间的嵌套调用，普通方法和事务方法之间的嵌套调用，都不会开启新的事务。

1. spring采用动态代理机制来实现事务控制，而动态代理最终都是要调用原始对象的，而原始对象在去调用方法时，是不会再触发代理了！
2. Spring的事务管理是通过AOP实现的，其AOP的实现对于非final类是通过cglib这种方式，即生成当前类的一个子类作为代理类，然后在调用其下的方法时，会判断这个方法有没有@Transactional注解，如果有的话，则通过动态代理实现事务管理(拦截方法调用，执行事务等切面)。当b()中调用a()时，发现b()上并没有@Transactional注解，所以整个AOP代理过程(事务管理)不会发生。
### 解决方案

1. 把这两个方法分开到不同的类中；
2. 把注解加到类名上面；

## 参考
[1] https://blog.csdn.net/levae1024/article/details/82998386