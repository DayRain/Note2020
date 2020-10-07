# 一、Springboot简介

## 1、简介

SpringBoot用来简化Spring应用开发，约定大于配置，去繁从简。

J2EE一站式解决方案

## 2、优点

- 快速创建独立运行的Spring项目以及与主流框架继承
- 使用嵌入式的Servlet容器，无需打包成WAR
- starters自动依赖与版本控制
- 大量的自动配置，简化开发，可以修改默认值
- 无需配置XML，无代码生成，开箱即用
- 准生产环境的运行时应用监控
- 与云计算的天然集成

## 3、微服务

是一种架构风格

一个应用应该是一组小型服务；可以通过Htpp的方式互通

每个功能元素最终都是一个可独立替换和独立升级的软件单元

# 二、SpringBoot HelloWorld

## 1、配置好maven

在setting.xml下的profiles节点加

```
	<profile>
	           <id>jdk-1.8</id>
			   <activation>
			       <activeByDefault>true</activeByDefault>
			       <jdk>1.8</jdk>				   
			   </activation>
			   <properties>
			       <maven.compiler.source>1.8</maven.compiler.source>
				   <maven.compiler.target>1.8</maven.compiler.source>
				   <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
			   </properties>
			   
	</profile>
```



## 2、添加依赖

```
 <!-- Inherit defaults from Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
    </parent>

    <!-- Add typical dependencies for a web application -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

## 3、编写主程序

```
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //Spring应用启动
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

## 4、Controller,Service

```
@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/hello")
    public String HelloPrint(){
        return "hello......";
    }
}
```

## 5、发布

- maven下的packing
- 得到 jar包（target下，原war包位置）
- 命令行输入  （ java  +  -jar  +  jar包名 ）  直接部署

# 三、HelloWorld相关代码解释

## 1、父项目和启动器

```
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
    </parent>

    <!-- Add typical dependencies for a web application -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

无需指定依赖版本

## 2、主类介绍

```
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //Spring应用启动
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

@SpringBootApplication

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```

​                @SpringBootConfiguration，表示该类是SpringBoot的配置类、

​                @EnableAutoConfiguration，开启自动配置功能。以前配置的东西，该注解自动配置。

​                                 @EnableAutoConfiguration由一下标签组成。

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

​                                   @Import({AutoConfigurationImportSelector.class}) 是Spring底层的注解，用来引入配置类。

将配置类（@SpringBootApplication标注的类）所在的包以及下面所有的组件扫描到Spring容器；

​               @Import({AutoConfigurationImportSelector.class})给容器导入组件？{AutoConfigurationImportSelector.class可以导入哪些组件？将所需要导入的组件以全类名的方式返回；这些组件就会被添加到容器当中。会给容器导入非常多的自动配置类（xxxAutoConfiguration）：这就是给容器导入所需要的组件，并且配置好这些组件。

四、SpringBoot Initialize

1、@RestController

@RestController= @ResponseBody+@Controller

```
//@ResponseBody
//@Controller
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello spring initialize";
    }
}
```

2、initialize生成的项目目录结构

- 主程序已生成好
- static放一些静态文件
- template保存所有模板页面：（springboot默认jar包使用嵌入式的tomcat，不支持jsp）；可以使用模板引擎（freemaker，thymeleaf）
- application.properties 配置文件，例如该端口，可以添加 server.port=端口。

# 四、、配置文件

## 1、固定名称：

​         application.properties

​         application.yml

YAML是"YAML Ain't a Markup Language"（YAML不是一种置标语言）的递归缩写。

以前的配置文件，一般用xml。yaml以数据为中心，比xml和json更适合做配置文件。

对比：
yml格式的文件

```
server:
  port: 8081
```

xml格式的文件，可能会这么写

```
<server>
       <port></port> 
</server>
```

## 2、yaml语法

（1）基本语法

k：（空格）v  必须有空格，表示一对键值对。

以空格的缩进来表示同一层级关系；只要是左对齐的一列数据，都是同一层级的。

```ymal
server:
    port: 8081
    path: /hello
```

属性和值大小写敏感

（2）值的写法

字面量：普通的值（数字，字符串，布尔）

​          k:v  字面值直接写

​               字符串默认不加上单引号或者双引号

​               “”：双引号：不会转移字符串里的特殊字符；特殊字符会作为本来想表示的意思

​                             name：“张三  \n list”：输出：zhangsan 换行 list

​            ‘’：单引号相反

​                            name :  '张三  \n list'   输出： 张三  \n list

对象：Map（属性和值）（键值对）

​        k:v       对象还是以kv的方式

```
                friends:

