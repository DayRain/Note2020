# servlet

## 各种路径

例如访问项目名为StudentManage，servlet路径为mainUrl

req.getContextPath()

```
/StudentManage
```

req.getRequestURI()

```
/StudentManage/mainUrl
```

req.getRequestURL()

```
http://localhost:8080/StudentManage/mainUrl
```



# jsp

## 绝对路径

```
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<link rel="stylesheet" href="<%=basePath%>/css/bootstrap.css">
```

