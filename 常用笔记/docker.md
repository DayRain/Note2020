# 1、环境准备

## 1.1 检查linux版本

支持centos6.5以上（最好是7以上）

要求内核版本为2.6.32-431或者更高版本，输入命令查看：

```
uname -r
```

## 1.2 概念

镜像（image）：类似于“class”

容器（container）：类似于对象，只要有一个镜像，就可以new多个容器。可以把每一个容器都看作是精简版的linux环境（包括root用户权限、进程空间、用户空间和网络空间等）和运行在其中的程序。

仓库（repository）：集中存放镜像的场所，最大的仓库为

https://hub.docker.com

## 1.3 安装

官网教程：https://docs.docker.com/engine/install/centos/

安装前准备：建议更换yum源为国内镜像

安装如下：

### 1.3.1添加docker仓库至yum

```
yum install -y yum-utils

yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

### 1.3.2安装最新版

```
sudo yum install docker-ce docker-ce-cli containerd.io
```



ceontos8

```
sudo yum install docker-ce docker-ce-cli containerd.io --nobest
```



### 1.3.3安装指定版本s

```
查看版本列表
yum list docker-ce --showduplicates | sort -r

将version——string替换为相应版本
yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io
```

### 1.3.4 镜像加速



阿里云镜像加速官网：

https://cr.console.aliyun.com/

阿里云加速地址：

```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://i0szhtu8.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

腾讯云加速

```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://mirror.ccs.tencentyun.coms"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```



# 2、基本命令

## 2.1 images相关

### 2.1.1 docker images

查看所有镜像

```
REPOSITORY 镜像的仓库源
TAG        镜像的标签
IMAGE ID   镜像的id
CREATED    镜像的创建时间
SIZE       镜像的大小

```

条件搜索，过滤部分镜像

docker -mysql --filter=STARS=9000         #表示只搜索stars大于9000的

### 2.1.2 pull

docker pull  镜像名【：tag】

docker pull mysql    #如果不指定tag，则表示默认下载最新的

```
[root@iZwz9cc9df2x1jeu9hkhltZ ~]# docker pull mysql
Using default tag: latest
latest: Pulling from library/mysql
6ec8c9369e08: Pull complete 
177e5de89054: Pull complete 
ab6ccb86eb40: Pull complete 
e1ee78841235: Pull complete 
09cd86ccee56: Pull complete 
78bea0594a44: Pull complete 
caf5f529ae89: Pull complete 
cf0fc09f046d: Pull complete 
4ccd5b05a8f6: Pull complete 
76d29d8de5d4: Pull complete 
8077a91f5d16: Pull complete 
922753e827ec: Pull complete 
Digest: sha256:fb6a6a26111ba75f9e8487db639bc5721d4431beba4cd668a4e922b8f8b14acc
Status: Downloaded newer image for mysql:latest
docker.io/library/mysql:latest

```

注意：这里的 docker pull mysql    等价于    docker pull  docker.io/library/mysql:latest

如果指定版本（标签号）：

```
[root@iZwz9cc9df2x1jeu9hkhltZ ~]# docker pull mysql:5.7
5.7: Pulling from library/mysql
6ec8c9369e08: Already exists 
177e5de89054: Already exists 
ab6ccb86eb40: Already exists 
e1ee78841235: Already exists 
09cd86ccee56: Already exists 
78bea0594a44: Already exists 
caf5f529ae89: Already exists 
4e54a8bcf566: Pull complete 
50c21ba6527b: Pull complete 
68e74bb27b39: Pull complete 
5f13eadfe747: Pull complete 
Digest: sha256:97869b42772dac5b767f4e4692434fbd5e6b86bcb8695d4feafb52b59fe9ae24
Status: Downloaded newer image for mysql:5.7
docker.io/library/mysql:5.7
```

注意：这里的标签号是从官网查的：https://hub.docker.com/_/mysql， 不是瞎填的

