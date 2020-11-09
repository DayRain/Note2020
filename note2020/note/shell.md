# 一、基础命令

## 1、查看系统支持的shell

```
[root@localhost /]# cat /etc/shells
/bin/sh
/bin/bash
/usr/bin/sh
/usr/bin/bash
```

## 2、查看系统默认shell

```
[root@localhost /]# echo $SHELL
/bin/bash
```

## 3、查看环境变量PATH

```
[root@localhost /]# echo $PATH
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin

[root@localhost /]# echo ${PATH}
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin
```

为什么不是 echo PATH?

```
[root@localhost /]# echo PATH
PATH
```

## 4、查看所有的环境变量

```
env
```

## 5、语言编码相关

### 5.1查看系统支持编码

```
locale -a
```

### 5.2查看当前语言

```
[root@localhost ~]# cat /etc/locale.conf 
LANG=zh_CN.UTF-8

```

### 5.3切换成英文编码

```
[root@localhost ~]# export LC_ALL=en_US.ytf8;locale
-bash: 警告:setlocale: LC_ALL: 无法改变区域选项 (en_US.ytf8): 没有那个文件或目录
-bash: 警告:setlocale: LC_ALL: 无法改变区域选项 (en_US.ytf8): 没有那个文件或目录
locale: Cannot set LC_CTYPE to default locale: No such file or directory
locale: Cannot set LC_MESSAGES to default locale: No such file or directory
locale: Cannot set LC_ALL to default locale: No such file or directory
LANG=zh_CN.UTF-8
LC_CTYPE="en_US.ytf8"
LC_NUMERIC="en_US.ytf8"
LC_TIME="en_US.ytf8"
LC_COLLATE="en_US.ytf8"
LC_MONETARY="en_US.ytf8"
LC_MESSAGES="en_US.ytf8"
LC_PAPER="en_US.ytf8"
LC_NAME="en_US.ytf8"
LC_ADDRESS="en_US.ytf8"
LC_TELEPHONE="en_US.ytf8"
LC_MEASUREMENT="en_US.ytf8"
LC_IDENTIFICATION="en_US.ytf8"
LC_ALL=en_US.ytf8
```

### 5.4切换成中文编码

```
[root@localhost ~]# export LC_ALL=zh_CN.UTF-8;locale
LANG=zh_CN.UTF-8
LC_CTYPE="zh_CN.UTF-8"
LC_NUMERIC="zh_CN.UTF-8"
LC_TIME="zh_CN.UTF-8"
LC_COLLATE="zh_CN.UTF-8"
LC_MONETARY="zh_CN.UTF-8"
LC_MESSAGES="zh_CN.UTF-8"
LC_PAPER="zh_CN.UTF-8"
LC_NAME="zh_CN.UTF-8"
LC_ADDRESS="zh_CN.UTF-8"
LC_TELEPHONE="zh_CN.UTF-8"
LC_MEASUREMENT="zh_CN.UTF-8"
LC_IDENTIFICATION="zh_CN.UTF-8"
LC_ALL=zh_CN.UTF-8

```

## 6、设置别名

例如经常要使用 ps -ef | grep 命令来查看程序的进程号，可以给他设置别名

```
alias pg='ps -ef|grep '
#下次使用的时候可以直接加进程名
pg java
pg redis
```

## 7、读取配置

有时候写了配置文件后，想要在当前的shell环境立马生效。

需要通过source或者小数点（.）命令。

```
#例如修改完环境变量
source /etc/profile
#或者
. /etc/profile
```

## 8、命令执行的判断依据

判断依据有：

； ||   &&

### 8.1 ；

不考虑指令的相关性，连续执行。

### 8.2 &&

cmd1  && cmd2

- 如果cmd1执行成功，则开始执行cmd2
- 如果cmd1执行失败，则不执行cmd2

### 8.3 ||

cmd1 || cmd2

- 如果cmd1执行成功，则不执行cmd2
- 如果cmd1执行失败，则开始执行cmd2

### 8.4 实战

1、如果某个文件夹不存在，则创建该文件夹

```
[root@localhost shell]# ls /root/shell/test || mkdir -p /root/shell/test
ls: 无法访问'/root/shell/test': 没有那个文件或目录
[root@localhost shell]# ls
catfile  param.sh  test
[root@localhost shell]# pwd
/root/shell
```

2、判断某个文件夹是否存在

```
[root@localhost shell]# ls /root/shell/test && echo "exist" || echo "not exist"
exist

#不可写成
ls /root/shell/test || echo "exist" && echo "not exist"
#会出现同时打印 exist和not exist的情况
```



# 二、语法相关

# 三、实战

## 1、命令行输入

需求：

让用户输入姓名，并控制台输出。

```
read -p "please input your first name: " firstname
read -p "please input your last name: " lastname
echo -e "\nYour full name is: ${firstname} ${lastname}"
```

## 2、根据日期创建文件

需求：

例如数据库备份等，为了保留以前的文件，并区分新旧文件，可以考虑创建文件的时候加上时间。

编写一个脚本，让用户输入一个字符串，作为文件名的开头，文件名的结尾处加上格式化后的时间。

```
#! /bin/bash
#读取用户输入的文件起始名
echo -e "I will use 'touch' command to create 3 files"
read -p "Please input you filename:" fileuser
#为了避免使用者随意按enter，利用变量功能分析文件名是否有设置？
filename=${fileuser:-"filename"}  #如果用户什么都没输入，默认文件开头为filename
#通过date指令来获取所需要的文件名
date1=$(date --date='2 days ago' +%Y%m%d)
date2=$(date --date='1 days ago' +%Y%m%d)
date3=$(date +%Y%m%d)
file1=${filename}${date1}
file2=${filename}${date2}
file3=${filename}${date3}

#创建文件
touch "${file1}"
touch "${file2}"
touch "${file3}"
```

程序运行结果，在同目录创建了三个带日期的文件

## 3、source和bash（sh）的区别

source执行会使脚本中的变量在当前的bash也生效。

bash（sh）会开一个新的bash执行，结果与当前bash无关

## 4、test命令

```
#判断文件名是否存在
test -e 文件名

#判断文件名是否存在且为文件
test -f 文件名

#判断文件名是否存在且为目录
test -d 文件名
```

