# 一、简介

Mongo，面向文档的数据模型能使它很容易的在多台服务器之间进行数据分割。

能够自动处理跨集群的数据和负载，自动重新分配文档，以及将用户请求路由到正确的机器上。带来的好处是，开发者不需要考虑mongo的拓展问题。只要添加了新的服务器，mongo可以实现将现有的数据传送过去。

默认端口27017

# 二、基本知识

## 2.1文档

mongo中的文档指的是键值对的有序集

```
{ 
  “hello”: "world"
}
```

对应于js中的对象。

注意点：

1、文档的字段是有序的

2、区分类型和大小写

3、文档里面不能有重复的键

## 2.2集合

定义：

集合是文档的集合，一组文档。

命名规则：

不能有空串

不能system.开头

不能包含$

子集合：

子集合用用点表示

例如blog.article



访问集合

```
db.集合名
db.getCollection("集合名")
```

查看所有的集合

```
show collections
```

删除集合

```
> show collections
my
self
> db.my.drop()
true
> show collections
self

```



## 2.3数据库

数据库是0个或者多个集合。

数据库最终会变成文件，所以命名上有很多限制，不要用乱七八槽的命名

内置数据库

- admin

相当于root数据库，如果像这个数据库添加一个用户，那么该用户会获得所有的权限。

一些特定的命令也只能在这个数据库运行，比如列出所有数据或者关闭服务器。

- local

这是一个不能复制的数据库，一台服务器的所有本地集合都可以存在这个数据库里。

- config

分片信息都可以存在配置文件里。

数据库的切换和创建都是use。

删除是切换到数据库后，db.dropDatabase()。

# 三、基本命令

## 3.1、客户端

1、db

查看指向那个数据库

2、use  testdb

选择哪个数据库

## 3.2、增删改查入门

1、创建

```
> post = {
... "id":1,
... "title": "日记",
... "content": "今天是个好日子呀！",
... "date": new Date()}
```

```
> db.blog.insert(post)
WriteResult({ "nInserted" : 1 })

```

```
db.blog.find()
{ "_id" : ObjectId("5fc845143d766d2d26fd533e"), "id" : 1, "title" : "日记", "content" : "今天是个好日子呀！", "date" : ISODate("2020-12-03T01:53:03.213Z") }

```

2、查询

```
> db.blog.find()
{ "_id" : ObjectId("5fc845143d766d2d26fd533e"), "id" : 1, "title" : "日记", "content" : "今天是个好日子呀！", "date" : ISODate("2020-12-03T01:53:03.213Z") }
> db.blog.findOne()
{
	"_id" : ObjectId("5fc845143d766d2d26fd533e"),
	"id" : 1,
	"title" : "日记",
	"content" : "今天是个好日子呀！",
	"date" : ISODate("2020-12-03T01:53:03.213Z")
}

```

3、修改

语法是：

db.blog.update(定位方式， 替换对象)

定位方式：表示怎么定位到这个集合，形式是{title: "标题"}，前者不需要加引号



```
添加一个字段
> post.comments = []
[ ]

更新
> db.blog.update({title: "日记"}, post)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

查询
> db.blog.findOne()
{
	"_id" : ObjectId("5fc845143d766d2d26fd533e"),
	"id" : 1,
	"title" : "日记",
	"content" : "今天是个好日子呀！",
	"date" : ISODate("2020-12-03T01:53:03.213Z"),
	"comments" : [ ]
}

```

4、删除

删除的文档，也需要定位

```
> db.blog.remove({title: "日记"})
WriteResult({ "nRemoved" : 1 })
> db.blog.findOne()
null

```

## 3.3、数据类型

### 所有数据类型

1、 null

表示空值或者不存在{"x" : null}

2、布尔类型

true ，false {“x" : true}

3、数值

shell默认使用64位浮点型数值

{"x" : 3.14}  {"x" : 3}

如果需要表示整数

{"x" : NumberInt("3")}

{"x" : NumberFloat("3")}

4、字符串

{”x": "foobar"}

5、日期

{“x" : new Date()}

6、正则表达式

{"x" : /foobar/i}

7、数组

{”x" :["a", "b", "c"]}

8、内嵌文档

{"x" : {"foo" : "bar"}}

内嵌文档改变了处理数据的方式，通过冗余来提高效率

9、对象id

文档的唯一标识

{"x" : ObjectId()}

10、二进制数据

将非二进制字符串保存进数据库中，只能用这个

shell中无法使用

11、代码

可以包含任何形式的js代码

{"x" : function() {} }

### 主键

主键_id，默认是ObjectId，这些值是任何类型的，默认是ObjectId对象。

同一个集合里面，主键不能相同

ObjectId的字节创建方式

1- 4 时间戳

5-7 机器

8-9 pid

10-12 计数器

## 3.4 mongo shell

### 远程连接

命令

mongo ip:27017/myDB

或者你想启动后再连接可以这么做

mongo --nodb

```
conn = new Mongo("ip:27017")
db = conn.getDB("myDB")
```

### 运行脚本

mongo 脚本1   脚本2

进入客户端后，运行脚本

load("XXXX.js")

## 3.5 文档增删改查

### 简单插入

```
> db.foo.insert({"bar" : "baz"})
WriteResult({ "nInserted" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5fc8545e3d766d2d26fd533f"), "bar" : "baz" }

```

### 批量插入

```
> db.foo.insert([ {"_id": 1} , {"_id": 2} , {"_id" : 3}])
BulkWriteResult({
	"writeErrors" : [ ],
	"writeConcernErrors" : [ ],
	"nInserted" : 3,
	"nUpserted" : 0,
	"nMatched" : 0,
	"nModified" : 0,
	"nRemoved" : 0,
	"upserted" : [ ]
})

```

### 删除文档

删除集合内部所有数据，但不会删除集合本身

db.foo.remove()

### 条件删除

```
> db.foo.remove({"_id" : 1})
WriteResult({ "nRemoved" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5fc8545e3d766d2d26fd533f"), "bar" : "baz" }
{ "_id" : 2 }
{ "_id" : 3 }

```

### 清空集合

db.foo.drop()

### 修改一条数据

```
> db.col.update({'title' : 'MongoDB 教程'}, {$set: {'title' : 'MongoDB'}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.col.find()
{ "_id" : ObjectId("5fc858e33d766d2d26fd5340"), "title" : "MongoDB", "description" : "MongoDB 是一个 Nosql 数据库", "by" : "菜鸟教程", "url" : "http://www.runoob.com", "tags" : [ "mongodb", "database", "NoSQL" ], "likes" : 100 }

```

### 修改多条数据

```
db.col.update({'title':'MongoDB 教程'},{$set:{'title':'MongoDB'}},{multi:true})
```

类似的修改器还有

$gt

$set

$push

$inc

$push

$each

### 删除文档

删除所有同名的

```
db.col.remove({'title':'MongoDB 教程'})
```

只删除一个

```
db.COLLECTION_NAME.remove(DELETION_CRITERIA,1)
```

删除所有

```
db.col.remove({})
```

### 查询文档

条件查询

```
db.col.find(条件)
```

易读的方式读取数据

```
db.col.find().pretty()
```

