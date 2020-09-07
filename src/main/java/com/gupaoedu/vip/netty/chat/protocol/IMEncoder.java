package com.gupaoedu.vip.netty.chat.protocol;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

    /**
     * 将IMMessage对象比南城字节byte（netty内部调用）
     * @param ctx ChannelHandlerContext
     * @param msg IMMessage实体类
     * @param out ByteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    /**
     * 将IMMessage对象编码成指定的自定义协议字符串，（浏览器Websocket协议）
     * @param msg IMMessage实体类
     * @return String
     */
    public String encode(IMMessage msg) {
        if (null == msg) {
            return "";
        }
        String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
        if (IMP.LOGIN.getName().equals(msg.getCmd()) || IMP.FLOWER.getName().equals(msg.getCmd())) {
            prex += ("[" + msg.getSender() + "][" + msg.getTerminal() + "]");
        } else if (IMP.CHAT.getName().equals(msg.getCmd())) {
            prex += ("[" + msg.getSender() + "]");
        } else if (IMP.SYSTEM.getName().equals(msg.getCmd())) {
            prex += ("[" + msg.getOnline() + "]");
        }
        if (!(null == msg.getContent() || "".equals(msg.getContent()))) {
            prex += (" - " + msg.getContent());
        }
        return prex;
    }

}
