# 一、常用命令总结（CentOS）

1、查看ip地址 ：  ip addr

2、查看内核版本：uname -r    升级内核  yum update

3、防火墙相关命令

1)、查看防火墙状态：
firewall-cmd  --state

systemctl status firewalld

service  iptables status

2）、查看当前public的zone下支持的服务

firewall-cmd --zone=public --list-services

3）、查看当前public的zone下支持的端口

firewall-cmd --zone=public --list-port

4）、从public的zone。移出http服务

firewal-cmd  --zone=public --remove-ser

5）、重新加载firewall的配置

firewall-cmd --reload

6）、添加指定的服务

firewall-cmd --zone=public --add-service=http

7)、添加指定端口

firewall-cmd --zone=public --add-port=8080

firewall-cmd --zone=public --add-port=8080/tcp

firewall-cmd --zone=public --add-port=8080/udp

8)、移出端口

firewall-cmd --zone=public --remove-port=8080



9）、暂时关闭防火墙

systemctl stop firewalld

service  iptables stop

10）、永久关闭防火墙

systemctl disable firewalld

chkconfig iptables off

11）、重启防火墙

systemctl enable firewalld

service iptables restart  

12）、永久关闭后重启

chkconfig iptables on



