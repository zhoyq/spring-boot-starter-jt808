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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/17
 */
@Slf4j
@Component
public class Jt808NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final AttributeKey<ByteBuf> BUFFER = AttributeKey.valueOf("buffer");

    private static final byte MSG_BROKER = 0x7E;
    private static final int MSG_MIN_LEN = 15;
    private static final int MEG_MIN_LEN_WITH_PKG = 19;

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
            msg.markReaderIndex();
            byte byteBuf = msg.readByte();
            if( byteBuf == MSG_BROKER ){
                // 还原读取情况
                msg.resetReaderIndex();
                byte[] bodyProp = new byte[5];
                msg.markReaderIndex();
                msg.readBytes(bodyProp, 0, 5);
                byte[] body = new byte[]{bodyProp[3],bodyProp[4]};
                int sizeBuf = Jt808Helper.getMsgBodyLength(body);
                boolean b = Jt808Helper.hasPackage(body);
                int size;
                if(b){
                    size = sizeBuf + MEG_MIN_LEN_WITH_PKG;
                }else{
                    size = sizeBuf + MSG_MIN_LEN;
                }
                // 还原读取情况
                msg.resetReaderIndex();
                log.trace("the real pkg length is {}", size);

                // 如果消息内容不够，则重置，相当于不读取size
                if (size > msg.readableBytes() || size < MSG_MIN_LEN ) {
                    // 标记
                    msg.markReaderIndex();
                    // 读取以显示
                    byte[] bytes = new byte[msg.readableBytes()];
                    msg.readBytes(bytes, 0, msg.readableBytes());
                    // 还原
                    msg.resetReaderIndex();
                    log.trace("short data length {} data {} go to reread {} ",
                            msg.readableBytes(), ByteArrHelper.toHexString(bytes), ctx.channel().remoteAddress());
                } else {
                    byte[] bytes = new byte[size];
                    msg.readBytes(bytes, 0, size);
                    // 验证得到的数据是否正确
                    if( bytes[bytes.length-1] == MSG_BROKER ){
                        log.trace("origin data {} {}", ByteArrHelper.toHexString(bytes), ctx.channel().remoteAddress());
                        // 这里转义还原
                        bytes = Jt808Helper.retrans(bytes);
                        // 在这里验证校验码
                        if(Jt808Helper.verify(bytes)){
                            // 把字节转换为Java对象的工具类
                            out.add(bytes);
                            // 如果读取内容后还粘了包，就让父类再重读 一次，进行下一次解析
                            return msg.readableBytes() > 0;
                        }else{
                            // 如果读取内容后还粘了包，就让父类再重读 一次，进行下一次解析
                            return msg.readableBytes() > 0;
                        }
                    }else{
                        log.trace("wrong data to drop {} {}", ByteArrHelper.toHexString(bytes), ctx.channel().remoteAddress());
                        //如果按格式获取数据后 末尾不是0x7e 或者校验位不对 直接丢弃 还有剩余数据
                        //继续使用 没有 进行吓一条数据
                        return msg.readableBytes() > 0;
                    }
                }
            }else{
                log.trace("wrong data structure {}", ctx.channel().remoteAddress());
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
