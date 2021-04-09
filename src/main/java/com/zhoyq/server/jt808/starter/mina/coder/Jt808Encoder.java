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

package com.zhoyq.server.jt808.starter.mina.coder;

import com.zhoyq.server.jt808.starter.core.Coder;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.springframework.stereotype.Component;


/**
 * @author zhoyq
 * @date 2018-06-22
 */
@Slf4j
@Component
@AllArgsConstructor
public class Jt808Encoder implements ProtocolEncoder {

    private Coder coder;

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
        byte[] buf = (byte[])message;
        byte[] buffer = coder.encode(buf);
        out.write(IoBuffer.wrap(buffer));
    }

    @Override
    public void dispose(IoSession ioSession) { }
}
