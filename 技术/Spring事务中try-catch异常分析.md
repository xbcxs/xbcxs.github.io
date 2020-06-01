# Spring事务中try-catch影响分析

## 引言
分析Spring事务中使用try-catch对事务的影响情况。

## 几种场景

### 场景1
若depatDao.save()异常，userDao.save不会回滚。
```
@transactional
public void test1(){
    try {
        userDao.save(user);
        deptDao.save(dept);
    } catch (Exception e) {
    
    }
}
```
此种情况try catch这种把整个包裹起来，这种业务方法也就等于脱离了spring事务的管理，因为没有任何异常会从业务方法中抛出，全被捕获并“吞掉”，导致spring异常抛出触发事务回滚策略失效。

### 场景2
若depatDao.save()异常，userDao.save会回滚，上层需要处理异常。
```
@transactional
public void test2(){
    try {
        userDao.save(user);
        deptDao.save(dept);
    } catch (Exception e) {
        throw new RuntimeException();
    }
}
```
此种情况throw new RuntimeException()会导致test2()事务回滚，上层需要处理异常。

### 场景3 
若depatDao.save()异常，userDao.save回滚，上层不需要处理异常.
```
@transactional
public void test3(){
    try {
        userDao.save(user);
        deptDao.save(dept);
    } catch (Exception e) {
        // 手动回滚
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
```
此种情况TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()手动回滚事务。上层不需要处理异常。

## 总结
1. 避免在业务代码中“吞掉”异常。
2. 要么直接抛出异常，要么捕捉异常后包装便于理解的自定义异常再抛出，由顶层应用层捕捉处理异常，响应给调用方。