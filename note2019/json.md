json数据交换

# 一、添加依赖

```
  <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.9.8</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.9.8</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.9.8</version>
    </dependency>

    <!--添加处理json为javabean-->
    <!-- https://mvnrepository.com/artifact/org.codehaus.jackson/jackson-core-asl -->
    <dependency>
      <groupId>org.codehaus.jackson</groupId>
      <artifactId>jackson-core-asl</artifactId>
      <version>1.9.13</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.codehaus.jackson/jackson-mapper-asl -->
    <dependency>
      <groupId>org.codehaus.jackson</groupId>
      <artifactId>jackson-mapper-asl</artifactId>
      <version>1.9.13</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/net.sf.json-lib/json-lib -->
    <dependency>
      <groupId>net.sf.json-lib</groupId>
      <artifactId>json-lib</artifactId>
      <version>2.4</version>
    </dependency>
```

# 二、json后台返回

## 1、返回pojo

```
    @RequestMapping("/m1")
    @ResponseBody  //这个注解将表明返回的不是视图，会将数据转换成json格式
    public User m1(){
        User u = new User();
        u.setName("aabb");
        u.setPwd("123456");
        return u;
    }
```

## 2、返回键值对map

```
    @RequestMapping("/m1")
    @ResponseBody  //这个注解将表明返回的不是视图，会将数据转换成json格式
    public User m1(){
        User u = new User();
        u.setName("aabb");
        u.setPwd("123456");
        return u;
    }
```

## 3、返回数组

```
    @RequestMapping("/m3")
    @ResponseBody
    public User[]m3(){
        User user1 = new User();
        user1.setName("nihao");
        user1.setPwd("123");
        User user2 = new User();
        user2.setName("wohao");
        user2.setPwd("123");
        User[]users = new User[]{user1,user2};
        return users;
    }
```

## 4、返回List

```
    @ResponseBody
    public List<User> m4(){
        User user1 = new User();
        user1.setName("wo");
        user1.setPwd("123");
        User user2 = new User();
        user2.setName("wohao");
        user2.setPwd("123");
       List<User>list = new ArrayList<>();
       list.add(user1);
       list.add(user2);
        return list;
    }
```

# 三、json前台解析

## 1、解析返回的pojo

```
<script>
    $(function () {
        $('#btn1').click(function () {
            $.ajax({
                url:'${ctx}/json/m1',
                type:'post',
                success:function (data) {
                    alert(data.name);
                }
            })
        })
    })
</script>
```



## 2、解析map



```
  $('#btn2').click(function () {
            $.ajax({
                url:'${ctx}/json/m2',
                type:'post',
                success:function (data) {
                    alert(data.name);
                    alert(data.age);
                }
            })
        })
```

## 3、解析数组

```
 $('#btn3').click(function () {
            $.ajax({
                url:'${ctx}/json/m3',
                type:'post',
                success:function (data) {
                    for(var i=0;i<data.length;i++){
                        alert(data[i].name);
                        alert(data[i].pwd);
                    }
                }
            })
        })
```

## 4、解析list

```
 $('#btn4').click(function () {
           $.ajax({
               url:'${ctx}/json/m4',
               type:'post',
               success:function (data) {
                   for(var i = 0; i<data.length;i++){
                       alert(data[i].name);
                       alert(data[i].pwd);
                   }
               }
           })
       })
```

# 四、json数据如何通过ajax提交到后台，后台如何解析

## 1、提交一个pojo

前台写法

```
$(function () {
    $('#btn1').click(function () {
        var obj={
            'name':'叶问',
            'pwd':'123456'
        };
        $.ajax({
            url:'${ctx}/json2/add',
            type:'post',
            contentType:'application/json',
            data:JSON.stringify(obj),
            success:function (data) {
            }
        })
    })
})
```

后台写法

```
package com.ph.controller;
import com.ph.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller//=controller+responsebody
@RequestMapping("/json2")
public class JsonController2 {
    //前台如何提交一个User对象过来
    @RequestMapping("/add")
    //user入参只能提交表单数据
    public String add(@RequestBody User user){
        System.out.println(user.getName()+user.getPwd());
        return "msg";
    }
}
```

## 2、提交一个list

前台代码：

```
    $('#btn2').click(function () {
        var obj={
            'name':'叶问',
            'pwd':'123456'
        };
        var obj2={
            'name':'nihao',
            'pwd':'123456'
        };
        var arr = new Array();
        arr.push(obj);
        arr.push(obj2);
        $.ajax({
            url:'${xtx}/json2/add2',
            type:'post',
            contentType:'application/json',
            data:JSON.stringify(arr),
            success:function (data) {
                if(data.code == 1000){
                    alert("ok!");
                }
            }
        })
    })
```

后台代码：

```
  @RequestMapping("/add2")
    //user入参只能提交表单数据
    public Map<String,Integer> add2(@RequestBody List<User> list){
        Map<String,Integer>map = new HashMap<>();
        map.put("code",1000);
        return map;
    }
```

