
# Spring几种事件机制

## Spring Event
事件驱动模型包括：事件定义、监听、发布。

事件定义：继承ApplicationEvent类来实现事件源。  
事件监听：通过实现ApplicationListener接口来自定义自己的监听器。  
事件发布：通过调用ApplicationContext的publishEvent方法去发布事件。

事件定义

```
public class EmailEvent extends ApplicationEvent  {

    public EmailEvent(Message source) {
        super(source);
    }
}
```

事件监听

```
@Component
public class FaceEventListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(EmailEvent event) {
        Message source = (Message)event.getSource();
        // 处理邮件发送.....
    }
}
```

事件发布

```
@Service
public class PublishHandler {

    @Autowired
    private ApplicationContext applicationContext;

    public void testHandle(){
        Message mes = new Message();
        mes.content("xxx");
        //发布事件
        applicationContext.publishEvent(new EmailEvent(mes));
    }
}
```

### Spring4.2 @EventListener监听实现

#### 基于注解的监听实现

在spring4.2中我们可以以更加简洁的方式来监听event的发布，监听事件我们不必再实现ApplicationListener接口了，只要在方法上添加注解@EventListener即可

```
@EventListener
public void onApplicationEvent(EmailEvent event) {
    Message source = (Message)event.getSource();
    // 处理邮件发送.....
}

```
该方式根据方法参数类型来自动监听相应事件的发布。

如果要监听多个事件类型的发布，可以在@EventListener(classes = {FaceEvent.class,ArmEvent.class})指定，spring会多次调用此方法来处理多个事件。但是注意此时，方法参数不能有多个，否则会发生转换异常，可以将使用多个事件的父类作为唯一的方法参数来接收处理事件，但除非必要否则并不推荐监听多个事件的发布。

如果有多个监听器监听同一事件，我们可以在方法上使用spring的@order注解来定义多个监听器的顺序，如：

```
@EventListener
@Order(4)
public void onApplicationEvent(EmailEvent event) {
    Message mes = (Message) event.getSource();
    // 处理邮件发送...
}


@EventListener({FaceEvent.class,ArmEvent.class})
@Order(3)
public void onApplicationEvent3(Object event) {

    if(event instanceof EmailEvent){
        Message mes = (Message) event.getSource();
        // 处理邮件发送...
    }else if(event instanceof LogEvent){
        Log log = (Log) event.getSource();
        // 处理日记记录...
    }
}
```
#### 监听事件时的事务隔离
@TransactionalEventListener和@EventListener都可以监听事件，但前者可以对发布事件和监听事件进行一些事务上的隔离。@TransactionalEventListenerr不和发布事件的方法在同一个事务内，发布事件的方法事务结束后才会执行本监听方法，监听逻辑内发生异常不会回滚发布事件方法的事务。

@TransactionalEventListener有一个属性为fallbackExecution，默认为false，指发布事件的方法没有事务控制时，监听器不进行监听事件，此为默认情况！fallbackExecution=true，则指发布事件的方法没有事务控制时，监听方法仍可以监听事件进行处理。

使用@TransactionalEventListenerz在事务结束后执行监听方法上还可以进行细化的控制。它有一个属性为TransactionPhase，默认为TransactionPhase.AFTER_COMMIT，即事务提交后。还可以根据需要选择AFTER_COMPLETION、BEFORE_COMMIT、AFTER_ROLLBACK。但仍需注意，如果fallbackExecution=false，且发布事件的方法没有事务控制时，监听器根本不会监听到事件，此处的TransactionPhase也就没有意义了。

## 参考
[1]https://blog.csdn.net/java_collect/article/details/81156529?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight