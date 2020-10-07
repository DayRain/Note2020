# 一、安装

## 1、解压

下载包，解压

```
安装gcc
yum install gcc-c++
make
```

安装完成后，解压目录里有个REDME.md，会告诉你怎么运行，不同版本不一样。现在装的版本为：6.0.6

说明书提示启动入口在src目录下

## 2、配置后台运行

redis默认不是后台运行，需要配置conf中的

```
daemonize yes
```

## 3、启动

### 1、启动服务端

进入src目录下

```
 ./redis-server 配置文件所在目录
```

### 2、启动客户端

```
./redis-cli -p 6379
```

如果设置了密码，需要-a 加上密码，也可以登录后输入密码

```
127.0.0.1:6379> keys *
(error) NOAUTH Authentication required.  
127.0.0.1:6379> auth Ph0716
OK
```



### 3、查看版本

```
info
```

### 4、查询服务是否开启

```
ps -ef |grep redis
```

### 5、关闭服务

```
客户端里
shutdown
exit
```

### 6、redis-benchmark性能测试

```
./redis-benchmark -h localhost -p 6379 -c 100 -n 100000

-c表示连接数 -n表示请求数
```

### 7、配置远程连接

```
1、找到
注释掉
bind 127.0.0.1
2、添加：
表示允许所有的ip连接
bind 0.0.0.0
3、修改模式，改成非保护模式
protected-mode no
```



# 二、基础知识

## 1、默认数据

默认有16个数据库，默认使用第0个

可以用select切换数据库

例如切换到第3个数据库

```
select 3
```

查看db大小

```
DBSIZE
```

## 2、常用命令

根据key值查看value值

```
get   key
```

查看所有的key值

```
keys *
```

清空当前数据库

```
flushdb
```

清空所有数据库

```
flushall
```

判断key是否存在

```
EXISTS name
```

设置过期时间

```
expire key second
时间单位为秒
```

查看还有多久过期

```
ttl  key
```

移出key

将key从一个数据库移动到另一个数据库

```
move key  数据库id
```

查看key的类型

```
type key
```



## 3、redis属性

redis是单线程的，redis基于内存操作、CPU不是redis性能瓶颈，redis依赖于内存和带宽。

## 4、String类型

### （1）append

追加字符串，如果不存在，则创建

```
127.0.0.1:6379[1]> set name hello
OK
127.0.0.1:6379[1]> get name
"hello"
127.0.0.1:6379[1]> append name world
(integer) 10
127.0.0.1:6379[1]> get name
"helloworld"
127.0.0.1:6379[1]> append password abc123
(integer) 6
127.0.0.1:6379[1]> get password
"abc123"
127.0.0.1:6379[1]> 

```

### （2）strlen

查看某个key对应value的长度

```
127.0.0.1:6379[1]> STRLEN name
(integer) 10
127.0.0.1:6379[1]> STRLEN password
(integer) 6
```

### （3）incr

自增

前提必须是纯数字的字符串

```
127.0.0.1:6379[1]> set age 18
OK
127.0.0.1:6379[1]> type age
string
127.0.0.1:6379[1]> incr age
(integer) 19
127.0.0.1:6379[1]> TYPE age
string
127.0.0.1:6379[1]> INCR name
(error) ERR value is not an integer or out of range

```

```
incr 5      表示减5
```



### （4）decr

自减，同上

### （5）range

```
127.0.0.1:6379[1]> get password
"abc123"
127.0.0.1:6379[1]> getrange password 1 3        #获取指定范围内的字符串
"bc1"
127.0.0.1:6379[1]> getrange password 0 -1       #获取全部
"abc123"
127.0.0.1:6379[1]> get password                 #
"abc123"
127.0.0.1:6379[1]> setrange password 1 xx
(integer) 6
127.0.0.1:6379[1]> get password
"axx123"
127.0.0.1:6379[1]> 
```

### （6）setex

设置一个键值对，并设置过期时间

等同于：

```
SET mykey value
EXPIRE mykey seconds 
```



```
127.0.0.1:6379[1]> setex temp 30 hello
OK
127.0.0.1:6379[1]> ttl temp
(integer) 25
127.0.0.1:6379[1]> ttl temp
(integer) 23
127.0.0.1:6379[1]> ttl temp

```

### （7）setnx

如果不存在的话，就添加键值对

```
127.0.0.1:6379[1]> setnx peng 123
(integer) 1
127.0.0.1:6379[1]> setnx peng 234
(integer) 0
127.0.0.1:6379[1]> get peng
"123"
```

### （8）mset

同时获取多个值

```
127.0.0.1:6379[1]> mset key1 value1 key2 value2
OK
127.0.0.1:6379[1]> mget key1 key2
1) "value1"
2) "value2"
127.0.0.1:6379[1]> 

```

### （9）getset

获取原有的数据，并设置下新的数据

```
127.0.0.1:6379[1]> get db 
"mybatis"
127.0.0.1:6379[1]> getset db mysql
"mybatis"
127.0.0.1:6379[1]> get db
"mysql"

```

## 5、List类型

redis里面的list可以作为阻塞队列、栈

所有的list命令都是L开头的

### （1）lpush

list的左边添加元素