# 五、XML数据交互

## 1、添加依赖

```
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.9.8</version>
    </dependency>
```

## 2、方法的返回，数据类型的定义

```
package com.ph.controller;

import com.ph.pojo.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//produces返回类型
@RequestMapping(value = "/xml",produces ={MediaType.APPLICATION_XML_VALUE} )
@Controller
public class XMLController {
    @RequestMapping("/m1")
    @ResponseBody
    public User m1(){
        User user = new User();
        user.setName("ph");
        user.setPwd("123");
        return user;
    }
}
```

# 六、文件上传

apache上传组件方案

## 1、添加依赖



```
<!-- https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>

```

## 2、springmvc中注册一个文件上传解析器

```
 <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <!--定义文件上传的大小-->
        <property name="maxUploadSize" value="1024*1024"></property>
        <property name="defaultEncoding" value="utf-8"></property>
        <property name="maxUploadSizePerFile"value="20000"></property>
    </bean>
```

## 3、写一个上传的页面

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload</title>
</head>
<body>
<form action="${ctx}/file/upload" method="post" enctype="multipart/form-data">
    文件：<input type="file" name="file"><br>
    <input type="submit" name="提交">
</form>
</body>
</html>
```

## 4、后台处理程序

## 

```
package com.ph.constant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Date;
@Controller
@RequestMapping("/file")
public class FileController {
    //入参代表上传的文件
    private static String uploadPath = "E:"+ File.separator;
    @RequestMapping("/upload")
    //如果成功，将文件名放入model
    public String upload(@RequestParam("file")MultipartFile multipartFile, Model model){
        //1、判断为空否
        if(multipartFile != null && !multipartFile.isEmpty()){
            //2、获取文件名
          String  originalFilename= multipartFile.getOriginalFilename();
            System.out.println(originalFilename);
            //3、截取文件名的前缀
            String fileNamePrefix = originalFilename.substring(0,originalFilename.lastIndexOf('.'));
            //4、加工处理,原文件加时间戳
            String newfileNamePrefix = fileNamePrefix+new Date().getTime();
            //5、得到新文件名
            String newfileName = newfileNamePrefix+originalFilename.substring(originalFilename.lastIndexOf('.'));
            //6、新建文件对象
            File file = new File(uploadPath+newfileName);
            //7、上传
            try {
                multipartFile.transferTo(file);
                model.addAttribute("fileName",newfileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "uploadSuc";
    }
}

```

## 5、多文件上传

```
   @RequestMapping("/upload2")
    //如果成功，将文件名放入model
    public String upload2(@RequestParam("file")MultipartFile[]multipartFiles, Model model){
        //1、判断为空否
        List<String>fileNames = new ArrayList<>();
        if(multipartFiles != null && multipartFiles.length>0){
            //遍历
            for(MultipartFile multipartFile:multipartFiles){
                if(multipartFile != null && !multipartFile.isEmpty()){
                    //2、获取文件名
                    String  originalFilename= multipartFile.getOriginalFilename();
                    System.out.println(originalFilename);
                    //3、截取文件名的前缀
                    String fileNamePrefix = originalFilename.substring(0,originalFilename.lastIndexOf('.'));
                    //4、加工处理,原文件加时间戳
                    String newfileNamePrefix = fileNamePrefix+new Date().getTime();
                    //5、得到新文件名
                    String newfileName = newfileNamePrefix+originalFilename.substring(originalFilename.lastIndexOf('.'));
                    //6、新建文件对象
                    File file = new File(uploadPath+newfileName);
                    //7、上传
                    try {
                        multipartFile.transferTo(file);
                        fileNames.add(newfileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        model.addAttribute("fileNames",fileNames);
        return "uploadSuc2";
    }
```

## 6、servlet3.0新特性

```
    <bean id="multipartResolver" class="org.springframework.web.multipart.support.StandardServletMultipartResolver">
    </bean>
```

# 七、文件下载

```
package com.ph.constant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/download")
public class DownloadController {

    //定义一个文件下载的目录
    private  static String parentPath = "E:"+ File.separator;

    @ResponseBody
    @RequestMapping("/down")
    public String down(HttpServletResponse response){

        response.setCharacterEncoding("utf-8");
        //通过输出流写到客户端，浏览器
        //1、获取文件下载名
        String fileName = "头像.jpg";
        //2、构建一个文件对象,通过Paths工具类获取一个Path对象
        Path path = Paths.get(parentPath,fileName);
        //3、判断它是否存在
        if(Files.exists(path)) {
            //存在则下载
            //通过response设定响应类型
            //4、获取文件的前缀
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            //5、添加头信息
            response.setContentType("application/"+fileSuffix);
            //6、设置contentType，只有指定它才能下载
                       //只认识编码 ISO8859-1
            try {
                response.addHeader("Content-Disposition","attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //7、通过path写出去就ok
            try {
                Files.copy(path,response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("com in response");
        return "msg";
    }
}
```

