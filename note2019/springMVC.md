SpringMVC

# 一、简介

springMVC是一个web层MVC框架。

model  模型

view  视图

controller 控制器

这是一种设计模式，将责任拆分，不同组件负责不同的事。

好处：结构清晰

​          好维护

坏处：复杂

# 二、入门体验

## 1、创建web项目

## 2、编写web.xml

在其中注册一个特殊的servlet，前端控制器

```
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                        http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">
  <display-name>Archetype Created Web Application</display-name>
  <!--注册一个前端控制器
     DispatcherServlet
    -->
  <servlet>
    <!--这里的命名是有讲究的，如果不去更改spring的默认配置文件，他会去
    web-inf 下面找一个叫做springmvc-servlet.xml的文件。
    -->
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
</web-app>

```

## 3、编写一个springMVC的配置文件

springmvc-servlet.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--配置一个视图解释器 ,类名：内部资源视图解释器-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!--前缀-->
        <property name="prefix" value="/jsp/"/>
        <!--后缀-->
        <property name="suffix" value=".jsp"/>
    </bean>
    <bean class="com.ph.controller.HelloControl" name="/helloControl">
    </bean>
</beans>
```

## 4、编写一个控制器

```
package com.ph.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloControl implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("girl","菲菲");
        mav.setViewName("girl");
        return mav;
    }
    //实现controller接口的方式
}
```

## 5、编写一个结果页面

```
<%--
  Created by IntelliJ IDEA.
  User: 13760
  Date: 2019/3/7
  Time: 18:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    我的女孩：${girl}
</head>
<body>

</body>
</html>

```



## 6、组件分析

### （1）web.xml

注册前端控制器，目的在于，我们希望让springmvc去处理所有的请求。

通过

```
 <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
```

处理了所有的请求（不是真的所有）

### （2）urlPattern的写法问题

- /
- /*         (永远别这么写，无法匹配controller导致404)
- *.do     （匹配.do的控制器页面）

```
<bean class="com.ph.controller.HelloControl" name="/helloControl.do">
</bean>
```

如果这么写，第三种也可以匹配成功！第三种是有的团队习惯将请求的行为加上do小尾巴来区分，还有用*.action

/用法

处理所有请求，但是和/*不一样，他处理后要出去的时候不会将girl.jsp当作一个新的请求，将渲染的请求直接返回给浏览器

### （3）关于前端控制器的解释

springmvc设计的理念是希望开发者尽量远离原生的servlet api因为原生的api比较繁琐，希望将操作进一步简化，将很多责任拆分，不把技术点绑定在一起，可以随意切换。但是本身还是基于servlet。

Springmvc配置文件名字的问题

​         默认情况下使用 dispacherServlet-servlet 的名字当作命名空间

【namespace】.xml(WEB-INF)之下寻找。

​         如果一定要使用其他名字,在web.xml 中的servlet标签中修改如下参数

```
    <init-param>
      <param-name>namespace</param-name>
      <param-value>mvc</param-value>
    </init-param>
```

默认是在web.xml上的，但如果是maven的标准，应该在resources下，那么该如何解决这种问题呢。？

```
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:+文件名</param-value>
    </init-param>
```

这样可以顺带改名，推荐使用这种用法。

### （4）视图解析器

springmvc支持多种视图

- jsp
- freemaker

内部的资源视图解释器

- 视图前缀

     /jsp/        这是请求资源的路径的配置，viewName：girl

- 视图后缀

   .jsp           此时：前缀+视图名+后缀 = /jsp/girl.jsp

springmvc的设计理念：

​      物理视图由逻辑视图转换而来

​      物理视图是 webapp/jsp/girl.jsp

​       逻辑视图：prefix，logicViewName，suffix

​                      View = prefix +  logicViewName   +  suffix

###    （5）控制器的解释

是一种比较传统的实现一个接口的方式完成的，Controller（只有一个函数的接口又被称为函数式函数）

设计为ModelAndView，在model中填充数据，然后在具体的视图进行展示，还需要在配置文件中配置这个bean，取个名字充当访问的URL，它就处理一个请求，跟servlet相差不大。

### （6）注解开发模式

​          基于接口的开发模式，已经是过去式了。现在一般采用注解开发。

- @Controller
- @RequestMapping

开发步骤

1. 记得配置基础扫描包，这样配置的1注解才会生效
2. 在指定的类上面田间@Controller注解
3. 添加@RequestMapping类似于前面的controller的那个名字（不同requesthandler处理的HandlerMapping）

当我们写上Controller之后，就标记了它为spring的一个控制器组件，此时我们的handlermapping回去扫描寻找这个controller是否与之匹配，如果发现匹配就把这里处理的工作交给它。

匹配的规则是什么？  通过请求的路径进行匹配的 @RequestMapping（URI）此时就是通过这个URI进行匹配的。

（7）请求转发

转发到页面：直接写名

重定向到页面：redirect：path

转发到另一个控制器 forward:path

# 三、springmvc如何访问web元素

request

session

application

可以通过模拟的对象完成操作，也可以使用原生的ServletAPI完成，直接在方法中入参即可。

# 四、注解详解

## 1、@RequestMapping

- value写的是路径，是一个数组的形式，可以匹配多个路径。
- path  是value的别名，所以二者任选其二，他们的作用是一样的。
- method 是可以指定可以访问的请求的类型，比如get，post，它可以写成数组的形式。
- params可以指定参数，你还可以去限定这个参数的特征，比如等于某个值。
- headers 影响浏览器的行为。
- consumers 媒体类型
- produces 产生的响应的类型

## 2、关于请求路径的问题

springmvc支持ant风格

-   ？任意字符，斜杠除外
- ‘*’   任意个字符
- ** 支持任意层路径   /m3/** 这样才可以体现出来  /m3**

@GetMapping() 只支持get方式的请求

@PostMapping() 只支持post方式的请求

## 3、关于静态资源访问的问题

由于我们的servlet设置了URL匹配方式为 / 所以，它将静态资源也当作了一个后台的请求

比如Controller里的index.css文件，因为没有，所以404

解决方式很多，最简单的是让apringmvc单独处理，将这些交给容器的默认servlet处理，就不让DispacherServlet来处理。

解决方式一：

```
spring配置文件内添加
<mvc:default-servlet-handler/>
注意：如果只加上这一个，全部由他处理，注解将失效