```
127.0.0.1:6379[1]> lpush list 01abc
(integer) 1
127.0.0.1:6379[1]> lpush list 02abc
(integer) 2
127.0.0.1:6379[1]> lpush list 03abc
(integer) 3
127.0.0.1:6379[1]> get list
(error) WRONGTYPE Operation against a key holding the wrong kind of value
127.0.0.1:6379[1]> lrange list 0 -1
1) "03abc"
2) "02abc"
3) "01abc"
```

### （2）rpush

list的右边添加元素

```
127.0.0.1:6379[1]> rpush list 0rabc
(integer) 4
127.0.0.1:6379[1]> lrange list 0 -1
1) "03abc"
2) "02abc"
3) "01abc"
4) "0rabc"
```

### （3）lpop

拿出最左边的那个

ropo作用类似

```
127.0.0.1:6379[1]> lpop list
"03abc"
127.0.0.1:6379[1]> lrange list 0 -1
1) "02abc"
2) "01abc"
3) "0rabc"
```

### （4）lindex

通过下标获取某一个值

```
127.0.0.1:6379[1]> lindex list 0
"02abc"
127.0.0.1:6379[1]> lindex list 1
"01abc"
```

### （5）llen

返回列表长度

```
127.0.0.1:6379[1]> llen list
(integer) 3

```

### （6）lrem

表示移除list中的元素

lrem  key  数字  value

数字为正，表示先添加进去的

数字为负，表示从后向前，后添加进去的

```
127.0.0.1:6379[1]> LRANGE list 0 -1
 1) "hello"
 2) "right"
 3) "right"
 4) "right"
 5) "right"
 6) "hello"
 7) "hello"
 8) "hello"
 9) "02abc"
10) "01abc"
11) "0rabc"
127.0.0.1:6379[1]> lrem list 2 hello
(integer) 2
127.0.0.1:6379[1]> LRANGE list 0 -1
1) "right"
2) "right"
3) "right"
4) "right"
5) "hello"
6) "hello"
7) "02abc"
8) "01abc"
9) "0rabc"
```

### （7）rpoplpush 

rpoplpush list1  list2

将list1的最后一个添加到list2的最左边

```
127.0.0.1:6379[1]> rpoplpush list list2
"0rabc"
127.0.0.1:6379[1]> LRANGE list2 0 -1
1) "0rabc"
```

### （8） lset

指定位置添加元素

前提是该集合已被创建

```
127.0.0.1:6379[1]> lset list2 0 happy
OK
127.0.0.1:6379[1]> lrange list2 0 -1
1) "happy"

```

## 6、Set类型

### （1）sadd

```
127.0.0.1:6379[1]> SADD myset set01
(integer) 1
127.0.0.1:6379[1]> SMEMBERS myset
1) "set01"
127.0.0.1:6379[1]> SADD myset set02 set03
(integer) 2
127.0.0.1:6379[1]> SMEMBERS myset
1) "set01"
2) "set03"
3) "set02"

```

### （2）smembers

列举所有set成员信息

### （3）sismember

判断集合里是否有这个元素

```
127.0.0.1:6379[1]> SISMEMBER myset 1
(integer) 0
127.0.0.1:6379[1]> SISMEMBER myset set01
(integer) 1
```

###   （4）scard

返回set集合内的数量

```
127.0.0.1:6379[1]> SISMEMBER myset 1
(integer) 0
127.0.0.1:6379[1]> SISMEMBER myset set01
(integer) 1
```

### （5）srem

移除元素

```
SREM myset set01
```

### （6）srandmember

```
127.0.0.1:6379[1]> SRANDMEMBER myset
"set03"
127.0.0.1:6379[1]> SRANDMEMBER myset
"set02"
127.0.0.1:6379[1]> SRANDMEMBER myset
"set03"
127.0.0.1:6379[1]> SRANDMEMBER myset
"set03"
127.0.0.1:6379[1]> SRANDMEMBER myset
"set03"
```

### （7）smove

将一个set里的数据移到另一个set里

```
smove myset01 myset02 "hello"
```

### （8）sdiff

筛选出两个文件不同的地方

```
sdiff  key1   key2
```

### （9）sinter

取交集

```
127.0.0.1:6379[1]> sinter myse02 myset
1) "set02"
```

（10）sunion

取集合

```
127.0.0.1:6379[1]> sunion myse02 myset
1) "hello"
2) "set03"
3) "set02"
```

## 7、Hash

map集合，<key, value>

### （1）hget、hset

```
127.0.0.1:6379[1]> hset myhash name peng
(integer) 1
127.0.0.1:6379[1]> hget myhash name
"peng"
```

### （2）hgetall

查询所有键值对

```
127.0.0.1:6379[1]> hgetall myhash
1) "name"
2) "peng"
```

### （3）hdel

根据键值对的名字删除整个键值对

```
127.0.0.1:6379[1]> hdel myhash name
(integer) 1
```

### （4）hlen

查询 某个key里面有多少个键值对

```
hlen myhash
```

### （5）hexists

hash表的某个字段是否存在

```
hexists myhash name
```

### （7）hkeys

```
127.0.0.1:6379[1]> hkeys myhash
1) "name"
2) "age"
```

## 8、Zset

有序set

## 9、补充数据类型

Geospatia 主要记录地理位置

Hyperloglog 基数统计，比如用作统计网站浏览量，有0.85%的错误率



Bitmaps 位存储，主要是用来统计用户信息，活跃、不活跃，登录，打卡，只要是两个状态的，都可以用bitmaps