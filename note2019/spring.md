# 一、spring简介

目的：使java ee开发更加容易的框架

涉及知识：java、反射、xml、xml解析、代理、大量设计模式

# 二、环境搭配

## 1、添加依赖

```
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>5.1.4.RELEASE</version>
    </dependency>
```

## 2、java代码

```
package com.ph.pojo;

public class Girl {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

## 3、配置文件

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--将对象的创建交给spring容器，所以在这个配置文件里告诉spring要什么对象-->

    <!--calss: 写java类的全限定名，它是通过全类名，然后通过反射的技术创建的-->
    <!--id:可通过id给bean取名-->
    <!--<bean class="com.ph.pojo.Girl" id="girl">-->

    <!--</bean>-->
    <bean class="com.ph.pojo.PrettyGirl" id="girl">
    </bean>
    <bean class="com.ph.pojo.AliPay" id="pay">
    </bean>
</beans>
```

## 4、测试

```
 @Test
    public void m1(){
        //1、获取上下文对象，spring里面申明对象都是通过上下文获取对象。
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        //2、通过上下文对象获取girl
        Girl girl = (Girl) ctx.getBean("girl");
        ((PrettyGirl)girl).show();
        System.out.println(girl);
    }
```

优点：如果没有spring，那么所有对象之间的依赖，类之间的依赖都要在java代码中实现，比较复杂。spring可以通过配置文件来创建对象。

# 三、学习的核心内容 IOC、AOP

IOC：inverse of control 反转控制   （最为典型的是 DI 依赖注入，在spring中表现为xml文件）

控制：创建对象、建立关系的权力

反转：之前是程序员通过java代码自己创建的、现在由spring来创建，程序员只要申明要什么。

# 四、容器

![1551761506512](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551761506512.png)



pojos：自己定义的类

metadata：在spring的配置文件中写的这些就是元数据

实例化容器：例如ClassPathXmlApplicationContext（），将配置文件传入



五、值的注入

## 1、setter（最常用）

​    要求其字段有setter函数才可以完成

​    通过property子节点完成注入

注：如果没有setter函数会报错

```
Error:(39, 32) java: 找不到符号
  符号:   方法 getName()
  位置: 类型为com.ph.pojo.Girl的变量 girl
```

spring默认是通过无参构造函数来创建一个对象的，如果没有无参的构造函数会报错

## 2、构造注入

### 方式一

​     通过“name”构造，实例如下：

```
car类
package com.ph.pojo;

public class Car {
    private String name;
    private Double speed;
    private Double price;

    public Car(String name, Double speed, Double price) {
        this.name = name;
        this.speed = speed;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}


car.xml

    <bean class="com.ph.pojo.Car" id="car">
        <constructor-arg name="name" value="宝马"/>
        <constructor-arg name="price" value="200.00"/>
        <constructor-arg name="speed" value="1230"/>
    </bean>
    

测试类：
    @Test
    public void car(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("car.xml");
        Car car = applicationContext.getBean("car",Car.class);
        System.out.println(car);
    }
```

### 方式二

通过  “index”  下标注入

但是当构造函数多的时候，可能会引起歧义，所以最好用第一种。

```
    <bean class="com.ph.pojo.Car" id="car2">
        <constructor-arg index="0" value="宝马"/>
        <constructor-arg index="1" value="200"/>
        <constructor-arg index="2" value="3000"/>
    </bean>
```

### 方式三

type+index组合使用，可避免歧义，但是过于繁琐。

```
    <bean class="com.ph.pojo.Car" id="car3">
        <constructor-arg index="0" type="java.lang.String" value="宝马"/>
        <constructor-arg index="1" type="java.lang.Double" value="200"/>
        <constructor-arg index="2" type="java.lang.Double" value="3000"/>
    </bean>
```



# 五、Bean标签属性用法

## 1、abstract

​               该bean将无法实例化。

## 2、parent

​               指定父bean，将会继承父bean内容，通过id继承。

使用案例：

```
<bean class="com.ph.pojo.Girl" id="happyGirl" abstract="true">
        <property name="name" value="快乐的女生"></property>
    </bean>

    <bean class="com.ph.pojo.Girl" id="myGirl" parent="happyGirl">
        <property name="age" value="21"></property>
    </bean>
```

## 3、destroy-method

​           指定某个对象销毁的时候，一定执行的方法，适合于请理型工作

