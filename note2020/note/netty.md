# 一、概述

## 1、Netty核心组件

- Channel
- 回调
- Future
- 事件和ChannelHandler

Netty的每一个出站I/O操作都将返回一个ChannelFuture；都不会阻塞，所以说Netty完全是异步和事件驱动的。

将事件派发给不同的ChannelHandler进行处理。

Netty通过触发事件将Selector从应用程序中抽象出来，消除了所有本来就需要手动编写的派发代码，在内部，为每个Channel分配一个EventLoop，用来处理时间。主要功能为1、注册感兴趣的事件 2、将事件派发给ChannelHandler 3、安排进一步的动作

# 二、入门案例

## 1、server端

```
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建BossGroup 和 workerGroup
        //说明
        //1、创建两个线程组 bossGroup 和 workerGroup
        //2、bossGroup处理与客户端的连接，真正的业务处理交给workerGroup
        //3、两个都是无限循环
        //4、默认有 cpu核心数*2  个子线程（nipEventLoop）
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

       try{
           //创建服务器端的启动对象
           ServerBootstrap bootstrap = new ServerBootstrap();

           bootstrap.group(bossGroup, workGroup)
                   .channel(NioServerSocketChannel.class) //使用NioSocketChannel作为线程队列
                   .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列
                   .childOption(ChannelOption.SO_KEEPALIVE, true)//设置成保持活动连接状态
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                       //给pipeline设置处理器
                       @Override
                       protected void initChannel(SocketChannel socketChannel) throws Exception {
                           socketChannel.pipeline().addLast(new NettyServerHandler());
                       }
                   });

           System.out.println("服务器 is ready。。。。。。。");

           //绑定端口，同步对象
           //启动服务器
           ChannelFuture channelFuture = bootstrap.bind(6668).sync();

           //对关闭端口进行监听，如果有关闭消息传过来，则会关闭
           channelFuture.channel().closeFuture().sync();
       }finally {
           bossGroup.shutdownGracefully();
           workGroup.shutdownGracefully();
       }

    }
}

```

## 2、server端handler

```
/**
 * 说明
 * 我们自定义一个Handler，需要继承netty 规定好的某个HandlerAdapter
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //ChannelHandlerContext 上下文对象，还有管道pipleline，通道channel，地址
    //msg 客户端发过来的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx= "+ctx);
        //将msg转成byte
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

```

## 3、client端

```
public class NettClient {

    public static void main(String[] args) throws InterruptedException {
        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            //客户端使用BootStrap
            Bootstrap bootstrap = new Bootstrap();

            //设置相关参数
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("客户端ok..");

            //启动客户端去连接服务器
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();

            channelFuture.channel().closeFuture().sync();
        }finally {
            eventExecutors.shutdownGracefully();
        }

    }
}
```

## 4、client端handler

```
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //通道就绪时会触发该方法

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, server: ", CharsetUtil.UTF_8));
    }

    //当通道有读取时间时，会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println("服务器回复的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址：" + ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```

# 三、任务

## 1、自定义普通任务

当业务处理时间比较长的时候，需要自定义用户任务

```
/**
 * 说明
 * 我们自定义一个Handler，需要继承netty 规定好的某个HandlerAdapter
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //ChannelHandlerContext 上下文对象，还有管道pipleline，通道channel，地址
    //msg 客户端发过来的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx= "+ctx);

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //处理事务
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }

            }
        });

        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端", CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
```

## 2、自定义定时任务

```
/**
 * 说明
 * 我们自定义一个Handler，需要继承netty 规定好的某个HandlerAdapter
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //ChannelHandlerContext 上下文对象，还有管道pipleline，通道channel，地址
    //msg 客户端发过来的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx= "+ctx);

        //用户自定义定时任务->该任务是提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    //处理事务
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }

            }
        },5, TimeUnit.SECONDS);



        //将msg转成byte
//        ByteBuf byteBuf = (ByteBuf) msg;
//        System.out.println("客户端发送的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端", CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

```

# 四、异步模型

## 1、简述

Netty的异步模型是建立在future和callback之上的。callback就是回调。重点说Future，它的核心方法是：假如方法fun处理业务非常耗时，那么一直等待future肯定不合适，所以可以在调用fun的时候，立马返回一个Future，后续通过Future去监控方法fun的处理过程，即Future-Listener机制。

## 2、实例

接着上面的server端

```
           //绑定端口，同步对象
           //启动服务器
           ChannelFuture channelFuture = bootstrap.bind(6668).sync();

           //给channelFuture注册监听器
           channelFuture.addListener(new ChannelFutureListener() {
               @Override
               public void operationComplete(ChannelFuture channelFuture) throws Exception {
                   if(channelFuture.isSuccess()){
                       System.out.println("监听端口 6668 成功");
                   }else{
                       System.out.println("监听端口失败");
                   }
               }
           });
```

# 五、http服务实例

## 1、server端

```
public class TestServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TestServerInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}

```

```
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        //加入一个netty 提供的httpServerCodec codec=>{coder - decoder}
        pipeline.addLast("MyHeepServerCodec", new HttpServerCodec());

        //2、增加自定义的处理器
        pipeline.addLast("TestHeepServerHandler", new TestHttpServerHandler());
    }
}
```

```
/**
 * 1、SimpleChannelInboundHandler 是ChannelInboundHandlerAdapter
 * 2、HttpObject客户但和服务器相互通讯的数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        if(httpObject instanceof HttpRequest){
            System.out.println("httpObject类型=" + httpObject.getClass());
            System.out.println("客户端地址" + channelHandlerContext.channel().remoteAddress());

            //回复信息
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应，即httpresponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            channelHandlerContext.writeAndFlush(response);
        }
    }
}
```