说明：第二次下载的时候，很多是Already exists ，是因为docker是分层下载的，很多东西如果之前已经下载完成了，就不用继续下载了，可以共用。

### 2.1.3 docker rmi

删除镜像

删除镜像前，可以 docker image查询 id

```
[root@iZwz9cc9df2x1jeu9hkhltZ ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
mysql               5.7                 8679ced16d20        9 days ago          448MB
mysql               latest              e3fcc9e1cc04        9 days ago          544MB
hello-world         latest              bf756fb1ae65        7 months ago        13.3kB
```

然后使用命令：

```
docker rmi -f  e3fcc9e1cc04 
```

如果想要删除多个容器

```
docker rmi -f $(docker images -aq)
```

## 2.2container相关

### 2.2.1 docker run 

产生一个新的容器，而docker start 是

```
docker run [可选参数] image
--name="Name" 容器名字 ，用来区分容器
-d  后台方式运行
-it  使用交互方式运行， 进入容器查看内容
-p   指定容器的端口 -p 8080：8080
       -p ip:主机端口：容器端口
       -p 主机端口：容器端口（常用）
       -p 容器端口
       容器端口
 -P   随机指定端口
```

进入centos

```
docker run -it centos /bin/bash

exit
```

### 2.2.2 docker ps 

```
docker ps      当前正在运行的容器

-a             当前正在运行的容器 + 历史运行过的容器
-n=?           显示最近创建的容器
-q             只显示容器
```

### 2.2.3 退出容器

```
exit #直接容器停止并退出
ctrl+P+Q 容器不停止退出
```

### 2.2.4删除容器

```
docker rm 容器id               #删除指定容器
docker rm -f  $(docker ps -aq)  #删除所有容器
```

### 2.2.4启动和停止容器

```
docker start 容器id      
docker restrat 容器id
docker stop 容器id      #停止正在运行的容器
docker kill 容器id       #强制停止当前容器
```

### 2.2.5查看容器日志

```
docker logs -tf d8b6b342814e
docker logs -tf d8b6b342814e --tail 10
```

### 2.2.6 查看容器中的进程信息

要求：该容器必须正在运行

```
docker top d8b6b342814e
```

### 2.2.7查看容器中的元数据

```
docker inspect d8b6b342814e
```

### 2.2.8 进入当前正在运行的容器

方式一：

```
docker exec -it d8b6b342814e /bin/bash
```

方式二：

```
docker attach d8b6b342814e
```

区别：

方式一 是开启一个新的终端

方式二 接着正在执行任务的那个终端

### 2.2.9 将容器内的文件拷贝到容器外

```
docker cp d8b6b342814e:/home/hello.txt /home
```

### 2.2.10 容器生成镜像

```
docker commit -a "个人信息" -m "说明" [imageId]  [新的镜像名]:v1 
```

### 2.2.11 容器间映射（--link）

--link会在hosts文件加一条映射



启动容器，第一个mynginx指的是进入容器后，输入mynginx就可以表示 后面一个 mynginx的ip

```
docker run -dit --link mynginx:mynginx alpine
```



# 3、安装软件

## 3.1 安装nginx

下载镜像

```
docker pull nginx
```

运行容器

```
docker run -d --name "nginx01" -it -p 80:80 nginx
```

查看ip的小技巧

如果ifcofig命令不能用

```
cat /etc/hosts
```



## 3.2 安装tomcat

方式一：用完即删

```
 docker run -it --rm tomcat:9.0   #该方法一般用来测试，用完后会自动删除
```

停止后，docker ps就没有了

方式二：常规方式

```
docker pull tomcat:9.0

docker run -d --name 'tomcattest01' -p 8080:8080 tomcat

```

访问后会出现404，是因为webapp下没有文件，docker安装的tomcat是最精简版的。



## 3.3 安装图形化工具

```
docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer
```



## 3.4 安装mysql

```
docker pull mysql:5.7
```



