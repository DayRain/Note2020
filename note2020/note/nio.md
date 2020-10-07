# 一、BIO 

## 1、服务端测试代码

```
public class BIOServer {

    public static void main(String[] args) throws IOException {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        //创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动了");

        while (true) {
            //监听，等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");

            //创建一个线程与之通信
            newCachedThreadPool.execute(new Runnable() {
                public void run() {
                    handler(socket);
                }
            });

        }

    }

    public static void handler(Socket socket){
        try{
            System.out.println("线程id是："+Thread.currentThread().getId()+"  线程名为："+Thread.currentThread().getName());
            byte[]bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            //循环读取
            while (true){
                System.out.println("read-------");
                int read = inputStream.read(bytes);
                System.out.println("read-------over");
                if(read != -1){
                    System.out.println(new String(bytes, 0, read));//输出客户端发送的数据
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {

                socket.close();
                System.out.println("close------");
                System.out.println("关闭和客户端的连接！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


```

## 2、telnet测试

开启telnet服务

客户端连接

```
telnet 127.0.0.1 端口
```

输入命令

```
ctrl + 】
```

发送请求

```
send  内容
```

如果把多线程去掉进行测试，则服务器同一时刻只能收到一个客户端发来的消息

```
    public static void main(String[] args) throws IOException {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        //创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动了");

        while (true) {
            //监听，等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");

            //创建一个线程与之通信
//            newCachedThreadPool.execute(new Runnable() {
//                public void run() {
//                    handler(socket);
//                }
//            });
            handler(socket);

        }

    }
```



# 二、NIO

## 1、概述

NIO，同步非阻塞io，也被称为new io。

NIO的相关类都被放在java.nio包及子包下，并且对原java.io中的很多类进行改写。

NIO的三大核心部分：Channel（通道）、Buffer（缓冲区）、

Selector（选择器）。

NIO是面向缓冲区，或者面向块编程的。数据读取到一个它稍后处理的缓冲区，需要时可以在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络。

通俗理解就是。NIO可以做到一个线程来处理多个操作。假设有10000个请求过来，根据实际情况，可以分配50个或者100个线程，不需要像之前的阻塞IO那样，非得分配10000个。

HTTP2.0使用了多路复用的技术，做到同一个连接并发处理多个请求，而且并发请求的数量比Http1.1大了好几个数量级。

## 2、Buffer

```
public class BasicBuffer {
    public static void main(String[] args) {
        //举例说明Buffer的使用
        //创建一个Buffer，大小为5，即可存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);
        intBuffer.put(10);
        intBuffer.put(12);
        intBuffer.put(11);
        intBuffer.put(13);
        intBuffer.put(14);

        //如何从buffer中读数据
        //将buffer转换，读写切换
        intBuffer.flip();
        while(intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
```

## 3、NIO和BIO的比较

1、BIO以流的方式处理数据，而NIO以块的方式处理数据，块的I/O效率要比流I/O高很多

2、BIO是阻塞的，NIO是非阻塞的。

3、BIO基于字节流和字符流进行操作，而NIO基于Channel和Buffer进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写到通道中，Selector用于监听多个通道的事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道。

## 4、对应关系

一个Thread对应一个Selector

一个Selector对应多个Channel

一个Channel对应一个Buffer

Channel和Buffer都是双向的

## 5、Channel案例：本地文件写数据

```
public class NIOFileChannel01 {
    public static void main(String[] args) throws Exception{
        String str = "hello, world";
        //创建一个输入流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("h:\\hello.txt");
        //通过fileOutputStream获取对应的FileChannel
        //fileChannel真实类型是 FileChannelImpl

        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());

        //对ByteBuffer进行flip
        byteBuffer.flip();

        //将byteBuffer数据写入到fileChannel
        fileChannel.write(byteBuffer);

        fileOutputStream.close();

    }
}
```

## 6、Channel案例：本地文件读数据

```
public class NIOFileChannel02 {
    public static void main(String[] args) throws IOException {
        File file = new File("H:\\hello.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        FileChannel fileChannel = fileInputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        //将数据读进byteBuffer
        fileChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));

    }
}
```

## 7、Channel案例：使用一个Buffer完成文件拷贝



