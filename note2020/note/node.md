# 一、hello world

## 1、查看版本

```
node --version
```

## 2、运行代码

编写js代码

```
var foo = 'hello,world'
console.log(foo)
```

cd 到该文件目录

```javascript
node 文件名（文件名不能为node）
```

## 3、说明

```
1、node中，采用EcmaScript编码。没有Dom、BOM。


```



## 4、读取文件

```
var fs = require('fs')
fs.readFile('./data/hello.txt', function(error , data){
    console.log(data);
    console.log(error);
})
```

## 5、写文件

```
var fs = require('fs');

fs.writeFile('./data/test.md',"测试一下写文件", function(error){
    if(error){
        console.log('写入失败');
    }else{
        console.log("写入成功");
    }
})
```

## 6、http

```
var http = require('http');
var server = http.createServer();

server.on('request', function(request, response){
    response.write('你的请求路径是：'+ request.url);
    response.setCharac
    response.end();
})

server.listen(8080, function(){
    console.log('在8080启动了一个服务');
})
```

## 7、 版本管理工具

参考链接：https://blog.csdn.net/lewky_liu/article/details/87959839

linux下用nvm

1、查看node版本列表

```
nvm list available
```

2、修改源


```

setting.txt文件下加上

#新增

arch: 64 
proxy: none 
node_mirror: http://npm.taobao.org/mirrors/node/ 
npm_mirror: https://npm.taobao.org/mirrors/npm/
```
3、安装node

```
nvm install 8.11.3 64-bit
```

4、查看已安装版本

```
nvm list
```

5、使用指定版本

```
nvm use 8.11.3
```

5、删除指定版本

```
nvm uninstall 8.11.3
```



# 二、模块化

## 1、基本说明

```
2、var rs =  require('模块')，表示加载模块

3、require（‘表示在模块中直接引用js代码’）

4、node中只有模块作用域，没有全局

5、相对路径表示当前路径的时候必须是   ./   不可以不写
```

## 2、简单案例

a文件

```
var b = require('./05-b.js');
console.log(b.getAdd());
```

b文件

```
var a = 10;
var b = 20;
exports.getAdd = function () {
    return a + b;
}
```

## 3、export

1、第一种方式，可以直接向上面那样

```
exports.getAdd = function () {
    return a + b;
}
```

2、第二种方式，直接挂载到module上

c

```
var foo = require('./07-d');
console.log(foo);
```

d

```
var foo = 'abc123';
module.exports = foo;
```

输出

```
abc123
```

所以require得到的返回值，是module对象中的exports属性的值。

exports就是module.exports的一个引用。

如果先给module.exports复制，再给exports复制，后者将无效，因为前者的指向的存储空间发生了改变

# 三、npm

## 1、简单命令

- npm init

​     npm init -y 跳过向导，快速生成

- npm install 

一次性把dependencies选项中的依赖全部安装

- npm install 包名

只下载

- npm install --save 包名

下载并且保存依赖项（package.json文件中的dependencies选项）

## 2、修改npm源

```
1、npm install --global cnpm

安装的时候使用cnpm

比如
cnpm install jquery

2、或者
npm install jquery --registry=https://registry.npm.taobao.org


3、加入配置
npm config set registry https://registry.npm.taobao.org
```

建议使用第三种方式：



# 四、vue创建单页面应用

```
vue init webpack  projectname

```



```
npm run dev
```



# 五、vue cli

## 1、安装环境

1、安装全局拓展

```
npm install -g  @vue/cli   //vuecli3
npm install -g vue-cli    //vuecli2

npm uninstall -g vue-cli //卸载
```

2、查看版本

```
vue --version
```

## 2、创建项目

### 1、创建命令

```
vue create vhr-client
```

如果出现以下错误

```
vue : 无法加载文件 D:\soft\node\node-install\vue.ps1，因为在此系统上禁止运行脚本。有关详细信息，请参阅 https:/go.micros
oft.com/fwlink/?LinkID=135170 中的 about_Execution_Policies。
所在位置 行:1 字符: 1
+ vue create vhr-client
+ ~~~
    + CategoryInfo          : SecurityError: (:) []，PSSecurityException
    + FullyQualifiedErrorId : UnauthorizedAccess
```

表示权限不够

管理员打开cmd

```
set-ExecutionPolicy RemoteSigned  
输入 Y或者A即可
```

### 2、运行命令

```
npm run server
```