所以应该组合使用
<mvc:annotation-driven/>
    <mvc:default-servlet-handler/>

```

解决方式二：

通过映射关系，一一编写规则

```
<mvc:resources mapping="/css/*" location="css/"/>
```

解决方式三：

自行在web.xml定义映射规则

@PathVariable

restful风格

```
package com.ph.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/add/{id}/{name}/{price}")
    public String addProduct(@PathVariable("id") Integer id, @PathVariable("name") String name,@PathVariable("price") Double price){
        System.out.println(id+name+price);
        return "forward";
    }
}
```

## 4、对于非get post请求的支持

对于非 get post 请求的支持，需要额外的内容添加，需要加一个过滤器

- 过滤器
- 返回的不再是页面而是数据

```
  <filter>
    <filter-name>hiddenHttpMethodFilter</filter-name>
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>hiddenHttpMethodFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

表单里还要添加隐藏的参数

name="_method"   value="DELETE"



## 5、@Responsebody

返回数据，一般情况返回json格式。

```
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/put")
    @ResponseBody
    public String put(String name){
        System.out.println(name);
        return  "ok";
    }
}
```

@ModelAttribute

@SessionAttribute

@SessionAttributes

@RequestParam

@RequestBody

@InitBinder



## 6、关于post请求中文乱码问题解决

springmvc提供了一个优秀的字符编码过滤器，只要注册即可。

```
  <filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>utf-8</param-value>
    </init-param>
    <init-param>
      <param-name>forceRequestEncoding</param-name>
      <param-value>true</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

## 7、关于form表单提交数据的方式

方式一：

通过属性名进行绑定，可以完成数据传送



![1552094005110](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1552094005110.png)

![1552094033424](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1552094033424.png)

两者必须一致

  方式二：

利用@RequestParam

```
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/put")
    @ResponseBody
    public String put(@RequestParam("name") String name){
        System.out.println(name);
        return  "ok";
    }
}
```

方式三：

直接使用pojo传递

```
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/put")
    @ResponseBody
    public String put(User user){
        System.out.println(user.getName()+user.getPassword());
        return  "ok";
    }
}
```

## 8、日期类型需要特殊处理

```

@Controller
@RequestMapping("/user")
public class UserController {
    @InitBinder
    public void init(WebDataBinder webDataBinder){
        //指定什么格式，前台传什么格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class,new CustomDateEditor(simpleDateFormat,false));
    }
    @RequestMapping("/put")
    @ResponseBody
    public String put(User user){
        System.out.println(user.getName()+user.getPassword()+user.getBirth().toString());
        return  "ok";
    }
}
```



## 9、@ModelAttribute

使用方式一：

如果某些对象从头到尾每次请求中都要存在，不消失的话，就是和这么用

```
package com.ph.controller;
import com.ph.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/user2")
public class User2Controller {
    //在controller任何一个处理方法之前执行
    @ModelAttribute
    public User init(){
        User user = new User();
        user.setName("nihao");
        System.out.println("init....");
        return user;
    }
    @RequestMapping("/login")
    public String login(Model model){
        System.out.println(model.containsAttribute("user"));
        System.out.println(model.containsAttribute("User"));
        return "msg";
    }
}
```

使用方式二:

```
    @ModelAttribute("user")
    public void init(Model model){
        User user = new User();
        user.setName("nihao");
        model.addAttribute("user",user);
    }
