package com.zhoyq.server.jt808.starter.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2020-10-27
 */
@Component
public class BufferWrapper {

    private IoSession session;
    private IoBuffer in;
    private ProtocolDecoderOutput out;
    private Boolean minaFlag;

    private ChannelHandlerContext ctx;
    private ByteBuf msg;
    private List<Object> nettyOut;

    public BufferWrapper () { }

    public void init(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        this.session = session;
        this.in = in;
        this.out = out;
        this.minaFlag = true;
    }

    public void init(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        this.ctx = ctx;
        this.msg = msg;
        this.nettyOut = out;
        this.minaFlag = false;
    }

    int remaining() {
        if (this.minaFlag) {
            return in.remaining();
        }
        return msg.readableBytes();
    }

    void mark() {
        if (this.minaFlag) {
            in.mark();
        } else {
            msg.markReaderIndex();
        }
    }

    public byte get() {
        if (this.minaFlag) {
            return in.get();
        }
        return msg.readByte();
    }

    void reset() {
        if (this.minaFlag) {
            in.reset();
        } else {
            msg.resetReaderIndex();
        }
    }

    public void get(byte[] buf) {
        if (this.minaFlag) {
            in.get(buf);
        } else {
            msg.readBytes(buf);
        }
    }

    String getRemoteAddress() {
        if (this.minaFlag) {
            return session.getRemoteAddress().toString();
        }
        return ctx.channel().remoteAddress().toString();
    }

    void write(byte[] buf) {
        if(minaFlag){
            out.write(buf);
        } else {
            nettyOut.add(buf);
        }
    }
}
