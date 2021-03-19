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

import com.zhoyq.server.jt808.starter.core.BufferWrapper;
import com.zhoyq.server.jt808.starter.core.Coder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@AllArgsConstructor
public class Jt808NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final AttributeKey<ByteBuf> BUFFER = AttributeKey.valueOf("buffer");

    private Coder coder;
    private BufferWrapper bufferWrapper;

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
        bufferWrapper.init(ctx, msg, out);
        return coder.decode(bufferWrapper);
    }
}
