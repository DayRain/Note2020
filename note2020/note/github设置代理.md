# HTTP代理：

全局代理
git config --global http.proxy http://127.0.0.1:1080
git config --global https.proxy https://127.0.0.1:1080
部分代理
git config --global http.https://github.com.proxy https://127.0.0.1:1080
git config --global https.https://github.com.proxy https://127.0.0.1:1080
取消代理
git config --global --unset http.proxy
git config --global --unset https.proxy

socket5
git config --global http.https://github.com.proxy socks5://127.0.0.1:1080
git config --global https.https://github.com.proxy socks5://127.0.0.1:1080





SSH代理：

在C:\Users\dayrain\.ssh\

创建文件夹 configTemp

再创建文件config

添加如下命令

-S 为 socks, -H 为 HTTP

ProxyCommand connect -H 127.0.0.1:1080 %h %p

如果找不到 connect 命令那么指定其绝对路径，一般在 git 安装目录下 \mingw64\bin\connect.exe.

#分域名代理
Host github.com
    ProxyCommand connect -S 127.0.0.1:1080 %h %p