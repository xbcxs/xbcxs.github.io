
 # Spring3.2升级Springboot2.1.4
 
 ## 排除Sprigboot内置spring-boot-starter-tomcat

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
	<exclusions>
		<exclusion>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
		</exclusion>
	</exclusions>
</dependency>
		
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-tomcat</artifactId>
	<scope>provided</scope>
</dependency>
```

去掉spring-boot-starter-tomcat，从新导入设置scop:provide,解决开发环境JSP依赖，发布环境去掉jar中的JSP依赖，因为外置Tomcat中已存在。

 ## 引入tomcat支持web发布需要的依赖
 
```
<!-- JSP标签 -->
<dependency>
	<groupId>javax.servlet</groupId>
	<artifactId>jstl</artifactId>
</dependency>
<!-- tomcat相关依赖 -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-tomcat</artifactId>
	<scope>provided</scope>
</dependency>
<!-- 对jsp的支持的依赖 -->
<dependency>
	<groupId>org.apache.tomcat.embed</groupId>
	<artifactId>tomcat-embed-jasper</artifactId>
	<scope>provided</scope>
</dependency>
```

## war发布运行

```
springboot tomcat外置适配方案：
public class EapApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        //找到原先的启动类
        return application.sources(EapApplication.class);
    }
    
}
```

## 排除自动装配

```
@SpringBootApplication(exclude = {
    RabbitAutoConfiguration.class,
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
```

先排除后续再调通。

## 解决tomcat8.5以后的版本对manifest中jar依赖的检核报错

tomcat除了加载了我们maven管理的jar包之外，还会对jar中的manifest文件进行分析，如果其中存在classpath，他会将其中的内容也添加jar包依赖中，并对这些jar包进行加载。解决方法：增加一下代码设置不扫描Manifest文件。

```
@Bean
public TomcatServletWebServerFactory tomcatFactory() {
    return new TomcatServletWebServerFactory() {
        @Override
        protected void postProcessContext(Context context) {
            ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
        }
    };
}
```

## sessionFactory 找不到bean异常


```
@Autowired
private EntityManagerFactory entityManagerFactory;

protected SessionFactory getSessionFactory() {
    return entityManagerFactory.unwrap(SessionFactory.class);
}

@Override
public Session getHibernateSession() {
    Session session = null;
    try {
        session = getSessionFactory().getCurrentSession();
    } catch (HibernateException e) {
        log.error(e.getMessage(), e);
    }
    return session;
}
```

## Caused by: org.hibernate.jpa.boot.archive.spi.ArchiveException: Could not build ClassFile

依赖javassit版本过低，解放方法：

```
<!--
<dependency>
    <groupId>javassist</groupId>
	<artifactId>javassist</artifactId>
	<version>3.12.0.GA</version>
</dependency>
-->
```

## hibernate5 getCurrentSession() 需要@Transactional注解在使用的地方才可以

```
@Transactional
@Repository
public class HibernateBaseDaoImpl<T> implements IBaseDao {

}
```

##  Could not obtain transaction-synchronized Session for current thread
1.openSession和getCurrentSession方法的区别  
1）采用openSession方法获取Session示例时,SessionFactory直接创建一个新的Session示例。而getCurrentSession方法创建的Session示例会被绑定到当前线程中。  
2）采用openSession方法创建的Session实例，在使用完成后需要调用close方法进行手动关闭。而getCurrentSeesion方法创建的Session实例则会在提交或者回滚操作时自动关闭。  
3）采用getCurrentSession方法创建Seesion实例时，必须要配置：hibernate.current_session_context_class;

2.必须要将使用了sessionFactory.getCurrentSession()获取session的代码所在的方法加入到事务管理器中；否则获取不到session了。（例如：service中查询方法配置事务只读@Transactional(readonly=true)；或者openSessionInView?;）

3.sessionFactory.getCurrentSession()是要基于事务的，才能实现session生命周期的管理。所以我们查询方法上用个只读事务就ok了。

## AddressListRest初始化bean失败，需要解决SystemContext.getBean()
在SystemContext上添加注解@Component

## Hibrenate5获取session
```
@PersistenceContext
protected EntityManager em;

@Override
public Session getHibernateSession() {
    return em.unwrap( Session.class );
}
```
不再使用getCurrentSession()

## session.beginTransaction()问题
问题定位原因：

// 在hibernate3.6中，该代码含义表示：如果已经处于事务中则打开当前事务
transaction = session.beginTransaction();

// 在hibernate5+中，该代码含义表示：如果已经处于事务中则打开一个新事务，这样就导致了一个事务未关闭又打开了新的事务，出现了Transaction already active异常，导致数据库连连接不能及时释放，连接耗尽，后续服务不可用。
代码改进为hibernate5中新方式：transaction = session.getTransaction();

## hibernate5.2 could not find setter for rownum_
hibernate原生sql封装，报错信息：could not find setter for rownum_

修改方式增加addScalar
```
.addScalar("需要的字段")
.setResultTransformer(Transformers.aliasToBean(XXXDTO.class))
```


## 不同系统，不同发布方式资源读取问题

Springboot对资源文件读取相对于tager/calsses,而传统非Springboot项目WEB相对于tager/0.0.1-SNAPSHOT/WEB-INF/classes
```
/**
     * 得到classpath路径
     * 可用于windows和Linux
     * 不可用于jar工程
     *
     * @return
     */
    public static String getClasspath() {
        try {
            return new ClassPathResource("").getFile().getPath();
        } catch (IOException e) {
            throw new UncheckedException(e.getMessage());
        }
    }

    /**
     * 得到classpath流
     * 可用于windows和Linux
     * 可用jar,bin,webroot工程环境
     *
     * @return
     */
    public static InputStream getClasspathStream() {
        try {
            return new ClassPathResource("").getInputStream();
        } catch (IOException e) {
            throw new UncheckedException(e.getMessage());
        }
    }

    /**
     * 根据文件的classpath相对路径得到文件流
     * 可用于windows和Linux
     * 可用jar,bin,webroot工程环境
     *
     * @return
     */
    public static InputStream getClasspath(String relativeFilePath) {
        try {
            return new ClassPathResource(relativeFilePath).getInputStream();
        } catch (IOException e) {
            throw new UncheckedException(relativeFilePath + "not found ！");
        }
    }
```

## OpenSessionInViewFilter

application.properties

```
spring.jpa.open-in-view=true
```

## 数据库配置

@Bean
DataSource dataSource() {
    HikariConfig config = new HikariConfig("/db.properties");
    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;

}

db.properties

## sitemesh 问题
引入3.0.1 sitemesh jar，删除旧的2.4

注册过滤器WebConfig.java,配置过滤器拦截WebSiteMeshFilter.java

```
@Configuration
public class SiteMeshConfigurer {

    @Bean
    public FilterRegistrationBean siteMeshFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        SiteMeshExtendFilter siteMeshFilter = new SiteMeshExtendFilter();
        filter.setFilter(siteMeshFilter);
        return filter;
    }

    class SiteMeshExtendFilter extends ConfigurableSiteMeshFilter {

        @Override
        protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
            builder.addDecoratorPath("*.sitemesh", "/uiframe/jsp/frame.jsp")
                    .addDecoratorPath("*.lightmesh", "/uiframe/jsp/lightframe.jsp")
                    .addDecoratorPath("*.simpleLightmesh", "/uiframe/jsp/simpleLightframe.jsp")
                    .addDecoratorPath("*.winlightmesh", "/uiframe/jsp/winlightframe.jsp");
        }
    }
}
```
删除sitemesh2中URL<%@ uri="http://www.opensymphony.com/sitemesh/page" %>引入报错。

修正title、body、head相关的书写规范。

```
<sitemesh:write property='title'/>
<sitemesh:write property='body'/>
<sitemesh:write property='head'/>
```


 
 
## develop-tools工具导致a canot cast to a

去掉pom中develop-tools。




## web.xml错误页面配置

```
@Controller
public class ErrorPageController implements ErrorController {
   
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 500) {
            return "/WEB-INF/error/500";
        } else if (statusCode == 404) {
            return "/WEB-INF/error/404";
        } else if (statusCode == 403) {
            return "/WEB-INF/error/403";
        } else if (statusCode == 401) {
           return "/WEB-INF/error/401";
        } else {
            return "/WEB-INF/error/500";
        }

    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