​                            lastName: zhangsan

​                            age: 18
```

行内写法

```
                friends: {lastName：张三，age：18}
```

数组（List，Set）：  

​       用  -值表示数组中的一个元素

```
pets:
- cat
- dog
- pig
```

行内写法

```
pets：【cat，dog，pig】
```

## 3、yaml使用实例（配置文件注入）

​            加如依赖   配置文件处理器

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

   配置文件：application.yml

```
person:
  name: zhangsan
  age: 18
  boos: true
  birth: 2017/12/25
  maps:
    k1: v1
    ke: v2
  lists:
    - lisi
    - wangwu
  dog:
    name: 小黑
    age: 12
```

对应javabean

```
 private String name;
    private Integer age;
    private Boolean boos;
    private Date birth;

    private Map<String,Object>maps;
    private List<Object>lists;
    private Dog dog;
```

javabean要添加注解、

```
@ConfigurationProperties(prefix = "person")
@Component
public class Person {
```

测试

```
package com.ph.project2;

import com.ph.project2.pojo.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *     springBoot单元测试，可以在测试的时候，方便的进行像在编码时候的注入
 *
 */
@RunWith(SpringRunner.class)       //表示用springboot的驱动类
@SpringBootTest
public class Project2ApplicationTests {

    @Autowired
    Person person;
    @Test
    public void contextLoads() {
        System.out.println(person);
    }

}
```

## 4、properties配置实例

其他同上

```
person.name=lisi
person.age=18
person.boos=false
person.birth=1997/12/25
person.maps.k1=v1
person.maps.k2=v2
person.lists=1,b,c
person.dog.name=haha
person.dog.age=18
```

## 5、配置文件中文乱码问题

File——settings——file encoding——utf-8

## 6、@Value获取值和@ConfigurationProperties区别

|                      | @Value     | @ConfigurationProperties |
| -------------------- | ---------- | ------------------------ |
| 功能                 | 一个个指定 | 批量注入配置文件中的属性 |
| 松散绑定（松散语法） | 不支持     | 支持                     |
| SpEL（表达式语言）   | 支持       | 不支持                   |
| JSR303数据校验       | 不支持     | 支持                     |
| 复杂类型封装         | 不支持     | 支持                     |

@Value使用案例

```
@Component
public class Person {
    @Value("${person.name}")
    private String name;
    @Value("#{2*3}")
    private Integer age;
```

JSR303数据校验

```
@ConfigurationProperties(prefix = "person")
@Component
@Validated
public class Person {
    @Email //邮箱校验，如果不是邮箱格式会报错
    private String name;
```

什么时候使用@Value？在业务逻辑中获取一下配置文件中的值，就可以用@Value

```

@RestController
public class HelloWorldValueController {
    @Value("${person.name}")
    String name;
    @RequestMapping("/helloValue")
    public String hello(){
        return name;
    }
}
```

如果说专门编写一个JavaBean来个配置文件映射，可以用@ConfigurationProperties

## 7、@PropertySource 和  @ImportSource

@PropertySource,可以加载指定目录下的配置文件

```
@PropertySource(value = {"classpath:person.properties"})
@ConfigurationProperties(prefix = "person")
@Component
@Validated
public class Person {
  //  @Email //邮箱校验，如果不是邮箱格式会报错
    private String name;
    private Integer age;
    private Boolean boos;
    private Date birth;
```

 @ImportSource，可以导入Spring的配置文件

SpringBoot中没有Spring的配置文件，自己写的Spring配置文件也不熊被自动加载，必须通过标签来添加。

```
@ImportResource(locations = {"classpath:beans.xml"})
@SpringBootApplication
public class Project2Application {
    public static void main(String[] args) {
        SpringApplication.run(Project2Application.class, args);
    }
}
```

但一般不用以上的方式，SpringBoot推荐使用全注解的方式给容器添加组件

```
@Configuration
public class AppConfig {