```
public class NIOFileChannel03 {
    public static void main(String[] args) throws IOException {
        File file1 = new File("1.txt");
        FileInputStream fi = new FileInputStream(file1);
        FileChannel channel1 = fi.getChannel();

        File file2 = new File("2.txt");
        FileOutputStream fo = new FileOutputStream(file2);
        FileChannel channel2 = fo.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        while (true){
            byteBuffer.clear();
            int read = channel1.read(byteBuffer);
            if(read == -1){
                break;
            }
            byteBuffer.flip();
            channel2.write(byteBuffer);
        }
        fi.close();
        fo.close();
    }
}

```

## 8、Channel案例：使用transferForm方法拷贝文件

```
public class NIOFileChannel04 {
    public static void main(String[] args) throws IOException {
        FileInputStream fi = new FileInputStream("1.jpg");
        FileOutputStream fo = new FileOutputStream("2.jpg");

        FileChannel sourceCh = fi.getChannel();
        FileChannel destCh = fo.getChannel();

        destCh.transferFrom(sourceCh, 0, sourceCh.size());

        fi.close();
        fo.close();
        sourceCh.close();
        destCh.close();
    }
}
```

## 9、Buffer类型化和只读

put不同类型时，读取的时候需要按顺序读取

```
public class ByteBufferPutAndGet {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        byteBuffer.putInt(1);
        byteBuffer.putLong(199);
        byteBuffer.putChar('a');
        byteBuffer.putShort((short) 12);

        byteBuffer.flip();
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());
        System.out.println(byteBuffer.getShort());
    }
}
```

只读

```
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        for (int i = 0; i < 64; i++){
            byteBuffer.put((byte) i);
        }

        byteBuffer.flip();

        byteBuffer = byteBuffer.asReadOnlyBuffer();

        while (byteBuffer.hasRemaining()){
            System.out.println(byteBuffer.get());
        }
    }
}

```

如果改为只读的byteBuffer，强行存数据时，会报错如下

```
Exception in thread "main" java.nio.ReadOnlyBufferException
	at java.nio.HeapByteBufferR.put(HeapByteBufferR.java:172)
	at com.dayrain.nio.ReadOnlyBuffer.main(ReadOnlyBuffer.java:20)
```

## 10、MappedByteBuffer

```
//MappedByteBuffer 可以让文件直接在内存里修改（堆外内存），操作系统不需要拷贝一次。
public class MappedByteBufferTest {
    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        //获取对应的通道
        FileChannel fileChannel = randomAccessFile.getChannel();
        //这里的5表示可以修改的返回为0到5
        fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0 , 5);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');

        randomAccessFile.close();
    }
}

```

## 11、Scattering和Gathering多Buffer读写

```
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        final ByteBuffer[]byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(5);

        //等待客户端连接（telnet）
        SocketChannel socketChannel = serverSocketChannel.accept();

        int messageLength = 8; //将设从客户端接受八字节
        //循环读取
        while (true) {
            int byteRead = 0;

            while (byteRead < messageLength) {
                long l = socketChannel.read(byteBuffers);
                byteRead += l;//累计读取的字符数
                System.out.println("byteRead" + byteRead);
                Arrays.asList(byteBuffers).stream().map(buffer-> "position=" + buffer.position() +", limit="+ buffer.limit()).forEach(System.out::println);
            }

            //将所有的buffer进行flip
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());

            //将数据显示到客户端
            long byteWrite = 0;
            while (byteWrite < messageLength){
                long l = socketChannel.write(byteBuffers);
                byteWrite += l;
            }

            //将所有的buffer进行clear
            Arrays.asList(byteBuffers).forEach(buffer -> {
                buffer.clear();
            });
            System.out.println("byteRead="+ byteRead +" byteWrite=" + byteWrite +" messageLength" + messageLength);
        }

    }
}

```

## 12、Selector原理

1、Java的NIO，用非阻塞的IO方式。可以用一个线程，处理多个客户端连接，就会使用到Selector

2、Selector能够检测到多个注册的通道上是否有事件发生（注意：多个Channel以事件的方式可以注册到同一个Selector），如果事件发生，便获取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。

3、只有在连接真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程。

4、避免了多线程之间的上下文切换导致的开销。



流程

1、当客户端连接时，会通过ServerSocketChannel得到SocketChannel

2、将SocketChannel注册到selector上，register（Selector sel， int ops），一个selector上可以注册多个SocketChannel

3、注册后返回一个SelectionKey，会和该Selector关联（集合）

4、Selector进行监听select方法，返回有事件发生的通道的个数。

5、进一步得到各SelectionKey（有事件发生）

6、在通过SelectionKey反向获取SocketChannel，方法channel（）

7、可以通过得到的channel，完成业务处理