触发方法：1、容器关闭（   ctx.close()  ）2、刷新（   ctx.reflush()  ）

## 4、init-method

​            某个对象初始化的适合调用函数。

## 5、name

​            别名，可以写多个（用空格，逗号等等分隔符隔开）

## 6、scope

​            指定范围，可以为singleton（spring上下文只有一个实例），prototype（原型，要一个给一个）

注：默认情况下，容器初始化后，bean就已经注入（初始化），如果将“lazy-init”标签值设定为true，则要等需要注入时才注入。

## 7、depend-on

​             如果一个bean严重依赖于另一个bean的准备的话，就可以配置。例如

```
    <bean class="com.ph.pojo.Girl"  id="girl5" depends-on="clothes">
    </bean>
    <bean class="com.ph.pojo.Dress" id="clothes">
    </bean>
```

```
Dress：
public class Dress {
    public Dress(){
        System.out.println("我打扮好了");
    }
}
Girl：
package com.ph.pojo；
public class Girl {
    private String name;
    private Integer age;
    public Girl(){
        System.out.println("我是女孩");
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
测试代码：
    @Test
    public void m6(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("girlBean.xml");
        Girl girl = applicationContext.getBean("girl5",Girl.class);//这样就不需要强转
    }
```

结果：

![1551766120534](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551766120534.png)



## 8、ref

当要注入的是对象而非普通类型时，要用ref，实例如下：

```
    <bean class="com.ph.pojo.Girl"  id="girl5" depends-on="dress">
       <!--要注入的是对象时，要用ref通过id引用-->
        <property name="dress" ref="dress"/>
    </bean>
    <bean class="com.ph.pojo.Dress" id="dress">
    </bean>
```



## 9、alias

​          可在spring文件中单独定义别名

​           注：spring中的bean是可以互相引用的，前提是被上下文扫描到



# 六、Spring中各种值的注入

1、数组注入

```
people类中有数组时
private String[]friends;
如果配置文件这么写：
<bean class="com.ph.pojo.People" id="people">
        <property name="name" value="刘德华"/>
        <property name="age" value="52"/>
        <property name="friends" value="刘邦 李强"/>
</bean>
测试程序：
    @Test
    public void m1(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        People people = applicationContext.getBean("people",People.class);
        System.out.println(people);
        System.out.println(people.getFriends().length);
    }
    
结果：
People{name='刘德华', age=52, friends=[刘邦 李强]}
1
Process finished with exit code 0
说明：并没有当作数组传入，而是当作了字符串。
解决方案一：array标签区分

    <bean class="com.ph.pojo.People" id="people">
        <property name="name" value="刘德华"/>
        <property name="age" value="52"/>
        <property name="friends">
            <array>
                <value>刘邦</value>
                <value>李强</value>
            </array>
        </property>
    </bean>
解决方案二：逗号隔开（英文输入法中的逗号）
<property name="friends" value="刘邦,李强"/>
```

2、集合注入

（1）当集合内放的时基本数据类型时，与数组类似，例如：

```
        <property name="nums">
            <list>
                <value>1</value>
                <value>2</value>
            </list>
        </property>
```

（2)集合内放的是对象时

```
<property name="cats">
            <list>
                <!--内部bean，无法被外部引用，所以无需id-->
                <bean class="com.ph.pojo.Cat">
                    <property name="legs" value="2"/>
                    <property name="skin" value="黄色"/>
                </bean>
            </list>
        </property>
```

（3）传入的是set集合时

```
        <property name="dogs">
            <set>
                <bean class="com.ph.pojo.Dog">
                    <property name="name" value="小白"/>
                    <property name="age" value="1"/>
                    <property name="hobby" value="跳舞"/>
                </bean>
                <bean class="com.ph.pojo.Dog">
                    <property name="name" value="小蓝"/>
                    <property name="age" value="2"/>
                    <property name="hobby" value="吃饭"/>
                </bean>
            </set>
        </property>
```

（4）map传入

```
        <property name="users">
            <map>
                <entry key="user1">
                    <bean class="com.ph.pojo.User">
                        <property name="name" value="老李"/>
                        <property name="age" value="19"/>
                    </bean>
                </entry>
            </map>
        </property>
```

（5）自动注入（autowire）

根据类型匹配（byType），如果同种类型多个bean，则可以添加primary标签来表示优先级