```
docker run -d --name mymysql -p 3306:3306 -v /home/dayrain/docker/mysql/conf:/etc/mysql/conf.d -v /home/dayrain/docker/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Ph0716 mysql:5.7 
```

## 3.5安装redis

1、下载镜像

```
docker pull redis:6
```

2、创建本地挂载目录

```
/home/dayrain/docker/redis/conf

/home/dayrain/docker/redis/data
```

3、写配置文件

位于conf目录下的redis.conf

```
#bind 127.0.0.1 
protected-mode no
appendonly yes
requirepass 密码
```

4、运行redis

```
docker run -d --name myredis -v /home/dayrain/docker/redis/data:/data -v /home/dayrain/docker/redis/conf/redis.conf:/etc/redis/redis.conf  -p  6379:6379 redis:6 redis-server /etc/redis/redis.conf
```

5、登录

```
 docker exec -it myredis redis-cli
```

如果需要验证

```
auth 密码
```



# 4、容器数据卷

## 4.1 简单操作

容器的持久化和同步操作，可将数据永久保存在本地，并且可以实现容器之间的数据共享。

实现：

将本机home下的ceshi目录，挂载到容器的home下。之后本机的目录与容器的home会共享空间

```
docker run -it -v /home/ceshi:/home 831691599b88 /bin/bash
```



## 4.2 匿名挂载

```
docker run -d -p 80:80 --name nginx01 -v /etc/nginx nginx

```

查看数据卷

```
docker volume ls

```



## 4.3 具名挂载

```
 docker run -d -P --name nginx02 -v juming-nginx:/etc/nginx nginx

```

查看数据卷

```
docker volume ls
```

查看数据卷位置

```
docker volume inspect juming-nginx
```

## 4.4 指定权限

```
#只读
docker run -d -P --name nginx02 -v juming-nginx:/etc/nginx:ro nginx

#可读可写
docker run -d -P --name nginx02 -v juming-nginx:/etc/nginx:rw nginx
```

## 4.5  实现mysql容器间数据共享

```
docker run -d -P --name mysql02 -v /etc/mysql/conf.d -v /var/lib/mysql -e MYSQL_ROOT_PASSWORD=Ph0716 mysql:5.7

docker run -d -P --name mysql03 --volumes-from mysql02  -e MYSQL_ROOT_PASSWORD=Ph0716 mysql:5.7

```

## 4.6 删除数据卷

```
docker volume rm my-vol
```

删除无主数据卷（会删除所有，慎用）

```
docker volume prune
```

# 5、DockerFIle

dockerfile是用来构建docker镜像的文件，是一个命令参数脚本

## 5.1 构建步骤

1、编写一个dockerfile文件

2、dockerbuild构建成为一个镜像

3、docker run 运行镜像

4、docker push发布镜像（DockerHub、阿里云镜像仓库）

## 5.2  指令

```
FROM        #基础镜像，一切从这构建
MAINTAINER  #镜像谁写的，姓名+邮箱
RUN         #镜像构建的时候需要运行的命令
ADD         #添加内容，比如添加一个tomcat
WORKDIR     #镜像的工作目录
VOLUME      #挂载目录
EXPOST      #保留端口配置
CMD         #指定容器启动时运行的命令，只有最后一个有效，可以被替代
ENTRYPOINT  #指定容器启动时运行的命令，可以追加命令
ONBUILD     #触发指令，了解即可
COPY        #类似ADD，将文件拷贝到镜像中
ENY         #构建的时候设置环境变量
```

## 5.3 实战：制作CentOS镜像

> 编写dockerfile文件

```
FROM centos
MAINTAINER dayrain<1376034301@qq.com>

ENV MYPATH /usr/local
WORKDIR $MYPATH

RUN yum -y install vim
run yum -y install net-tools

EXPOSE 80

CMD echo $MYPATH
CMD echo "----end----"
CMD /bin/bash

```

