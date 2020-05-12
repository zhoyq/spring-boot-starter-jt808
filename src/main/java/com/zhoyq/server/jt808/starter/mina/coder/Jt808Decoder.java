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

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoyq
 * @date 2018-06-22
 */
@Slf4j
@Component
public class Jt808Decoder extends CumulativeProtocolDecoder {

    @Autowired
    private Jt808Helper jt808Helper;
    @Autowired
    private ByteArrHelper byteArrHelper;

    private static final byte MSG_BROKER = 0x7E;
    private static final int MSG_MIN_LEN = 15;
    private static final int MEG_MIN_LEN_WITH_PKG = 19;

    /**
     * 这里处理的是网络原因引起的分包 粘包
     * 1、当内容刚好时，返回false，告知父类接收下一批内容
     * 2、内容不够时需要下一批发过来的内容，此时返回false，这样父类 CumulativeProtocolDecoder
     *    会将内容放进IoSession中，等下次来数据后就自动拼装再交给本类的doDecode
     * 3、当内容多时，返回true，因为需要再将本批数据进行读取，父类会将剩余的数据再次推送本
     *    类的doDecode
     */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        // 有数据时，读取前 8 字节判断消息长度
        if (in.remaining() > 0) {
            in.mark();
            byte byteBuf = in.get();
            if( byteBuf == MSG_BROKER ){
                // 还原读取情况
                in.reset();
                byte[] bodyProp = new byte[5];
                in.mark();
                in.get(bodyProp, 0, 5);
                byte[] body = new byte[]{bodyProp[3],bodyProp[4]};
                int sizeBuf = jt808Helper.getMsgBodyLength(body);
                boolean b = jt808Helper.hasPackage(body);
                int size;
                if(b){
                    size = sizeBuf + MEG_MIN_LEN_WITH_PKG;
                }else{
                    size = sizeBuf + MSG_MIN_LEN;
                }
                // 还原读取情况
                in.reset();
                log.trace("the real pkg length is " + size);

                // 如果消息内容不够，则重置，相当于不读取size
                if (size > in.remaining() || size < MSG_MIN_LEN ) {
                    // 标记
                    in.mark();
                    // 读取以显示
                    byte[] bytes = new byte[in.remaining()];
                    in.get(bytes, 0, in.remaining());
                    // 还原
                    in.reset();
                    log.trace("short data length "+in.remaining()+" data "+ byteArrHelper.toHexString(bytes) +" go to reread " + session.getRemoteAddress());
                } else {
                    byte[] bytes = new byte[size];
                    in.get(bytes, 0, size);
                    // 验证得到的数据是否正确
                    if( bytes[bytes.length-1] == MSG_BROKER ){
                        log.trace("origin data " + byteArrHelper.toHexString(bytes) + " " + session.getRemoteAddress());
                        // 这里转义还原
                        bytes = jt808Helper.retrans(bytes);
                        // 在这里验证校验码
                        if(jt808Helper.verify(bytes)){
                            // 把字节转换为Java对象的工具类
                            out.write(bytes);
                            // 如果读取内容后还粘了包，就让父类再重读 一次，进行下一次解析
                            return in.remaining() > 0;
                        }else{
                            // 如果读取内容后还粘了包，就让父类再重读 一次，进行下一次解析
                            return in.remaining() > 0;
                        }
                    }else{
                        log.trace("wrong data to drop " + byteArrHelper.toHexString(bytes) + " " + session.getRemoteAddress());
                        //如果按格式获取数据后 末尾不是0x7e 或者校验位不对 直接丢弃 还有剩余数据
                        //继续使用 没有 进行吓一条数据
                        return in.remaining() > 0;
                    }
                }
            }else{
                log.trace("wrong data structure " + session.getRemoteAddress());
                for(int i = 0;i<in.remaining();){
                    if( in.get() == MSG_BROKER ){
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