```
    <bean class="com.ph.pojo.User" id="user" autowire="byType">
        <property name="name" value="养狗人"/>
        <property name="age" value="25"/>
    </bean>
    <bean class="com.ph.pojo.Dog">
        <property name="age" value="2"/>
        <property name="name" value="小黑"/>
        <property name="hobby" value="犬吠"/>
    </bean>
```

也可以byName（bean对应pojo里对象的名字），byConstructor（根据构造器里面参数名来找）

# 七、注解

## 1、使用案例

配置文件的头文件应该改为

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

</beans>
```

注解使用案例

```
package com.ph.service;
import org.springframework.stereotype.Component;
//申明它是一个组建
@Component("userService")
public class UserService {
    public void eat(){
        System.out.println("i am eating");
    }
}
```

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    <import resource="classpath:spring/spring-*"/>
    <!--激活注解-->
    <context:component-scan base-package="com.ph.service"/>
</beans>
```

```
package com.ph;
import com.ph.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class TestService {
    @Test
    public void m1(){
        ApplicationContext applicationContext = new  ClassPathXmlApplicationContext ("spring/ApplicationContext.xml");
        UserService userService=applicationContext.getBean("userService", UserService.class);
        userService.eat();
    }
}
```

## 2、常用注解：

component

controller

service

repository

## 3、零碎知识点

如何引入外部properties文件

```xml
   <context:property-placeholder location="classpath:databse.properties"/>
```

如何通过表达式引用外部的值

```
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <bean class="com.ph.service.UserService">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
    </bean>
```

spring如何从一个配置文件引用另一个配置文件

```
 <import resource="classpath:spring/spring-*"/>
```

spring扫描包的标签写法（包括其子目录）

```
<!--激活注解-->
    <context:component-scan base-package="com.ph.service"/>
```

# 八、AOP XML版本

AOP面向切面编程

## 1、简单实例

需要添加的依赖：

```
<!-- https://mvnrepository.com/artifact/org.aspectj/aspectjrt -->
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjrt</artifactId>
      <version>1.9.2</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.aspectj/aspectjweaver -->
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjweaver</artifactId>
      <version>1.9.2</version>
    </dependency>
```

实例：

```
先贴两个类：
package com.ph.advice;

/*
    写一个before advice
 */
public class BeforeAdvice {
    public void methodBefore(){
        System.out.println("我在方法之前执行。。。。。。");
    }
}


package com.ph.service;

public class ProviderService {
    public void add(){
        System.out.println("添加一个供应商");
    }
}
```

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!--aop基于代理完成，所以要先激活自动代理-->
    <aop:aspectj-autoproxy proxy-target-class="true"/>
    <!--注册一个切面-->
    <bean class="com.ph.advice.BeforeAdvice" id="beforeAdvice">
    </bean>

    <!--配置切入点等信息-->
    <aop:config>
        <aop:aspect id="beforeAspect" ref="beforeAdvice">
            <!--method 指明用哪个方法来切
                pointcut切入点
                -->
            <aop:before method="methodBefore" pointcut="execution(* com.ph.service..*.*(..))"/>
        </aop:aspect>
    </aop:config>
    <bean class="com.ph.service.ProviderService" id="providerService"></bean>
</beans>
```

测试代码：

```
package com.ph;

import com.ph.advice.BeforeAdvice;
import com.ph.service.ProviderService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test2 {
    @Test
    public void m1(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //如果不是Application管理的对象，无法织入
//        ProviderService providerService1 = new ProviderService();
//        providerService1.add();
        //织入成功
        ProviderService providerService = applicationContext.getBean("providerService",ProviderService.class);
        providerService.add();
    }
}
```

结果：

![1551862761640](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551862761640.png)

## 2、<aop: after>用法

```
<aop:aspect id="afterAspect" ref="afterAdvice">
            <!--
            *  com . ph . service . * . * ( . . )
            第一个星：任意访问类型 第二个：任意类  第三个：任意方法  括号里两个点表示任意参数
            execution(* com.ph.service.*.*(String))
            表示在有一个String类型参数的方法后执行
            execution(* com.ph.service.*.*())
            表示在无参方法后执行
            execution(* com.ph.service.*.*(int))
            这里的Integer与int不等同，无法拆装箱
            -->
            <aop:after method="methodAfter" pointcut="execution(* com.ph.service.*.*())"/>
        </aop:aspect>
