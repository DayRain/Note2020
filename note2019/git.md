# 1、配置用户信息

下载安装后，按照以下步骤配置用户信息

```xml
git config --global user.name ""
git config --global user.email ""
```

可用以下命令查看用户配置信息

```xml
git config --list
```

# 2、创建本地仓库并提交文件

1）、git bash

```xml
1、进入某个文件夹
2、生成仓库
git init
3、将某个文件加入仓库
git add 文件名
4、提交
git commit -m "注释"
5、查看状态
git status
```



2）、图形界面

略

# 3、工作流（GUI）

1）、添加提交

未暂存文件（添加）——》暂存文件（提交）    ——》     master库中

2）、需求变更

​      （1）、只更新到暂存区时，想撤销

如果有需求变更，但是还不完全确定，可以添加到暂存区

未暂存文件（添加）——》暂存文件（提交）

如果此时想撤销修改，则直接“丢弃”暂存区文件

​      （2）、已经提交后，想撤销

选择上一次提交（first commit）——》  右击    ——》重置当前分支到此次提交 ——》已提交的文件会回到未暂存文件——》丢弃

# 4、工作流（bash命令行）

## 1)、进入文件夹

![1557285952058](C:\Users\13760\Desktop\笔记\img\1557285952058.png)

git status 查看文件

untracked files 表示没有被跟踪的文件，依然在工作区。

## 2）、添加到暂存区

```
git add gui_demo.txt
```

![1557286097660](C:\Users\13760\Desktop\笔记\img\1557286097660.png)

## 3)、提交

```
git commit -m "first commit"
```

![1557286224869](C:\Users\13760\Desktop\笔记\img\1557286224869.png)

## 4)、临时变更需求

查看状态

![1557286329626](C:\Users\13760\Desktop\笔记\img\1557286329626.png)

提交到暂存区

![1557286380259](C:\Users\13760\Desktop\笔记\img\1557286380259.png)

此时，想要恢复到修改前

使用命令： git reset HEAD gui_demo.txt

![1557286535951](C:\Users\13760\Desktop\笔记\img\1557286535951.png)

清除工作区

![1557286702131](C:\Users\13760\Desktop\笔记\img\1557286702131.png)





如果是已经提交过的修改，想撤销的话

```
git log 
```

![1557286906780](C:\Users\13760\Desktop\笔记\img\1557286906780.png)

拿出 commit id   并执行命令

```
git reset --hard commitId
```

![1557286995466](C:\Users\13760\Desktop\笔记\img\1557286995466.png)

## 5）、删除文件

清空本地文件

```
git  rm  文件名
```

![1557287241158](C:\Users\13760\Desktop\笔记\img\1557287241158.png)

提交清空操作

![1557287324012](C:\Users\13760\Desktop\笔记\img\1557287324012.png)

# 5、远程仓库（Bash）

1）、生成ssh密钥

```
ssh-keygen -t rsa -C "1376034301@qq.com"
```

![1557289119598](C:\Users\13760\Desktop\笔记\img\1557289119598.png)

2)、找到密钥

![1557289230049](C:\Users\13760\Desktop\笔记\img\1557289230049.png)

![1557289270906](C:\Users\13760\Desktop\笔记\img\1557289270906.png)

3）、测试是否连通

```
ssh -T git@github.com
```

![1557289382112](C:\Users\13760\Desktop\笔记\img\1557289382112.png)

4)、创建一个新的仓库

```
echo "# bash_test" >> README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/DayRain/bash_test.git
git push -u origin master
```

​    如果再次修改文件并提交时，可以简化命令

```
git init
git add README.md
git commit -m "second commit"
git add  README.md
git push
```