    //通过该方法添加组件，而组件的id就是方法名
    @Bean
    public HelloService helloservice(){
        return new HelloService();
    }
}
```

## 8、配置文件占位符

1、随机数

```
${random.value} ${random.int} ${random.long}
${random.int(10)}   ${random.int[1024,65536]}
```

2、占位符获取前配置的值，如果没有可以用：指定默认值

```
person.dog.name=${person.hello:hello}_dog
```

## 9、Profile

1、多Profile文件

我们在主配置文件编写的时候，文件名可以是 application-(profile).properties/yml

默认使用applicaiton.properties

2、yml支持多文档块方式

```yaml
spring:
   profiles:
      active: prod
---
server:
   port: 8083
spring:
   profiles: dev
---
server:
   port: 8084
spring:
   profiles: prod
   
```



3、激活指定profile

​        1、在配置文件中指定 spring.profile.active=dev

​        2、命令行

```
--spring.profile.active=dev
```

​       3、虚拟机参数

```
-Dspring.profiles.active=dev
```

## 10、配置文件加载问题

```
- file:./config/          file指的是根目录（和 src并列）
- file:./
-classpath:/config/
-classpath:/
优先级从高到低
也可以通过spring.config.location 来改变默认位置
如果相同的配置，高优先级覆盖低优先级，如果不同的配置，则会互补
```

11、外部配置加载顺序

看官方文档

# 五、日志框架

## 1、常用日志框架

JUL，JCL，Jboss-logging，logback，log4j，log4j2，slf4j。。。

| 日志门面（日志的抽象层）                                     | 日志实现                                    |
| ------------------------------------------------------------ | ------------------------------------------- |
| JCL（jakarta Commons Logging） SLF4j（SImple Logging Facade for java） jboss-logging | Log4j JUL(java.util.logging) log4j2 Logback |

SpringBoot:底层是Spring框架，Spring框架默认使用JCL；

SpringBoot选用SLF4j 和logBack；

## 2、SLF4j使用

1、如何在系统中使用SLF4j

以后将开发的时候，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里的方法·。

系统导入slf4j的jar和 logback的实现jar

```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

## 3、遗留问题

当其他组件所使用的日志不统一时（Mybatis，Spring Hibernate）

如何将系统中的所有的日志统一到slf4j

1、将系统的其他日志框架先排除出去；

2、用中间包来替换原有的日志框架

3、我们导入slf4j的其他的实现

4、如果要引入其他的框架？一定要把这个框架的默认日志依赖移除掉

## 4、日志使用

1、默认配置

SpringBoot默认已经配置好了

![1554606854472](C:\Users\13760\Desktop\笔记\img\1554606854472.png)

![1554606947534](C:\Users\13760\Desktop\笔记\img\1554606947534.png)

# 六、Web开发

## 1、使用SpringBoot：

1）、常见Springboot应用，选中需要的模块。

2）、SpringBoot已经默认将这些场景配置好，只要需要在配置文件中指定少了配置即可运行。

3）、自己写业务逻辑

## 2、SpringBoot对静态

资源的映射规则

```
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

    }
}
```

## 1）、所有 /web/jars/**，都去 classpath:/META-INF/resources/webjars/找资源；

webjars：以jar包的方式引入静态资源：

<https://www.webjars.org/>

```
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.3.1-2</version>
</dependency>
```

此包结构

![1554609738503](C:\Users\13760\Desktop\笔记\img\1554609738503.png)

访问时：<http://localhost:8080/webjars/jquery/3.3.1-2/jquery.js>

## 2)、“/**”访问当前项目的任何资源，静态资源只要放在以下文件夹里，都可以被访问到。。

```
“classpath:/META-INF/resources/”,
"classpath:/resources/",
"classpath:/static/".
"classpath:/public/",
"/"表示项目的根目录（resources）
```

localhost:8080/abc == 去静态资源文件夹里找abc

## 3）、欢迎页：静态资源文件夹下的所有index.html；被“/**”映射

例如输入地址localhost:8080/       找index页面

## 4）、所有的 **/favicon.ico  都是在静态资源文件夹下找；

## 5）更改静态资源默认路径

配置文件下：

```
spring.resources.static-location=classpath:/hello, classpath:/world     //这是一个数组
```

# 七、模板引擎

模板引擎

jsp、velocity、freemarker、Thymeleaf；

1、引入thymeleaf

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```

2、Thymeleaf使用&语法

```
@ConfigurationProperties(
    prefix = "spring.thymeleaf"
)
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";
    private boolean checkTemplate = true;
    private boolean checkTemplateLocation = true;
    private String prefix = "classpath:/templates/";
    private String suffix = ".html";
    private String mode = "HTML";
    //只要把HTML页面放在classpath：/templates/，thymeleaf就能自动渲染
```

3、实例

```
@Controller
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        //自动映射到   classpath:templates/success
        //访问地址    http://localhost:8080/hello
        return "success";
    }
}
```

##      1）、导入thymeleaf的名称空间

```
<html lang="en"  xmlns:th="http://www.thymeleaf.org">
```

##     2）、语法规则

###                            1、简单示例

```
@Controller
public class HelloController {

