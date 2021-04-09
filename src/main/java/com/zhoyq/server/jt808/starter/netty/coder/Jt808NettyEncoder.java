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

import com.zhoyq.server.jt808.starter.core.Coder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/17
 */
@AllArgsConstructor
public class Jt808NettyEncoder extends MessageToMessageEncoder<byte[]> {

    private Coder coder;

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        byte[] buf = coder.encode(msg);
        out.add(Unpooled.wrappedBuffer(buf));
    }
}
