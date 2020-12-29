/*
 *  Copyright (c) 2020. 衷于栖 All rights reserved.
 *
 *  版权所有 衷于栖 并保留所有权利 2020。
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  作者：衷于栖（feedback@zhoyq.com）
 *  博客：https://www.zhoyq.com
 *  创建时间：2020
 *
 */

package com.zhoyq.server.jt808.starter.netty.coder;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 这里处理的是网络原因引起的分包 粘包
 * 1、当内容刚好时，返回false，告知父类接收下一批内容
 * 2、内容不够时需要下一批发过来的内容，此时返回false，这样父类 CumulativeProtocolDecoder
 *    会将内容放进IoSession中，等下次来数据后就自动拼装再交给本类的doDecode
 * 3、当内容多时，返回true，因为需要再将本批数据进行读取，父类会将剩余的数据再次推送本
 *    类的doDecode
 *
 * 20200704 感谢B站网友 果子狸猫么么
 * 1、先转义 检查校验码 在解析检查长度
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/17
 */
@Slf4j
@Component
@AllArgsConstructor
@ChannelHandler.Sharable
public class Jt808NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final AttributeKey<ByteBuf> BUFFER = AttributeKey.valueOf("buffer");

    private static final byte MSG_BROKER = 0x7E;
    private static final int MSG_MIN_LEN = 15;
    private static final int MEG_MIN_LEN_WITH_PKG = 19;
    private static final int MAX_READ_LEN = 1024 * 10;

    private Jt808Helper jt808Helper;
    private ByteArrHelper byteArrHelper;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        boolean usingSessionBuffer = true;
        ByteBuf buf = ctx.channel().attr(BUFFER).get();
        if (buf != null) {
            boolean appended = false;
            if (buf.writableBytes() > 0) {
                try {
                    buf.writeBytes(ByteBufUtil.getBytes(msg));
                    appended = true;
                } catch (IndexOutOfBoundsException | IllegalStateException e) {
                    log.warn(e.getMessage());
                }
            }

            if (appended) {
                buf.nioBuffer().flip();
            } else {
                buf.nioBuffer().flip();
                ByteBuf newBuf = ByteBufAllocator.DEFAULT.buffer(buf.readableBytes() + msg.readableBytes());
                newBuf.writeBytes(ByteBufUtil.getBytes(buf));
                newBuf.writeBytes(ByteBufUtil.getBytes(msg));
                buf = newBuf;
                ctx.channel().attr(BUFFER).set(newBuf);
            }
        } else {
            buf = msg;
            usingSessionBuffer = false;
        }

        do {
            int oldPos = buf.readerIndex();
            boolean decoded = this.doDecode(ctx, buf, out);
            if (!decoded) {
                break;
            }

            if (buf.readerIndex() == oldPos) {
                throw new IllegalStateException("doDecode() can't return true when buffer is not consumed.");
            }
        } while(buf.readableBytes() > 0);

        if (buf.readableBytes() > 0) {
            if (usingSessionBuffer && buf.writableBytes() > 0) {
                buf.nioBuffer().compact();
            } else {
                this.storeRemainingInSession(buf, ctx);
            }
        } else if (usingSessionBuffer) {
            this.removeSessionBuffer(ctx);
        }

    }

    private void removeSessionBuffer(ChannelHandlerContext ctx) {
        ctx.channel().attr(BUFFER).set(null);
    }

    private void storeRemainingInSession(ByteBuf buf, ChannelHandlerContext ctx) {
        ctx.channel().attr(BUFFER).set(buf);
    }

    private boolean doDecode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out){
        // 有数据时，读取前 8 字节判断消息长度
        if (msg.readableBytes() > 0) {
            // 标记初始位置
            msg.markReaderIndex();
            // 读取一字节 检查是否标识位
            byte byteBuf = msg.readByte();
            if( byteBuf == MSG_BROKER ){
                // 设置一个长度缓存
                int pos = 1;
                // 是标识位 循环读取 直到 下一个标识位 为止
                for(int i = 0;i < msg.readableBytes();){
                    byteBuf = msg.readByte();
                    pos ++;
                    if( byteBuf == MSG_BROKER ){
                        // 读取到 下一个标识位 并且重置缓存
                        break;
                    }
                }
                // 循环终结 但是 还是没找到最后的标识位
                // 继续读取 重新解析
                if (byteBuf != MSG_BROKER) {
                    // FIXME 如果只有一个 标识位 会导致一直读取的问题 需要设置一个限定长度 读取超过这个长度 就直接丢弃
                    // 目前设置成 10K 以后会加入配置
                    if (pos < MAX_READ_LEN) {
                        msg.resetReaderIndex();
                    }
                    return false;
                }
                // 小于最小包长度 截断 重新读取 剩余的 字节
                if (pos < MSG_MIN_LEN) {
                    log.warn("data is too short ... drop !");
                    return msg.readableBytes() > 0;
                }
                // 重置缓存
                msg.resetReaderIndex();
                // 读取缓存
                byte[] packageBuf = new byte[pos];
                msg.readBytes(packageBuf);
                log.trace("origin data : {}", byteArrHelper.toHexString(packageBuf));
                // 转义 转义后的值 已经去掉了 标识位
                packageBuf = jt808Helper.retrans(packageBuf);
                log.trace("trans data : {}", byteArrHelper.toHexString(packageBuf));
                // 校验失败 丢掉当前包 继续读取剩余的字节
                boolean verify = jt808Helper.verify(packageBuf);
                if (!verify) {
                    log.warn("verify failed {}", ctx.channel().remoteAddress());
                    return msg.readableBytes() > 0;
                }
                // 校验成功 检查 长度 并输出到下一步操作
                byte[] body = byteArrHelper.subByte(packageBuf, 2, 4);
                int sizeBuf = jt808Helper.getMsgBodyLength(body);
                boolean b = jt808Helper.hasPackage(body);
                int size;
                if(b){
                    size = sizeBuf + MEG_MIN_LEN_WITH_PKG - 2;
                }else{
                    size = sizeBuf + MSG_MIN_LEN - 2;
                }

                // 检查长度
                if(size == packageBuf.length) {
                    // 长度符合 输出
                    log.info("handle data : {}", byteArrHelper.toHexString(packageBuf));
                    out.add(packageBuf);
                    return msg.readableBytes() > 0;
                }
                // 长度不符合
                log.warn("wrong data length expected {}, real {} drop !", size, packageBuf.length);
                return msg.readableBytes() > 0;
            }else{
                log.warn("wrong data structure {}", ctx.channel().remoteAddress());
                for(int i = 0;i<msg.readableBytes();){
                    if( msg.readByte() == MSG_BROKER ){
                        // 如果发送数据不正确 找到下一个0x7e 截断后 再读取一遍
                        return true;
                    }
                }
            }
        }
        // 处理成功，让父类进行接收下个包
        return false;
    }
}