    @RequestMapping("/hello")
    public String hello(Map<String,Object>map){
        //自动映射到   classpath:templates/success
        //访问地址    http://localhost:8080/hello
        map.put("hello","你好");
        return "success";
    }
}

<!DOCTYPE html>
<html lang="en"  xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>success</title>
</head>
<body>
success！
<!--th:text内的文本将会替代div内的文本-->
<div th:text="${hello}">hello</div>
</body>
</html>
```

###                                  2、th：text改变div内文本内容

​          th:id，th:class  ..............都可以改变原本div的属性

表达式

```
Simple expressions:表达式语法
Variable Expressions: ${...} 
#ctx : the context object. #vars: the context variables. #locale : the context        locale. #request : (only in Web Contexts) the HttpServletRequest object.        #response : (only in Web Contexts) the HttpServletResponse object. #session : (only in Web Contexts) the HttpSession object. #servletContext : (only in Web Contexts) the ServletContext object.


Selection Variable Expressions: *{...}
*使用案例
<div th:object="${session.user}">    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>    <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>    <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>  </div>



Message Expressions: #{...}   获取国际化命名
Link URL Expressions: @{...}   定义URL
Fragment Expressions: ~{...}  片段，引用表达式
Literals
Text literals: 'one text' , 'Another one!' ,…
Number literals: 0 , 34 , 3.0 , 12.3 ,… 
Boolean literals: true , false
Null literal: null Literal tokens: one , sometext , main ,…
Text operations:
String concatenation: + Literal substitutions: |The name is ${name}|
Arithmetic operations:
Binary operators: + , - , * , / , % Minus sign (unary operator): 
Boolean operations:
Binary operators: and , or Boolean negation (unary operator): ! , not
Comparisons and equality:
Comparators: > , < , >= , <= ( gt , lt , ge , le ) Equality operators: == , != ( eq , ne )
Conditional operators:
If-then: (if) ? (then) If-then-else: (if) ? (then) : (else) Default: (value) ?: (defaultvalue)
Special tokens:
Page 17 of 106
No-Operation: _
```

3、使用案例

```
@Controller
public class HelloController {

    @RequestMapping("/hello")
    public String hello(Map<String,Object>map){
        map.put("hello","<h1>你好</h1>");
        map.put("users", Arrays.asList("zhangsan","lisi","wangwu"));
        return "success";
    }
}
```

```
<!DOCTYPE html>
<html lang="en"  xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>success</title>
</head>
<body>
success！
<!--th:text内的文本将会替代div内的文本-->
<div th:text="${hello}">hello</div>
<hr/>
<div th:text="${hello}"></div>
<div th:utext="${hello}"></div>
<hr/>

<!--th:each每次便利都会生成当前这个标签-->
<h4 th:text="${users}" th:each="user:${users}"></h4>
<hr/>
<h4>
    <span th:each="user:${users}">[[${user}]]</span>
</h4>
</body>
</html>
```

运行结果：

![1554636844255](C:\Users\13760\Desktop\笔记\img\1554636844255.png)

# 八、Restful crud

## 1）、默认访问首页

## 2）、国际化

- 编写国际化配置文件
- 使用ResourceBundleMessageSource管理国际化资源文件
- 在页面使用fmt:message取出国际化内容

步骤：

1）、编写国际化配置文件，抽取页面需要显示的国际化消息

![1554702094964](C:\Users\13760\Desktop\笔记\img\1554702094964.png)

2）配置文件xie

```
spring.messages.basename=i18n.login

```

3）页面编写    用 #{}标签

```
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<title>Signin Template for Bootstrap</title>
		<!-- Bootstrap core CSS -->
		<link href="asserts/css/bootstrap.min.css" th:href="@{/webjars/bootstrap/4.0.0/css/bootstrap.min.css}" rel="stylesheet">
		<!-- Custom styles for this template -->
		<link href="asserts/css/signin.css" th:href="@{/asserts/css/signin.css}" rel="stylesheet">
	</head>