# 三、NIO入门

## 1、server端

```
public class NioServer {

    public static void main(String[] args) throws IOException {
        //创建一个ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到一个Selector对象
        Selector selector = Selector.open();

        //绑定一个端口6666，在服务端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChannel注册到selector关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {

            //等待疫一秒，如果没有事件发生，返回
            if(selector.select(1000) == 0) {
                //没有事件发生
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }

            //如果返回>0,获取SelectionKey集合
            //1、如果返回>0，表示已经获取到关注的事件
            //2、selector.selectedKeys()返回关注事件的集合
            // 通过selectionKeys反向获取通道
            Set<SelectionKey>selectionKeys = selector.selectedKeys();

            //遍历Set<selectionKey>,使用迭代器遍历

            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //获取到SelectionKey
                SelectionKey key = iterator.next();
                //根据key对应的通道发生的事件做不同的处理
                if(key.isAcceptable()) {//如果是OP_ACCEPT
                    //该客户端生成一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("客户端连接成功 生成了一个socketChannel" + socketChannel.hashCode());
                    socketChannel.configureBlocking(false);
                    //将socketChannel注册到selector,关注事件为OP_READ，同时给该Channel关联一个Buffer
                    //关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                if(key.isReadable()) {//如果发生了OP_READ
                    //通过key反向获取到对应的channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("from 客户端" + new String(buffer.array()));
                }

                //手动从集合中移除当前的selectionKey，防止重复操作
                iterator.remove();
            }
        }
    }
}

```

## 2、client端

```
public class NioClient {
    public static void main(String[] args) throws IOException {
        //等到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //设置服务器的ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //连接服务器
        if(!socketChannel.connect(inetSocketAddress)) {

            //没成功,非阻塞循环
            while (! socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
            }
        }

        //如果连接成功，就发送数据
        String str = "hello,world~";
        //无需指定大小，直接根据字节数组的大小放进buffer
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        //发送数据,将buffer数据写入channel
        socketChannel.write(buffer);
        System.in.read();
        
    }
}
```

## 3、群聊

1、server

```
public class GroupChatServer {

    private Selector selector = null;

    private ServerSocketChannel serverSocketChannel = null;

    private static final int port = 6667;

    public GroupChatServer(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {

            while (true) {

                int count = selector.select();

                if(count > 0){
                    //有事件需要处理
                    Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        if(selectionKey.isAcceptable()){
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + " 上线了！ ");
                        }

                        if(selectionKey.isReadable()) {//通道发送read事件
                            readData(selectionKey);
                        }
                        selectionKeyIterator.remove();
                    }
                }else{
                    System.out.println("正在等待");
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }

    private void readData(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = null;
        try{
            socketChannel = (SocketChannel) selectionKey.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int count = socketChannel.read(buffer);

            if(count > 0){
                String s = new String(buffer.array());
                System.out.println("客户端：" + s);

                //向其他客户端转发消息(去掉自己)
                sendInfoToOtherClient(s, socketChannel);
            }
        }catch (IOException e){
            try {
                System.out.println(socketChannel.getRemoteAddress()+"离线了。。。");
                selectionKey.channel();
                socketChannel.close();
            }catch (IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

    private void sendInfoToOtherClient(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息");
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();

            if(channel instanceof SocketChannel && channel != self){
                SocketChannel dest = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                dest.write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) {
        GroupChatServer chatServer = new GroupChatServer();
        chatServer.listen();
    }
}

```

2、client

```
public class GroupChatClient {

    //定义相关的属性
    private final String HOST  = "127.0.0.1";//服务器ip

    private final int PORT = 6667;

    private Selector selector;

    private SocketChannel socketChannel;

    private String username;

    public GroupChatClient() throws IOException {
        selector = Selector.open();

        socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", PORT));


        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);

        username = socketChannel.getLocalAddress().toString().substring(1);

        System.out.println("客户端is ok...");

    }

    public void sendInfo(String info){
        info = username + "说：" + info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void readInfo(){
        try {
            int readChannels = selector.select();
            if(readChannels > 0){

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        sc.read(buffer);

                        String s = new String(buffer.array());
                        System.out.println(s.trim());
                        iterator.remove();
                    }else {
                        System.out.println("没有可用的通道。。。") ;
                    }


                }

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        GroupChatClient chatClient = new GroupChatClient();

        //每隔三秒，读取从服务器发送的数据
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    chatClient.readInfo();

                    try{
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}

```

