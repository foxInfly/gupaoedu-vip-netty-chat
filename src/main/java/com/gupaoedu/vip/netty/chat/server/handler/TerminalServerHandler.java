package com.gupaoedu.vip.netty.chat.server.handler;

import lombok.extern.slf4j.Slf4j;

import com.gupaoedu.vip.netty.chat.processor.MsgProcessor;
import com.gupaoedu.vip.netty.chat.protocol.IMMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;

/**
 * 处理直接发送IMMessage对象的IDE控制台
 */
@Slf4j
public class TerminalServerHandler extends SimpleChannelInboundHandler<IMMessage>{

	private MsgProcessor processor = new MsgProcessor();

    /**
     * 业务处理
     * @param ctx ChannelHandlerContext
     * @param msg IMMessage
     */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
	    processor.sendMsg(ctx.channel(), msg);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("Socket Client: 与客户端断开连接:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
