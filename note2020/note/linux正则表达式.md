# 1、基本搜索

```
#搜索带有java的行数 -n表示带行号
grep -n 'java' regular_express.txt

#搜索不带java的行数
grep -vn 'java'  regular_express.txt

#忽略大小
grep -in 'java' regular_express.txt
```



# 2、[ ] 

[abc]就算里面的字符再多，也只表示一个字符。

```
#搜寻文件中的test或者tast，-n表示带行号
grep -n 't[ae]st' regular_express.txt

[root@localhost regular_expre]# grep -n 't[ae]st' regular_express.txt 
8:I can't finish the test.
9:Oh! The soup taste good.

```

# 3、^

^ 在不同的情况下，表示的含义不一样。

在中括号里表示取反

```
#包含oo
grep -n 'oo' regular_express.txt

#非g开头
grep -g '[^g]oo' regular_express.txt
```

在中括号外表示首行的意思

```
 #The开头
 grep -n '^The' regular_express.txt
```

在第二种语义下，与之相对的是$

```
#表示以点结尾的行
grep -n '\.$' regular_express.txt

#表示空白行
grep -n '^$' regular_express.txt
```



# 4、-

短杠表示范围

```
#小写字母开头，后面跟oo
grep -n '[a-z]oo' regular_express.txt
#字母数字开头，后面跟oo
grep -n '[A-Za-z0-9]oo' regular_express.txt
```

```
a-z 等同于 [:lower:]
所以 [a-z] 等同于 [[:lower:]]
```

# 5、. 和 * 的区别

都是占位符

点表示一定占一个位置

*表示占一个或者多个位置。但是正则里面的 星号和万用字符的星号不一样,它表示是对它前面一个字符的拓展。

例如

```
‘ooo*’要拆下来理解，分为两部分
'oo'和'o*'前者表示已经确定的两个o，后者表示零个或者无数个o，结合起来看就是，两个或者两个以上连续的o
```



```
#表示含有good
grep -n 'g..d' regular_express.txt

#表示至少两个o
grep -n 'ooo*' regular_express.txt

#如果想表示任意字符，比如两个g之间可以有任何字符，可以通过组合'.*'来实现，表示可能有零个或者有多个点，而点表示任意一个数，所以结合起来
grep -n 'g.*g' regular_express.txt

#任意数字
grep -n '[0-9][0-9]*' regular_express.txt

#只包含数字
grep -n '^[0-9][0-9]*$' regular_express.txt
```

# 6、{}

大括号限定连续的范围，需要和 * 联系起来

```
#表示两个或者两个以上的o
grep -n 'ooo*' regular_express.txt

#和上面一个没啥区别，表示两个或两个以上的o
grep -n 'o\{2\}' regular_express.txt

#两个到五个
grep -n 'o\{2, 5\}' regular_express.txt
```

# 7、sed工具使用

## 7.1 删除与新增

删除（d，delete）

```
#删除第二行
#nl表示将目标内容加上行号后，写到标准输出
nl /etc/passwd | sed '2d'

#删除二到五行
nl /etc/passwd | sed '2,5d'

#删除第二行到末尾
nl /etc/passwd | sed '2,$d'
```

新增（a, add）

目标行的后面一行

```
#在第2行和第3行之间加上drink milk
nl /etc/passwd | sed '2a drink milk'

#加上多行
nl /etc/passwd | sed '2a drink milk / tea / coco'
```

新增（i）

目标行的前面一行

```
#在第2行和第1行之间加上 drink milk
nl /etc/passwd | sed '2i drink milk'
```

## 7.2 以行为单位的取代与显示功能

替换

```
#将指定行数替换为自定义内容
#将第2到5行替换为MySelf Define
nl /etc/passwd | sed '2, 5c MySelf Define'
```

显示

```
 #第五到七行
 nl /etc/passwd | sed -n '5, 7p
```

## 7.3 永久修改

-i

可以修改源文件，属于危险操作

```
 #每一行结尾若是.则改成！
 sed -i 's/\.$/\!/g' regular_express.txt
 #最后一行加上一句话：This is a test。
 #$表示最后一行，a表示新增
 sed -i '$a # This is a test' regular_express.txt 
```