```

## 3、<aop: after-returning>用法

在方法的返回值之后，区别于<aop: after>,<aop: after>是在整个函数结束之后。

after-returning如何接收返回值：

```
    <aop:config>
        <aop:aspect ref="afterReturningAdvice">
            <aop:after-returning method="returningMethod" pointcut="execution(* com.ph.service.*.*())" returning="returning"/>
        </aop:aspect>
    </aop:config>
    
    
        public void returningMethod( String returning){
        System.out.println("返回值之后。。。");
        System.out.println("返回值："+ returning);
    }
```



## 4、<aop: after-throwing>用法

可以获取异常

```
    <aop:config>
        <aop:aspect ref="exceptionAdvice">
            <aop:after-throwing method="exception" pointcut="execution(* com..*.*(..))"/>
        </aop:aspect>
    </aop:config>
```

## 5、<aop: around>用法

可以决定是否给目标函数放行

```
    <aop:config>
        <aop:aspect ref="aroundAdvice">
            <aop:around method="around" pointcut="execution(* com..*.*(..))"/>
        </aop:aspect>
    </aop:config>
    
   
    
    package com.ph.advice;

import org.aspectj.lang.ProceedingJoinPoint;

public class AroundAdvice {
    //如果没有 proceedingJoinPoint.proceed(); 默认不放行
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        try {
            System.out.println("around......");
           Object object= proceedingJoinPoint.proceed();
           return  object;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}
```

# 九、AOP注解版

1、注释基本用法

​     （1）配置文件中

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <!--1、自动代理-->
    <aop:aspectj-autoproxy/>
    <!--2、基础扫描包-->
    <context:component-scan base-package="com"/>
</beans>
```

​      （2）advice

```
package com.ph.advice;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
@Aspect//切面
@Component//申明组件，实则相当于注册了一个bean
public class BeforeAdvice {
    @Before("execution(* com..*.*(..))")
    public void before(){
        System.out.println("很久之前");
    }
}
```

​     (3)被切类

```
package com.ph.application;

import org.springframework.stereotype.Component;
@Component("hello")
public class Hello {
    public void show(){
        System.out.println("我是 hello 函数");
    }
}
```

## 2、@Order（value）

​      可以指定不同类的不同函数的顺序，但是不能指定相同类的不同函数。另外，value小的先执行。

## 3、@AfterReturning标签

```
package com.ph.advice;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class TestReturing {
    @AfterReturning(value = "execution(* com..*.*(..))",returning = "returning")
    public void afterReturing(String returning){
        System.out.println(returning);
    }
}



package com.ph.application;

import org.springframework.stereotype.Component;
@Component("hello")
public class Hello {
    public void show(){
        System.out.println("我是 hello 函数");
    }
    public String showReturning(){
        return "hello return";
    }
}
```

## 4、@AfterThrowing

```
package com.ph.advice;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class TestThrowing {
    @AfterThrowing(value = "execution(* com.ph.application.Hello.showThrowing())")
    public void testThrowing(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName());
    }
}
```

## 5、@Around

```
package com.ph.advice;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class TestAround {
    @Around(value = "execution(* com..*.*(..))")
    public Object around(ProceedingJoinPoint pjp){
        try {
            System.out.println("i am around");
            Object object= pjp.proceed();
            return object;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}
```

## 6、execution表达式

格式：访问修饰符 包名限定 类名 方法名  参数列表  +  组合条件符号                

举例：

```
public com..*.*(java.lang.String)

访问类型是public，com包或者com子包下的任意类的任意方法并且参数是String的方法

public com.*.*(java.lang.String)
如果是这样，只有一个点，那么只能
```

7、Configuration、ComponentScan、Bean等注解使用案例

```
package com.ph.config;
import com.ph.service.Girl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
@Configuration
@ComponentScan(value = "com.ph")
public class SpringConfig {
    @Bean
    public Girl getGirl(){
        Girl girl = new Girl();
        girl.setName("feifei");
        girl.setAge(18);
        return girl;
    }
}


package com.ph;

import com.ph.config.SpringConfig;
import com.ph.service.Girl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class Test1 {
    @Test
    public void m1(){
        ApplicationContext apx = new AnnotationConfigApplicationContext(SpringConfig.class);
        Girl girl = apx.getBean(Girl.class);
        System.out.println(girl);
    }
}
```

