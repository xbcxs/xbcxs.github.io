
# Java中的双重检查锁

## 单例中的双重检查锁

### 代码示例
```
public class Singleton {

    private volatile static Singleton uniqueSingleton;

    private Singleton() {
    }

    public Singleton getInstance() {
        if (null == uniqueSingleton) {
            synchronized (Singleton.class) {
                if (null == uniqueSingleton) {
                    uniqueSingleton = new Singleton();
                }
            }
        }
        return uniqueSingleton;
    }
}
```
1. 双重检查

执行双重检查是因为，如果多个线程同时了通过了第一次检查，并且其中一个线程首先通过了第二次检查并实例化了对象，那么剩余通过了第一次检查的线程就不会再去实例化对象。这样，除了初始化的时候会出现加锁的情况，后续的所有调用都会避免加锁而直接返回，解决了性能消耗的问题。

2. volatile关键字

需要在uniqueSingleton前加入关键字volatile。使用了volatile关键字后，重排序被禁止，所有的写（write）操作都将发生在读（read）操作之前。

### volatile的使用

实例化对象uniqueSingleton = new Singleton();实际上可以分解成以下三个步骤：

- 分配内存空间  
- 初始化对象  
- 将对象指向刚分配的内存空间  

但是有些编译器为了性能的原因，可能会将第二步和第三步进行重排序，顺序就成了：

- 分配内存空间
- 将对象指向刚分配的内存空间
- 初始化对象

现在考虑重排序后，两个线程发生了以下调用：

Time | Thread A	| Thread B
---|---|---
T1 | 检查到uniqueSingleton为空 |
T2 | 获取锁	
T3 | 再次检查到uniqueSingleton为空	
T4 | 为uniqueSingleton分配内存空间	
T5 | 将uniqueSingleton指向内存空间	
T6 | | 检查到uniqueSingleton不为空
T7 | | 访问uniqueSingleton（此时对象还未完成初始化）
T8 | 初始化uniqueSingleton	

在这种情况下，T7时刻线程B对uniqueSingleton的访问，访问的是一个初始化未完成的对象。

为了解决上述问题，需要在uniqueSingleton前加入关键字volatile。使用了volatile关键字后，重排序被禁止，所有的写（write）操作都将发生在读（read）操作之前。至此，双重检查锁就可以完美工作了。

## 参考
https://www.cnblogs.com/xz816111/p/8470048.html