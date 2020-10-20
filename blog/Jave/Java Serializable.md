# Java Serializable

## 什么是Java序列化
Java序列化就是指把Java对象转换为字节序列的过程
1. 对于实现了Serializable接口的类，可以将其序列化输出至磁盘文件中，同时会将其serialVersionUID输出到文件中。
2. 然后有需要使用时，再从磁盘将对象内容及serialVersionUID读入内容中的某个对象。
3. 将磁盘内容读入对象时，需要进行强制类型转换，如Person person = (Person)ois.readObject(); 
4. 此时，将对比从磁盘读入的Serializable与对象所属类的Serializable，若二者一致，则转换成功。若二者不一致，则转换失败，并抛出InvalidClassException。

## 为什么要定义serialversionUID变量

SerialversionUID 用于标记类（是否变更）。
如果自己声明一个serialVersionUID变量。在序列化后，去添加一个字段，或者方法（不删除原有字段），而不会影响到后期的还原当，最终都可以反序列化成功。

如果没有为类指定serialVersionUID，那么java编译器会自动给这个class进行一个摘要算法，类似于指纹算法，只要这个文件多一个空格，得到的UID就会截然不同；类中的任何变化均会导致serialVersionUID的变化（因为默认的serialVersinUID对于class的细节非常敏感，例如class内容的任何变更）。此时反序列化会出抛出InvalidClassException。

- 自定义SerialversionUID可以让类反序列化时保持对增量变更的兼容。
- 没有为类指定serialVersionUID，默认的serialVersionUID机制会严格校验要求类反序列化后完全一致，否则抛出异常。

## 什么情况下需要序列化   
1. 当你想把的内存中的对象状态保存到一个文件中或者数据库中时候；
2. 当你想用套接字在网络上传送对象的时候；
3. 当你想通过RMI传输对象的时候；
