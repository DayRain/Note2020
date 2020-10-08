# 一、入门

## 1、解释运行

python文件以py结尾，例如 hello.py

例如，新建hello.py文件

键入

```
print("hello, world")
```

cmd打开文件，解释运行

```
python hello.py
```



# 二、语法

## 1、定义变量

解释型语言，弱语法，类似js

同一个变量可以表示多种数据类型

```
num = 1
print(num)
num = "123"
print(num)
num = 0.123
print(num)
```

## 2、关键字

通过命令查看所有关键字

```
import keyword
print(keyword.kwlist)
```

结果

```
['False', 'None', 'True', 'and', 'as', 'assert', 'async', 'await', 'break', 'class', 'continue', 'def', 'del', 'elif', 'else', 'except', 'finally', 'for', 'from', 'global', 'if', 'import', 'in', 'is', 'lambda', 'nonlocal', 'not', 'or', 'pass', 'raise', 'return', 'try', 'while', 'with', 'yield']
```

## 3、print函数

```
num = 10
print("数字是", num)  # 1，使用逗号是，会无视数据类型，在字符串后面加个空格，将变量贴在后面
# print("数字是" + num) 报错，num必须是字符串时，才可以用+拼装
print("数字是%d" % num)  # 和1的区别是，没有空格
print("数字是%s" % num)  # 不会报错，正常运行
print("数字是%f" % num)  # 不会报错，正常运行,但是结果会被转化为浮点数10.000000

num = 10.98721
print("身高是 %.1f" % num)  # 保留一位小数

num2 = 12
num3 = 13
num4 = 14
print("数字分别是 %f, %d, %d, %d" % (num, num2, num3, num4))  # 有多个变量需要输出的时候
```

## 4、算数运算符

除法运算与java中不太一样，/ 并不只是表示整除， 而是等同于数学中的除号。区别在于：两个整数相除，结果是浮点数

// 等同于 java中的 /

**表示幂运算

```
num = 10 / 20
print(num)  # 0.5

num = 10 / 1.2
print(num)  # 8.333333333333334

num = 10 // 20
print(num)  # 0

num = 10 % 20
print(num)  # 10

num = 10 ** 2
print(num)  # 100
```

*在运算符中表示乘法，在print方法中表示另一种含义

例如：下面的表达式表示，将“hello”打印10遍

```
print("hello" * 10)
```

## 5、逻辑运算符

逻辑运算符与java中虽然表述的含义是一样的，但是存在形式不太一样

and  等同于  &&

or  等同于 ||

not 等同于  !

python中表示肯定和否定也与java不太一样，开头字母均大写

```
print(True and False)  # False
```

## 6、数据序列

### 6.1、概述

这里的数据序列可以理解为数据类型，大概可以分为这几类

数字型

字符串型

列表  ["小明", 18, 175.6]    -------->list

元组  {'小张',  20 , '男'}     -------->tuple

字典 {'name': '小王', 'age':18 }    -------->dict



如何查看变量的数据类型？

利用type函数

```
num1 = 15
print(type(num1))
num2 = 10.1
print(type(num2))
num3 = "123"
print(type(num3))
num4 = '123'
print(type(num4))
num5 = ["小明", 18, 175.6]
print(type(num5))
num6 = {'小张',  20, '男'}
print(type(num6))
num7 = {'name': '小王', 'age': 18}
print(type(num7))
```

结果：

```
<class 'int'>
<class 'float'>
<class 'str'>
<class 'str'>
<class 'list'>
<class 'set'>
<class 'dict'>
```

### 6.2、字符串

> 表示方法

单引号双引号都可以，单引号中使用双引号时，会直接答应双引号

三引号内部既可以打印双引号，也可以打印单引号，包括换行符之类的

```
str1 = "sasd"
print(str1)
str1 = "abcdasdasd" \
       "sadas"
print(str1)
str1 = 'abc     say     "hello"'
print(str1)
str1 = """hello
world,世界
你好"""
print(str1)  # '''等同于"""
```

结果

```
sasd
abcdasdasdsadas
abc     say     "hello"
hello
world,世界
你好

```

如果是正则表达式，前面加个r即可

```
num = r"abc"
```

> 查询与统计

简而言之，对应于java中字符串的 charAt方法和length()方法

