

# 1.配置用户信息

## 1.1配置全局信息

通常在刚安装完git系统后，需要配置一下全局信息。

```
git config --global user.name "DayRain"
git config --global user.email "1376034301@qq.com"
```

# 2.git提交的流程

## 2.1 本地提交

```
1、cd到当前目录
2、初始化
git init
3、添加至暂存区
git add .    #添加所有文件
git add 文件名 #添加指定文件
4、提交文件
git commit -m  “注释”
```

## 2.2  版本回退的流程

```
git log   #查看提交信息
git reset --hard  commitId    #回到指定id所对应的版本
```

## 2.3 版本复原的流程

```
git reflog  #查看命令历史，可以找到之前版本的id
git reset --hard commitId
```

## 2.4 关联远程仓库

```
git remote add origin  #地址
git push -u origin master #第一次提交
git push origin master #推送最新的修改
```



# 4.常用命令

## 3.1 git status

查看git状态

## 3.2 git diff

查看文件修改后，与之前文件的不同之处

git diff HEAD -- 文件名

## 3.3 git log

查看提交记录，一般用于版本回退时使用

## 3.4 git reflog

查看提交记录，一般用于版本复原时使用

## 3.5 git restore

丢弃工作区尚未提交的修改，总的来说就是用版本库的代码来替换工作区的代码

git restore -- 文件名

git checkout -- 文件名         与之具有相同的功能

## 3.6 git reset --hard 

git reset --hard commitId 可以版本回退

## 3.7 git reset HEAD

git reset HEAD 文件名     把暂存区的修改撤销掉（已经add的文件）

此时该文件仍然在工作区，如果想要丢弃掉，git checkout -- 文件名

# 5.分支管理

## 5.1 基本流程

```
git checkout -b dev     #创建并切换到dev分支， 等同于（git branch dev     git checkout dev）
（创建分支，在新版的git可以用  git switch -c dev）
git checkout master     #切换回主分支
（新版git，切换分支可以用 git witch）
git merge dev           #合并分支
git branch -d dev       #删除dev分支
```

## 5.2 bug分支

如果之前写的代码需要紧急修改bug，则需要将当前代码存起来。

```
git stash
创建新的分支，修改bug
git stash list     #查看刚才的工作现场
git stash apply    #恢复工作现场，但是不删除stash里面的内容，需要命令 git stash drop删除
git stash pop      #恢复的同时，删除stash里面的内容

git cherry-pick 4c805e2  #可以将对其他分支的修改，拷贝一份到当前的分支，比如修改bug的时候。
```

## 5.3  推送分支

```
git push origin master
git push origin dev
```

## 5.4 抓取分支

```
git clone  地址   （默认情况下，只能看到本地的master分支）

git checkout -b  dev  origin/dev   (创建远程仓库中的dev到本地，)

git push orgin dev      （将dev推送到远程服务器上）
```

## 5.4 提交分支的时候，解决冲突问题

```
git push orgin dev  
上述命令报错的时候，可以远程仓库pull下来
git pull 
如果pull失败，可能是因为本地dev和远程dev没有关联
git branch --set-upstream-to=origin/dev dev   #设置关联
git pull
虽然pull成功，但是可能会出现冲突，需要手动解决，解决冲突后，提交
git commit -m "fix env conflict"
git push origin dev

```



```
因此，多人协作的工作模式通常是这样：

首先，可以试图用git push origin <branch-name>推送自己的修改；

如果推送失败，则因为远程分支比你的本地更新，需要先用git pull试图合并；

如果合并有冲突，则解决冲突，并在本地提交；

没有冲突或者解决掉冲突后，再用git push origin <branch-name>推送就能成功！

如果git pull提示no tracking information，则说明本地分支和远程分支的链接关系没有创建，用命令git branch --set-upstream-to <branch-name> origin/<branch-name>。

这就是多人协作的工作模式，一旦熟悉了，就非常简单。
```

