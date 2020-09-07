package com.gupaoedu.vip.netty.chat.server.handler;

import com.gupaoedu.vip.netty.chat.protocol.IMDecoder;
import com.gupaoedu.vip.netty.chat.protocol.IMEncoder;
import com.gupaoedu.vip.netty.chat.protocol.IMMessage;
import lombok.extern.slf4j.Slf4j;

import com.gupaoedu.vip.netty.chat.processor.MsgProcessor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 用来处理websocket协议的
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private MsgProcessor processor = new MsgProcessor();

    /**
     * 业务处理
     * @param ctx ChannelHandlerContext
     * @param msg TextWebSocketFrame
     */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,TextWebSocketFrame msg) throws Exception {
		processor.sendMsg(ctx.channel(), msg.text());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		Channel client = ctx.channel();
		String addr = processor.getAddress(client);
		log.info("WebSocket Client:" + addr + "异常");
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}

}