只不过python中可以通过下标直接获取字符串中的字符，len（）全局函数获取长度

count函数可以直接计算某个元素的值（注意是局部函数，不是全局）

```
str1 = "hello,world"
print(str1[0])
print(len(str1))
print(str1.count('o'))
```

```
h
11
2
```

> 字符串的判断与查找

```
str1 = "hello,world"
print(str1.startswith("hello"))  # 是否以hello开始
print(str1.endswith("world"))  # 是否以world结束

print(str1.find('e'))  # 返回下标
print(str1.find('p'))  # 没找到话返回 -1
print(str1.find("world"))  # 返回第一个字母所在的下标
print(str1.find('e', 0, 1))  # 从指定范围开始查找, 依然是包头不包尾
print(str1.index('o'))  # 返回查找到的第一个目标的下标
print(str1.index('p'))  # index没找到目标会直接报错，这就是index和find的区别
```

> 替换

```
str1 = "hello, world, world, world, nihao"
print(str1.replace('world', 'go'))
print(str1.replace('world', 'go', 1))  # 指定替换次数
```

> 拼接

区别于 +

```
str1 = "hello, i am world"
print(str1.split('o'))
print(str1.splitlines())

print(str1.join("hello"))
print("hello".join(str1))
```

```
['hell', ', i am w', 'rld']
['hello, i am world']
hhello, i am worldehello, i am worldlhello, i am worldlhello, i am worldo
hhelloehellolhellolhelloohello,hello helloihello helloahellomhello hellowhelloohellorhellolhellod
```

> 切片

截取子串

```
str1 = "hello,python"
print(str1[0:5:1])  # 包头不包尾
print(str1[0:5])  # 步长为1可以省略
print(str1[6:13])
```

### 6.3、list

> 初始化

两种方式：

1、指定初始值

2、list（）方法创建空列表

```
list1 = [123, "abc", True]
list2 = list()
print(list1)
print(list2)
```

> 常规用法

```
list1 = [123, "abc", True]
list2 = list()
print(list1)
print(list2)
print(list1[0])
print(list1[-1])  # 倒数第一个
print(list1[-2])  # 倒数第二个

# 添加
list2.append("hello")
print(list2)

# list2.insert(5, ",")  指定位置插入字符

# 删除
list2.append("world")
print(list2)
list2.pop()
print(list2)
list2.pop(0)  # 删除指定位置
print(list2)
```



### 6.4、tuple

> 概述



tuple是不可变的list

```
tuple1 = (1, 2, 3, "123")
print(tuple1)
print(len(tuple1))
```

如果只有一个元素，为了避免歧义（与数学上的括号），请这么定义

```
tuple1 = (1,)
```

### 6.5、dict

> 定义

```
my_dict = {'name': 'xiaoming', 'age': 18, 'gender': '女'}
print(type(my_dict))
```



> 基本使用

```
my_dict = {'name': 'xiaoming', 'age': 18, 'gender': '女'}
print(type(my_dict))

print(my_dict['name'])  # 如果没有，会报错
print(my_dict.get('name'))  # 如果没有，不会报错，返回None
print(my_dict.get('name', '没有找到'))  # 如果没有，不会报错，返回指定内容


# 新建或修改
my_dict['name'] = 'xiaogang'  # 如果存在，则是修改
my_dict['hobby'] = 'xuexi'  # 如果不存在，则是添加
print(my_dict)
```

## 7、流程控制

### 1、if

```
import random
computer = random.randint(1, 3)  # 范围：1到3，包含头部和尾部

person = input("请猜拳，1拳头，2剪刀， 3布")
person = int(person)
print("电脑出了" + str(computer))

if computer == person:
    print("打了个平手")
elif (computer - person) == -1 or (computer - person) == 2:
    print("电脑赢了")
else:
    print("你赢了")

```

### 2、while 

### 3、for



# 三、函数

## 1、基本使用

```
name = 123


def hello():
    """
    函数的注释，在这里写上函数的使用方法
    :return:
    """
    print("i will say hello")
    print("you say what ?")
    print("hello")
    print(name)

hello()

help(hello)
```

## 2、多返回值

```
def hello():
    return [1, 2, 3]


a, b, c = res = hello()
print(a)
print(b)
print(c)

```

