package com.zc.znetty.demo.ztime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    { 
        //(1)当建立连接并 准备好生成流量时,将调用channelActive()方法.此处生成一个32位int值表示当前时间
        //(2)要发送新消息,需要分配一个包含消息的新的缓冲区.此处4个字节.ChannelHandlerContext对象的alloc方法分配一个新的缓冲区
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        //ctx.writeAndFlush()返回一个ChannelFuture对象,表示尚未发生的I/O操作.Netty是异步的.任何请求的操作可能尚未执行    
        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future)
            {
                assert f == future;
                ctx.close();
            }
        }); // (4)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