	<body class="text-center">
		<form class="form-signin" action="dashboard.html">
			<img class="mb-4" th:href="@{/asserts/img/bootstrap-solid.svg}" src="asserts/img/bootstrap-solid.svg" alt="" width="72" height="72">
			<h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
			<label class="sr-only" th:text="#{login_username}">Username</label>
			<input type="text" class="form-control" th:placeholder="#{login_username}" placeholder="Username" required="" autofocus="">
			<label class="sr-only" th:text="#{login.password}">Password</label>
			<input type="password" class="form-control" placeholder="Password" th:placeholder="#{login.password}" required="">
			<div class="checkbox mb-3">
				<label>
          <input type="checkbox" value="remember-me"> [[#{login.remember}]]
        </label>
			</div>
			<button class="btn btn-lg btn-primary btn-block" type="submit" th:text="#{login.btn}">Sign in</button>
			<p class="mt-5 mb-3 text-muted">© 2017-2018</p>
			<a class="btn btn-sm">中文</a>
			<a class="btn btn-sm">English</a>
		</form>

	</body>

</html>
```

## 3）、登录

开发期间模板引擎修改后，要实时生效

 1 ）、禁用模板引擎的缓存

```
spring.thymeleaf.cache=false
```

2）、页面修改完后ctrl+f9；重新编译；

3）、登录错误消息的显示

```
<p style="color: red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}"></p>
```

4）、防止表单的重复提交

使用重定向的方式，可避免表单的重复提交

```
if(!StringUtils.isEmpty(username) && "123456".equals(password)){
            return "redirect:/main.html";
        }else{
            maps.put("msg","密码错误");
            return "index";
        }
        
        
 配置类要写上
 registry.addViewController("/main.html").setViewName("dashboard");
```

配置类：

```
@Configuration
public class MyMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        //输入hello，跳转到success页面
        registry.addViewController("/hello").setViewName("success");
        registry.addViewController("/index").setViewName("forward:index.html");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/main.html").setViewName("dashboard");
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
    }

    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }
}
```

## 4）、拦截器

两个地方

```
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("username");
        if(user == null){
            request.setAttribute("msg","尚未登录，请先登录");
            request.getRequestDispatcher("/index.html").forward(request,response);
        }else{
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

```

以及配置类添加方法

```
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/index","/index.html","/login","/user/login","/webjars/**","/asserts/**");
    }
```

## 5）、CRUD-员工列表

实验要求：

1）、RestfulCRUD：CRUD满足Rest风格

URI:  /资源名称/资源标识       HTTP请求方式区分对资源CRUD操作

|      | 普通CRUD（uri来区分操作） | RestfulCRUD     |
| ---- | ------------------------- | --------------- |
| 查询 | getEmp                    | emp-GET         |
| 添加 | addEmp?XXX                | emp-POST        |
| 修改 | updateEmp?i=xx&xxx=xx     | emp(id)-PUT     |
| 删除 | deleteEmp?id = 1          | emp/{id}-DELETE |

2)、实验的请求架构

|                                  | 请求URI | 请求方式 |
| -------------------------------- | ------- | -------- |
| 查询所有员工                     | emps    | GET      |
| 查询某个员工                     | emp/1   | GET      |
| 来到添加页面                     | emp     | GET      |
| 添加员工                         | emp     | POST     |
| 来到修改页面（查出员工信息回显） | emp/1   | GET      |
| 修改员工                         | emp     | POST     |
| 删除员工                         | emp/1   | DELETE   |

3）、员工列表

thymeleaf公共页面元素抽取

```
1、抽取公共片段
<div th:fragment="copy"> 
         &copy; 2011 The Good Thymes Virtual Grocery
</div> 
2、引入公共片段
 <div th:insert="~{footer :: copy}"></div> 
  ~{templatename::selector}   模板名：：选择器
  ~{templatename::fragmentname}   模板名：：片段名
3、默认效果：
insert的功能片段在div标签中
如果使用th:insert等属性引入，则可以不用写~（）
行内写法可以加上[[~()]],[(~())]
```

三种引入功能片段的th属性：

th:insert：将公共片段整个插入到声明引入的元素中

th:replace：将申明引入的元素替换成公共片段

th:include：将被引入的片段的内容包含进这个标签中



```
<footer th:fragment="copy">
      &copy; 2011 The Good Thymes Virtual Grocery
</footer>
引入方式
<body>
      ...
      <div th:insert="footer :: copy"></div>
      <div th:replace="footer :: copy"></div>
      <div th:include="footer :: copy"></div>
</body>
效果：
<body>
  ...
      <div>  
          <footer>   
              &copy; 2011 The Good Thymes Virtual Grocery 
          </footer>
      </div>
      
      <footer>   
          &copy; 2011 The Good Thymes Virtual Grocery  
      </footer>
      
      <div>    
          &copy; 2011 The Good Thymes Virtual Grocery 
      </div>
  </body>

```

引入片段的时候传入参数

6）、表单发送put请求

1、SpringMVC中配置HiddenHttpMethodFilter（SpringBoot会自动配置）

2、页面创建一个post表单

3、创建一个input项，name="_method"；值就是我们指定的请求方式