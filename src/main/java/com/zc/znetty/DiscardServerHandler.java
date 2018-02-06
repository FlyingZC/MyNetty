package com.zc.znetty;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理服务器端通道
 * ChannelInboundHandler提供了可以覆盖的各种事件处理程序方法
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter
{
    //每当从客户端接收到新数据时,该方法接收客户端消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        // 以静默方式丢弃接收的数据,此处接收的消息类型为ByteBuf
        //((ByteBuf) msg).release(); //最终通过release()显示释放
        ByteBuf in = (ByteBuf) msg;
        try
        {
            while (in.isReadable())
            { // (1)
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        }
        finally
        {
            ReferenceCountUtil.release(msg); // (2)
        }
        // 或者直接打印
        System.out.println("Yes, A new client in = " + ctx.name());
    }

    //当netty由于I/O错误 或 处理事件 时 抛出的异常而导致因尝试,使用Throwable调用exceptionCaught()
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        // 出现异常时关闭连接。
        cause.printStackTrace();
        ctx.close();
    }
}
