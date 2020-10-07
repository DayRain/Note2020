# 一、基本命令

安装：yum install docker

启动：systemctl start docker

查看docker版本：docker -v

开机自启动：systemctl enable docker

停止docker：systemctl stop docker

# 二、镜像操作

1、查看是否安装某镜像（例如mysql） docker search mysql

2、配置中国区的Docker官方镜像加速（官网：<https://www.docker-cn.com/registry-mirror>）

​        1）、直接拉取

```
$ docker pull registry.docker-cn.com/myname/myrepo:mytag
```

​        2）、在 Docker 守护进程启动时传入 `--registry-mirror` 参数

```
$ docker --registry-mirror=https://registry.docker-cn.com daemon
```

​      3）、永久性的修改，修改 `/etc/docker/daemon.json` 文件并添加上 registry-mirrors 键值。

```
{
  "registry-mirrors": ["https://registry.docker-cn.com"]
}
```

​      4）、直接用脚本

```
curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://f1361db2.m.daocloud.io
```

3、拉取（下载）

默认最新版本：  docker pull mysql

加上标签： docker pull mysql：tag

3、查看所有镜像

docker iamges

4、删除镜像

docker rmi images-id

# 三、容器操作

软件镜像——运行镜像——产生一个容器（正在运行的软）

1、步骤：

```
1、搜索镜像 docker search
2、拉取镜像 docker pull
3、根据镜像启动容器
docker run -name 自定义容器名 -d 镜像名
```

2、相关命令

查看运行中的容器：   

docker ps

查看所有容器： 

docker ps  -a

停止运行中的容器：   

docker stop i d/name 

删除容器：  

docker   rm   id

端口映射：

docker run -d -p 8080:8080 --name mytomcat   docker.io/tomcat          (-d后台运行。-p表示将主机的端口映射到容器)   （**主机(宿主)端口:容器端口**）

docker run -d -p 8080:8080  tomcat

3、安装mysql实例

​        1）、错误示范

```
[root@localhost ~]# docker run --name mysql -d mysql
5a19ca72447a110fe675a973c0436b57cfa2a223c7e478191736775f44bee062

[root@localhost ~]# docker logs mysql
error: database is uninitialized and password option is not specified 
  You need to specify one of MYSQL_ROOT_PASSWORD, MYSQL_ALLOW_EMPTY_PASSWORD and MYSQL_RANDOM_ROOT_PASSWORD

需要指定密码，或者其他参数
```

 2)、正确启动方式

```
[root@localhost ~]# docker run --name mysql01 -e MYSQL_ROOT_PASSWORD=Ph0716 -d mysql
4df77a53a836350c442fd24d3158d5397170ec254d706c4b36fc88fb975dea4b

```

```
带上端口
[root@localhost ~]# docker run -p 3306:3306 --name mysql02 -e MYSQL_ROOT_PASSWORD=Ph0716 -d mysql
294a5ff3bd0b30179da606cfd83a846ce2ecc0ea9d2dc410c85a3b3ae4a4fb2f
```

3）、远程无法连接mysql

SSH无法连接的时候，可能是权限的问题

```
首先通过docker进入mysql容器
docker exec -it mysql(这里的mysql是指你启动时的容器名称) bash
然后给mysql设置权限
mysql -uroot -p
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '你的密码';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的密码';
SELECT plugin FROM mysql.user WHERE User = 'root';
```

4）、其他高级操作

```
配置文件
$ docker run --name some-mysql -v /conf/mysql:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag
把主机的/conf/mysql文件夹挂载到mysqldocker容器的/etc/mysql/conf.d文件夹里面
改mysql的配置文件就只需要把mysql配置文件放在此目录即可


不用配置文件的启动
$ docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

5）、运行已经创建好的容器

```
docker start dockername
```

6）、创建数据库指定编码

```
CREATE DATABASE IF NOT EXISTS my_db default character set utf8 COLLATE utf8_general_ci;
```

