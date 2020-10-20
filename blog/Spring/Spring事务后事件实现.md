# Spring事务后事件实现

## 场景
在事务提交后的事件里执行业务逻辑，保持两段业务逻辑串行。主要用于前置业务事务完成后对后续业务发出信号通知。基于spring可以通过以下几种方案实现。

## 基于AOP
spring aop类似一个同心圆，要执行的方法为圆心，最外层的order最小。从最外层按照AOP1、AOP2的顺序依次执行doAround方法，doBefore方法。然后执行method方法，最后按照AOP2、AOP1的顺序依次执行doAfter、doAfterReturn方法。**也就是说对多个AOP来说，先before的，一定后after。**

如果我们要在同一个方法事务提交后执行自己的AOP，那么把事务的AOP order设置为2，自己的AOP order设置为1，然后在doAfterReturn里边处理自己的业务逻辑。

1. 自己的AOP order设置为1
```
@Component  
@Aspect  
@Order(1)  
public class MessageQueueAopAspect1{  
    ...  
}  
```


## 基于TransactionSynchronizationAdapter

```
@Transactional
@Override
public void saveAndNotify(StageMessage stageMessage) {

    stageMessageDao.save(stageMessage);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
        @Override
        public void afterCommit() {
            exchangeBrokerService.dispatchMessage(stageMessage.getTopicId());
        }
    });
}
    
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Override
public void dispatchMessage(String topicId) {

}
```

## 基于Spring事件机制

```
@TransactionalEventListenerr
public void onApplicationEvent(EmailEvent event) {
    Message source = (Message)event.getSource();
    // 处理邮件发送.....
}
```


@TransactionalEventListenerr不和发布事件的方法在同一个事务内，发布事件的方法事务结束后才会执行本监听方法，监听逻辑内发生异常不会回滚发布事件方法的事务。