```
## web.xml首页配置

```
@Configuration
public class WebMvcExtendConfigurer implements WebMvcConfigurer {

    /**
     * 首页跳转配置
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController( "/" ).setViewName( "forward:uiframe/jsp/login.jsp" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }
    
}
```


## 兼容后缀和路径两种访问模式

```
@Configuration
public class WebMvcExtendConfigurer implements WebMvcConfigurer {

    /**
     * 开启路径后缀匹配
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 后缀模式匹配
        configurer.setUseRegisteredSuffixPatternMatch(true);
        // 后缀路径模式匹配
        configurer.setUseTrailingSlashMatch(true);
    }

}
```

## spring XML相关配置
删除相关配置

配置中相关功能改用其他方式实现，例如定时器改用注解。

## webservices

<!-- 引入 cxf webservice  -->
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
            <version>3.2.4</version>
        </dependency>


<!-- 去掉注解XmlSchema冲突 -->

```
<dependency>
    <groupId>org.apache.woden</groupId>
    <artifactId>woden-api</artifactId>
    <version>1.0M8</version>
    <exclusions>
        <exclusion>
            <groupId>org.apache.ws.commons.schema</groupId>
            <artifactId>XmlSchema</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!--
<dependency>
    <groupId>org.apache.ws.commons.schema</groupId>
    <artifactId>XmlSchema</artifactId>
    <version>1.4.3</version>
</dependency>
-->
```
继续解决：class "javax.wsdl.WSDLElement"'s signer information does not match signer information of other classes in the same package
注解改包

```
<!--
<dependency>
    <groupId>org.eclipse.birt.runtime.3_7_1</groupId>
    <artifactId>javax.wsdl</artifactId>
    <version>1.5.1</version>
</dependency>
-->
```

后续继续剔除 axis、axis2依赖，涉及代码修改
 
```
<!-- 
<dependency>
    <groupId>org.apache.axis</groupId>
    <artifactId>axis</artifactId>
    <version>1.4</version>
</dependency>

<dependency>
    <groupId>org.apache.axis2</groupId>
    <artifactId>axis2-transport-http</artifactId>
    <version>1.7.9</version>
</dependency>
-->
```
## 页面标签
Springboot2，对页面标签检查更严格，标签对需要准确闭合。

## 大文件上传失败配置
application.properties添加配置

```
#multipart是否开启
spring.servlet.multipart.enabled=true
#单个文件最大大小
spring.servlet.multipart.max-file-size=1024MB
#所有上传文件最大大小
spring.servlet.multipart.max-request-size=10240MB
```
## 小文件上传临时文件获取不到

```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartConfigurer {

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // 设定文件上传的最大值为5MB，5*1024*1024
        multipartResolver.setMaxUploadSize(1024 * 1024 * 1024);
        // 设定文件上传时写入内存的最大值，如果小于这个参数不会生成临时文件，默认为10240
        multipartResolver.setMaxInMemorySize(10);
        return multipartResolver;
    }

}
```
## JAVA实体与库表对应

Springboot2-jpa中实体未明确指明对应列，按照默认解析驼峰转成下划线，解析错误

修改前

```
@Column
public String getInstanceProcessId() {
    return instanceProcessId;
}
```

Column注解未标明数据库的列名，hibernate解析时驼峰解析成下划线，执行SQL时，出现“标识符无效”的问题。

如下修改后，问题解决


```
@Column(name = "INSTANCEPROCESSID")
public String getInstanceProcessId() {
    return instanceProcessId;
}
```

## AopContext获取
类内部方法调用时，将方法放入当前代理类中,AopContext.currentProxy()获取失败

添加EnableAspectJAutoProxy注解

```
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
// TODO 当前代理可见，使发送消息AOP后事件生效.MessageServiceImpl.sendMessage()调用时在用
```

## Fastjson转换bean异常

无法格式化debug里未查询出来的bean属性（但System.out.println()可以打印出来）。

解决方式：修复方式升级fastjson版本

原版本：
```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.3</version>
</dependency>
```

升级后：

```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.68</version>
</dependency>
```
## SpringMvc框架下UriComponentsBuilder版本变更影响
SpringMvc框架使用的jar版本spring-web-3.1.0.RELEASE.jar

SpringBoot框架使用的jar版本spring-web-5.1.6.RELEASE.jar

UriComponentsBuilder.fromPath()方法逻辑变更：  
旧版本jar中支持//  
新版本jar中将连续的/全部转成单个/，导致把原来 http:// 转换成了 http:/  
        

## 其他组件接入修复
### Flyway
- 配置兼容Springboot版本的Flyway
- 配置集群模式
> Flyway使用数据库锁机制（locking technology of your database）来协调多个节点，从而保证多套应用程序可同时执行migration。

### profile 修复
Maven之profile与Spring boot 的 profile灵活选择使用
### 注册中心/配置中心接入。
将配置中心数据对接到Environment
### 注解定时器处理恢复。
Quartz集群配置
> 一个Quartz集群中的每个节点是一个独立的Quartz应用，它又管理着其他的节点。这就意味着你必须对每个节点分别启动或停止。Quartz集群中，独立的Quartz节点并不与另一其的节点或是管理节点通信，而是通过相同的数据库表来感知到另一Quartz应用的。

### 各类chache适配恢复。

## 剔除冗余依赖