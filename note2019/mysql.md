# 一、相关命令

启动命令：

```bash
net start mysql
```

关闭命令

```
net stop mysql
```

如果需要设置开机启动，或者要手动，进入服务设置即可

```
win+r
services.msc
#找到mysql一项属性修改即可
```

登录mysql

```
mysql -u root -p
#输入密码
```

mysql是一个登录命令。-u参数，跟上用户名的意思，-p指密码password的意思。

查看数据库

```
show databases；
```

创建数据库

```
create database mydb01;
```

![1551160251800](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551160251800.png)

使用某个数据库

```
use mydb01;
```

查看当前数据库所有的表

```
show tables;
```

退出mysql cmd环境

```bash
ctrl + C # 终止
```

# 二、语法

## 1.单引号和反单引号区别

单引号：字符

反单引号：  为了区分普通字符和保留字的区别

## 2、定义主键的几种方法

### （1）直接定义

```
CREATE TABLE employee2(
    id INT(11) PRIMARY KEY,
    NAME VARCHAR(32),
    deptId INT(11),
    salary INT(11)
)
```

### （2）最后定义主键

```
CREATE TABLE employee3(
    id INT(11),
    NAME VARCHAR(32),
    deptId INT(11),
    salary INT(11),
    PRIMARY KEY(id)
)

```

### （3）联合主键

```
CREATE TABLE employee4(
    id INT(11),
    NAME VARCHAR(32),
    deptId INT(11),
    salary INT(11),
    PRIMARY KEY(id,deptId)
)
```

## 3、主键约束

### （1）建库建表

```
#支持 exists if not exists

CREATE DATABASE IF NOT EXISTS mydb02;
USE mydb02;

#创建表
CREATE TABLE teacher(
   t_id INT(11) PRIMARY KEY,
   t_name VARCHAR(255),
   t_age INT(3)
);
```

### （2）外键约束

主表：关联字段中，主键所在的表是主表。

从表：关联字段中，外键所在的表是从表。



```
##创建学生表指向老师表
CREATE TABLE student(
   s_id INT(11) PRIMARY KEY,
   s_name VARCHAR(255),
   t_id INT(11),
   CONSTRAINT fk_s_t FOREIGN KEY (t_id) REFERENCES teacher(t_id)   
);
##student指向teacher，所以teacher主表，student从表。
#向student表插入数据时，会检查teacher表有没有对应id。
```

### （3）非空约束

```
CREATE TABLE `user`(
   u_id INT(11)PRIMARY KEY,
   u_name VARCHAR(32) NOT NULL
);
```

### （4）唯一约束

```
##唯一
CREATE TABLE `user2`(
   u_id INT(11)PRIMARY KEY,
   `name` VARCHAR(32) NOT NULL,
   phone INT(11)UNIQUE
);
```

### （5）默认值约束

```
##默认值
CREATE TABLE girl_student(
   g_id INT(11)PRIMARY KEY,
   gender VARCHAR(3) DEFAULT '女'
);
```

### （6）自增长约束

```
##自增长约束
CREATE TABLE auto_increment_table(
   id INT(11)PRIMARY KEY AUTO_INCREMENT,
   `name` VARCHAR(32)
);
```

## 4、查看表结构

###   （1）控制台乱码问题

![1551257690295](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551257690295.png)

原因：控制台编码是gbk，数据库是utf-8，所以不是数据库乱码，只是显示的问题

方案：改变命令程序编码。

![1551257990371](C:\Users\13760\AppData\Roaming\Typora\typora-user-images\1551257990371.png)

set character_set_results = gbk;

(下次启动时，还需要重新设置)

### （2）修改表名

```
alter table table_name1 rename to table_name2;
```

### （3）修改数据类型

​    注释：有可能会失败，比如varchar转到int，失败

```
alter table user modify u_id varchar(25);
```

### （4） 修改字段

```
#修改列名
ALTER TABLE USER CHANGE u_id user_id VARCHAR(25);
#后面的数据类型必须加上


#添加字段address
ALTER TABLE USER ADD address VARCHAR(500);
ALTER TABLE USER ADD COLUMN address VARCHAR(500);

#删除字段address
ALTER TABLE USER DROP address;

#添加字段在最前面
ALTER TABLE USER ADD address VARCHAR(500) FIRST;

#添加字段在id后面
ALTER TABLE USER ADD address VARCHAR(500) AFTER u_id;
```

## 5、增删改

```
CREATE TABLE student(
   s_id INT PRIMARY KEY,
   s_name VARCHAR(255)NOT NULL,
   s_gender INT(1)DEFAULT 0, #男为1，女为0
   birthday DATETIME,
   s_phone VARCHAR(11)UNIQUE
);

#插入数据
##[方式一：部分列插入]
INSERT INTO student(s_id,s_name,s_gender)VALUES(1,'小李',0);
##[方式二：所有列都插入]
INSERT INTO student VALUES(2,'小张',1,'2018-1-1 12:12:12',1356465656);
##[方式三：mysql支持多条同时插入]
INSERT INTO student VALUES(3,'小张',1,'2018-1-1 12:12:12',1356465666),(4,'小张',1,'2018-1-1 12:12:12',1236465656);

##删除数据的三种方法
#删除表
DROP TABLE student;
##删除数据(本质上一条一条的删除的)
DELETE FROM student;
###整个全部删除
TRUNCATE student;
####条件删除
DELETE FROM student WHERE s_id=1;
DELETE FROM student WHERE s_id=5 AND NAME='小张';
DELETE FROM student WHERE s_id=5 OR NAME='小张';   
DELETE FROM student WHERE s_id != 5;
DELETE FROM student WHERE s_name IS NOT NULL;

#修改数据
UPDATE student SET s_gender=1;##不加条件，修改所有行的列的数据。
UPDATE student SET s_gender=1 WHERE s_id=1;
```

## 6、单表查询

