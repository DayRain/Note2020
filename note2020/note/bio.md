# 1、简单demo

下面的服务器代码一共有两个阻塞的地方：

一个是建立连接的时候

​                Socket socket = ss.accept();

另一个是读取内容的时候

​                 System.out.println(dataInputStream.readUTF()); 

server端

```
public class Server {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        new Thread(()->{
            try{
                ServerSocket ss = new ServerSocket(8888);
                Socket socket = ss.accept();
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                System.out.println(dataInputStream.readUTF()); //阻塞
                ss.close();
                socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();

    }
}
```

client端

```
public class Client {
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            Thread.sleep(5000);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("hello\n123\nll");
            outputStream.close();
            socket.close();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }
}
```

# 2、InetAddress

## 1、地址解析

```
public class Demo {

    public static void main(String[] args) {
        try {
            InetAddress inetAddress = Inet4Address.getByName("www.baidu.com");
            String hostAddress = inetAddress.getHostAddress();
            byte[] address = inetAddress.getAddress();
            System.out.println(hostAddress);
            System.out.println(Arrays.toString(address));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
```

## 2、获取本地地址

```
public class Demo {

    public static void main(String[] args) {
        try {
            // InetAddress inetAddress = Inet4Address.getByName("www.baidu.com");
            // String hostAddress = inetAddress.getHostAddress();
            // byte[] address = inetAddress.getAddress();
            // System.out.println(hostAddress);
            // System.out.println(Arrays.toString(address));
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println(inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

```

# 3、代理

```
public class SocketProxy {

    public static void main(String[] args) {
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 1080);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socketAddress);
        Socket socket = new Socket(proxy);
        SocketAddress remote = new InetSocketAddress("www.google.com",80);
        try {
            socket.connect(remote);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
```

