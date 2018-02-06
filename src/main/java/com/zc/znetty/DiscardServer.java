package com.zc.znetty;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 */
public class DiscardServer
{

    private int port;

    public DiscardServer(int port)
    {
        this.port = port;
    }

    public void run() throws Exception
    {
        //EventLoopGroup是处理I/O操作的多线程事件循环.Netty为不同类型的传输提供了各种EventLoopGroup实现.
        //"boss"接受 传入连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //"worker"当"boss"接受连接并向 worker注册接受连接后,则"worker"处理所接受连接的 流量
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            //ServerBootstrap是一个用于设置服务器的助手类
            ServerBootstrap b = new ServerBootstrap();
            //指定使用NioServerSocketChannel类.该类 用于实例化新的通道以接受传入连接
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          //此处指定的处理程序将始终由新接受的通道计算.ChannelInitializer是一个特殊的处理程序,用于帮助用户配置新的通道.
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    { 
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception
                        {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)//设定Channel实现的参数(如此处对TCP/IP服务器设置了 tcpNoDelay和keepAlive)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //option()用于接受传入连接的NioServerSocketChannel,ChildOption用于由父ServerChannel接受的通道,此时为NioServerSocketChannel

            // 绑定端口号,启动服务器
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        }
        finally
        {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception
    {
        int port;
        if (args.length > 0)
        {
            port = Integer.parseInt(args[0]);
        }
        else
        {
            port = 8080;
        }
        new DiscardServer(port).run();
    }
}
