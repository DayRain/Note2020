# 一、入门案例

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

# 二、任务

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

# 三、异步模型

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

# 四、http服务实例

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

# 五、群聊

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