> 编译构建

点不要忘了加

```
 docker build -f mydockerfile-centos -t mycentos:0.1 .
```

> 创建容器

```
docker run -it  mycentos:0.1
```

## 5.4 实战：制作tomcat镜像

1、第一步：将jdk、tomcat的压缩包拷贝到当前文件夹

/home/dayrain/build/tomcat

```
apache-tomcat-9.0.37.tar.gz 
jdk-8u261-linux-x64.tar.gz

```

2、在当前文件夹写Dockerfile

无注释版

```
FROM centos
MAINTAINER  dayrain<1376034301@qq.com>

COPY readme.txt /usr/local/readme.txt

ADD jdk-8u261-linux-x64.tar.gz /usr/local/
ADD apache-tomcat-9.0.37.tar.gz /usr/local/

RUN yum -y install vim
ENV MYPATH /usr/local
WORKDIR $MYPATH

ENV JAVA_HOME /usr/local/jdk1.8.0_261
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV CATALINA_HOME /usr/local/apache-tomcat-9.0.37
ENV CATALINA_BASH /usr/local/apache-tomcat-9.0.37
ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin
ENV JAVA_OPTS "-Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080



CMD /usr/local/apache-tomcat-9.0.37/bin/startup.sh && tail -F /url/local/apache-tomcat-9.0.37/bin/logs/catalina.out


```

有注释版

```
FROM centos
MAINTAINER  dayrain<1376034301@qq.com>

#如果需要把文件拷贝到容器内，可以用COPY	
COPY readme.txt /usr/local/readme.txt

#会自动解压
ADD jdk-8u261-linux-x64.tar.gz /usr/local/
ADD apache-tomcat-9.0.37.tar.gz /usr/local/

#安装一下vim
RUN yum -y install vim

#这两步设置进入容器后，默认在的目录
ENV MYPATH /usr/local
WORKDIR $MYPATH

#设置各种环境变量
ENV JAVA_HOME /usr/local/jdk1.8.0_261
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV CATALINA_HOME /usr/local/apache-tomcat-9.0.37
ENV CATALINA_BASH /usr/local/apache-tomcat-9.0.37
ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin
#解决linux上tomcat启动的问题
ENV JAVA_OPTS "-Djava.security.egd=file:/dev/./urandom"


#暴露端口
EXPOSE 8080


#启动容器的时候同时启动tomcat
CMD /usr/local/apache-tomcat-9.0.37/bin/startup.sh && tail -F /url/local/apache-tomcat-9.0.37/bin/logs/catalina.out

```

3、构建镜像

```
docker build -t mytomcat:0.1 .
```

4、运行容器

```
#-d 表示后台运行
#-p 表示端口映射
#--name 名称
# -v挂载本地目录到容器中，：前面表示本地目录，后面表示其他目录

docker run -d -p 8080:8080 --name mytomcat01 -v /home/dayrain/build/mytomcat/webapps:/usr/local/apache-tomcat-9.0.37/webapps  -v /home/dayrain/build/mytomcat/logs/:/usr/local/apache-tomcat-9.0.37/logs mytomcat01:0.1

```

# 6、提交镜像到云端

## 6.1 提交到Dockerhub

1、登录账号

```
docker login -u 用户名      #输入密码
```

2、命名规范

命名不规范将无法上传，比如用户名是 dayrain， 那镜像名必须是 “dayrain/”开头， 例如：dayrain/tomcat:1.0

通过tag命令修改

```
docker tag mytomcat:1.0 dayrain/tomcat:1.0
```

3、提交

```
docker push dayrain/tomcat:1.0
```

# 7、导出镜像

## 7.1 导出镜像



docker save -o   [导出后的文件名]  [镜像名]

```
docker save -o /home/dayrain/dockerimages/tomcat.docker dayrain/tomcat
```



## 7.2 导入镜像

```
docker load --input 文件	

或者

docker load < 文件名
```

