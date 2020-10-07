# 1、修改yum源

yum update -y 会更新内核，慎用

```
备份
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

如果wget不可用
yum -y install wget

如果是Centos7
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

最后一步，生成缓存
yum makecache
```

注意：不同版本的linux，更换centos-7中的数字即可

其他可选镜像：

http://mirrors.163.com/.help/CentOS7-Base-163.repo（未使用）

http://mirrors.aliyun.com/repo/Centos-7.repo



# 2、修改yum配置，使其不更新内核

```
vi /etc/yum.conf
#最下面加个*
exclude=kernel*
```