## 2、请求过滤

```
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        if(httpObject instanceof HttpRequest){
            System.out.println("httpObject类型=" + httpObject.getClass());
            System.out.println("客户端地址" + channelHandlerContext.channel().remoteAddress());

            URI uri = new URI(((HttpRequest) httpObject).uri());

            if("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了favicon.ico， 不做响应");
            }

            //回复信息
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应，即httpresponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            channelHandlerContext.writeAndFlush(response);
        }
    }
}

```

# 六、群聊

```
public class GroupChatServer {

    private final int port;

    public GroupChatServer(int port){
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast("decode", new StringDecoder());
                            channelPipeline.addLast("encode", new StringEncoder());
                            channelPipeline.addLast(new GroupChatHandler());

                        }
                    });

            System.out.println("netty服务器启动！");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //监听关闭
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatServer(7001).run();
    }
}

```



```
public class GroupChatHandler extends SimpleChannelInboundHandler<String> {

//    public static List<Channel> channels = new ArrayList<>();

    //定义一个channel组
    //GlobalEventExecutor.INSTANCE全局的事件
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //表示一旦连接后，第一个触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将客户加入聊天的信息推送给其他的在线客户端

        //该方法会遍历，并向所有channelGroup发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入了聊天" + simpleDateFormat.format(new Date()) +"\n");
        channelGroup.add(channel);
    }

    //表示处于活跃状态，亦可表示上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了~");
    }

    //断开连接，将某某离开信息推送给其他在线用户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "断开了连接，无需手动remove");
        channelGroup.add(channel);
        System.out.println("还剩下多少人在线： " + channelGroup.size());
        //无需手动remove
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了~");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.forEach(ch ->{
            if(channel != ch){
                //排除当前用户
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg +"\n");
            }else{
                ch.writeAndFlush("自己给自己发送了一条信息" + msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭
        ctx.close();
    }
}

```





```
public class GroupChatClient {
    private final String address;

    private final int port;

    public GroupChatClient(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void run() throws InterruptedException {

        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try{

            Bootstrap bootstrap = new Bootstrap().group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast("decoder", new StringDecoder());
                            channelPipeline.addLast("encoder", new StringEncoder());
                            channelPipeline.addLast(new GroupChatClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress() + "--------");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String s = scanner.nextLine();
                //发送到服务器
                channel.writeAndFlush(s+"\r\n");
            }

        }finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatClient("127.0.0.1", 7001).run();
    }
}

```



```
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
    }
}

```

# 七、第二次笔记补充

## 7.1 demo

### server端

handler

```
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

        //将收到的消息发送给发送者，而不冲刷出站消息。
        ctx.write(in);
        // ctx.writeAndFlush(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)           //
                 .addListener(ChannelFutureListener.CLOSE);  //将未决消息冲刷到远程节点，并关闭Channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

}
```

服务启动类

```
public class EchoServer {

    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8888;
        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer< SocketChannel>()  {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }

                    });
            //异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成
            ChannelFuture channelFuture = b.bind().sync();
            //获取Channel的closeFuture，并且阻塞当前线程直到它完成
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
```

### client端

handler类

```
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

```

client类

```
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            Channel channel = f.channel();
            while (true){
                Scanner scanner = new Scanner(System.in);
                String s = scanner.nextLine();
                if("exit".equals(s)){
                    channel.writeAndFlush(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);
                    break;
                }
                channel.writeAndFlush(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8));
            }
            //会等待服务器关闭ci
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        String ip = "192.168.1.207";
        int host = 8888;
        new EchoClient(ip, host).start();
    }
}
```

## 7.2 ChannelHandler-Context

​       代表了ChannelHandler和ChannelPipeline之间的绑定。虽然这个对象可以被用于获取底层的channel，但主要还是被用于写出站数据。

​         Netty中发送消息的两种方式：

- 直接写到Channel中

会使消息从Channel-Pipeline的尾端开始流动

- 写到ChannelHandlerContext对象中

消息将从ChannelPipeline中的下一个Channel-Handler开始流动

## 7.3 EventLoopGroup

客户端（BootStrap）只需要一个EventLoopGroup，

服务器（ServerBootStrap）需要两个EventLoopGroup。一个代表自身绑定到某个端口正在监听的套接字。第二组包含所有已创建的用来处理客户端连接的channel。

服务器端，有的测试demo  add了两个个group，有的测试demo只 add了一个group，但实际上此时是共用一个group。

## 7.4 心跳





```
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final ByteBuf heartbeat_sequence = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("heart beat sequence", CharsetUtil.UTF_8));
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

        //将收到的消息发送给发送者，而不冲刷出站消息。
        ctx.write(in);
        // ctx.writeAndFlush(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将未决消息冲刷到远程节点，并关闭Channel
        // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
        //          .addListener(ChannelFutureListener.CLOSE);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(channelFuture.isSuccess()){
                            System.out.println("success");
                        }else{
                            System.out.println("error");
                        }
                    }
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            System.out.println(simpleDateFormat.format(new Date())+"触发了");
            //失败的时候关闭
            ctx.writeAndFlush(heartbeat_sequence.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }else {
            //不是心跳事件，继续传递
            super.userEventTriggered(ctx, evt);
        }
    }

}
```





```
public class EchoServer {

    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8888;
        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer< SocketChannel>()  {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS)); //被触发的时候发送（60秒内）
                            pipeline.addLast(serverHandler);
                        }
                    });
            //异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成
            ChannelFuture channelFuture = b.bind().sync();
            //获取Channel的closeFuture，并且阻塞当前线程直到它完成
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}

```

