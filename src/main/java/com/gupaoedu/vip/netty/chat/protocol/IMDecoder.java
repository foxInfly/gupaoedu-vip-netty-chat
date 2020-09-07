package com.gupaoedu.vip.netty.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

/**
 * 自定义IM协议的编码器
 */
public class IMDecoder extends ByteToMessageDecoder {

	//解析IM写一下请求内容的正则
	private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

	/**
	 * 解码，把流解码成对象 （netty内部调用）
	 * @param ctx channel handler context
	 * @param in byte buffer
	 * @param out List<Object> 解码后的对象list集合
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,List<Object> out) throws Exception {
		try{
	        final int length = in.readableBytes();  //获取可读字节长度
	        final byte[] array = new byte[length];
	        String content = new String(array,in.readerIndex(),length); //把字节编程字符串
	        
	        //空消息不解析
	        if(!(null == content || "".equals(content.trim()))){
	        	if(!IMP.isIMP(content)){
	        		ctx.channel().pipeline().remove(this);
	        		return;
	        	}
	        }
	        in.getBytes(in.readerIndex(), array, 0, length);//把bytebuffer内的内容读取到byte[]中
	        out.add(new MessagePack().read(array,IMMessage.class));//把byte[]转换成一个我们能够识别的IMMessage对象（这里是反序列化）
	        in.clear();//清空ByteBuffer
		}catch(MessageTypeException e){
			ctx.channel().pipeline().remove(this);
		}
	}
	
	/**
	 * 字符串解析成自定义即时通信协议 （浏览器Websocket协议）
	 * @param msg 字符串协议信息
	 * @return IMMessage
	 */
	public IMMessage decode(String msg){
		if(null == msg || "".equals(msg.trim())){ return null; }
		//解析字符串最好的办法：正则
		try{
			Matcher m = pattern.matcher(msg);
			String header = "";
			String content = "";
			if(m.matches()){
				header = m.group(1);//获取（）中的内容
				content = m.group(3);
			}
			
			String [] heards = header.split("\\]\\[");
			long time = 0;
			try{ time = Long.parseLong(heards[1]); } catch(Exception e){}
			String nickName = heards[2];
			//昵称最多十个字
			nickName = nickName.length() < 10 ? nickName : nickName.substring(0, 9);
			
			if(msg.startsWith("[" + IMP.LOGIN.getName() + "]")){
				return new IMMessage(heards[0],heards[3],time,nickName);
			}else if(msg.startsWith("[" + IMP.CHAT.getName() + "]")){
				return new IMMessage(heards[0],time,nickName,content);
			}else if(msg.startsWith("[" + IMP.FLOWER.getName() + "]")){
				return new IMMessage(heards[0],heards[3],time,nickName);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