```

如果没有传，加上@ModelAttribute，会使用自己的参数。

## 10、@SessionAttributes

这个用在类上，它会将模型自动填充到会话里面去。

## 11、@SessionAttribute

要求当前这次访问当中的会话里面必须要有某个对象



## 12、@RestController

= @Controller+@ResponseBody



## 13、@RequestBody

通过ajax获取数据，而非表单数据。

# 五、ctx写法

```
package com.ph.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(loadOnStartup = 2,urlPatterns = {})
public class InitServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute("ctx",config.getServletContext().getContextPath());
        super.init(config);
    }
}
```

# 六、拦截器

​           类似于过滤器，在请求将被处理之前做检查过滤，有权利决定请求是否被处理。

拦截器，可以设置多个。

​          通过实现HandlerInterceptor，这是一个接口

定义了非常重要的三个方法

- 前置处理
- 后置处理
- 完成处理

案例一

通过拦截器实现方法耗时统计与警告

```
    <!--拦截器的配置-->
    <mvc:interceptors>
        <mvc:interceptor>
            <!--拦截所有的请求 /*只能拦截一层-->
            <mvc:mapping path="/**/*"/>
            <bean class="com.ph.interceptors.MethodTimerInterceptor"></bean>
        </mvc:interceptor>
    </mvc:interceptors>
```

```
package com.ph.interceptors;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MethodTimerInterceptor implements HandlerInterceptor {

    private static final  Logger LOGGER=Logger.getLogger(MethodTimerInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //定义开始时间
        long start = System.currentTimeMillis();
        //将其存入请求域
        request.setAttribute("start",start);
        //返回true，才回去找下一个拦截器，如果没有下一个，则去controller
        //记录请求日志
        LOGGER.info(request.getRequestURI()+",请求到达！");
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //取出start
        long start = (long) request.getAttribute("start");
        //得到end
        long end = System.currentTimeMillis();
        //记录耗时
        long spend = end - start;

        if(spend>=1000){
            LOGGER.warn("方法耗时严重，记录一下耗时："+spend);
        }else{
            LOGGER.info("方法耗时"+spend+"毫秒，正常");
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```

案例二

   会话拦截

后台代码：

```
package com.ph.interceptors;

import com.ph.pojo.User;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 会话拦截器
 */
public class SessionInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = Logger.getLogger(SessionInterceptor.class);
    @Override
    //简单当前会话是否有User，有就放行，没有不放
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       Object user= request.getSession().getAttribute("SESSION_USER");
       if(user == null){
           LOGGER.warn("您不具备权限，请先登录");
           return false;
       }
       if(user instanceof User){
           //可以在这里查询数据库进行校验
           User u = (User) user;
           u.setPwd(null);
           request.getSession().setAttribute("SESSION_USER",u);
           LOGGER.info(u.getName()+"正在处于登录状态，可以执行操作");
       }else{
           LOGGER.warn("请先登录");
           return false;
       }
        return false;
    }
}
```

配置文件

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <context:component-scan base-package="com.ph"/>
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
    <mvc:default-servlet-handler/>
    <mvc:annotation-driven/>
    <!--文件上传解析器,id已经固定，必须是这个-->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <!--定义文件上传的大小-->
        <property name="maxUploadSize" value="1024000"/>
        <property name="defaultEncoding" value="utf-8"/>
        <property name="maxUploadSizePerFile" value="20000"/>
    </bean>

    <!--拦截器的配置-->
    <mvc:interceptors>
        <mvc:interceptor>
            <!--拦截所有的请求 /*只能拦截一层-->
            <mvc:mapping path="/**/*"/>
            <bean class="com.ph.interceptors.MethodTimerInterceptor"></bean>
        </mvc:interceptor>
    </mvc:interceptors>

    <!--会话拦截器-->
    <mvc:interceptors>
        <mvc:interceptor>
            <!--比如要拦截User（这里暂时用Test）模块下的会话（但是需要开放登录权限）-->
            <mvc:mapping path="/test/**/*"/>
            <!--排除登录这个URI-->
            <mvc:exclude-mapping path="/test/login"/>
            <bean class="com.ph.interceptors.SessionInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
</beans>
```

拦截器执行顺序问题

springmvc中，拦截器执行顺序和配置顺序有关。配在前面的优先拦截。

例如前置处理时   a1，a2，a3       后置处理  a3，a2，a1

拦截器与过滤器的比较

相似点：都有优先处理请求的权力，并且可以把请求转移到实际需要的地方，也可以对请求或者会话里的数据进行改造。

不同点：

- 过滤器和拦截器一起时，过滤器优先。
- 过滤器是servlet规范里的组件
- 拦截器一般是框架自己额外添加的组件。

