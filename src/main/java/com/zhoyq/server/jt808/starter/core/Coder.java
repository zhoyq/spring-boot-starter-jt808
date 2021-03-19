package com.zhoyq.server.jt808.starter.core;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2020-10-27
 */
@Slf4j
@Component
@AllArgsConstructor
public class Coder {

    private Jt808Helper jt808Helper;
    private ByteArrHelper byteArrHelper;

    private static final byte MSG_BROKER = 0x7E;
    /**
     * 含标识位
     */
    private static final int MSG_MIN_LEN = 15;
    private static final int MSG_MIN_LEN_2019 = 20;
    /**
     * 含标识位
     */
    private static final int MEG_MIN_LEN_WITH_PKG = 19;
    private static final int MEG_MIN_LEN_WITH_PKG_2019 = 24;
    private static final int MAX_READ_LEN = 1024 * 10;

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
     */
    public boolean decode(BufferWrapper wrapper) {
        // 有数据时，读取前 8 字节判断消息长度
        if (wrapper.remaining() > 0) {
            // 标记初始位置
            wrapper.mark();
            // 读取一字节 检查是否标识位
            byte byteBuf = wrapper.get();
            if( byteBuf == MSG_BROKER ){
                // 设置一个长度缓存
                int pos = 1;
                // 是标识位 循环读取 直到 下一个标识位 为止
                for(int i = 0;i < wrapper.remaining();){
                    byteBuf = wrapper.get();
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
                        wrapper.reset();
                    }
                    return false;
                }
                // 小于最小包长度 截断 重新读取 剩余的 字节
                if (pos < MSG_MIN_LEN) {
                    log.warn("data is too short ... drop !");
                    return wrapper.remaining() > 0;
                }
                // 重置缓存
                wrapper.reset();
                // 读取缓存
                byte[] packageBuf = new byte[pos];
                wrapper.get(packageBuf);
                log.trace("origin data : {}", byteArrHelper.toHexString(packageBuf));
                // 转义 转义后的值 已经去掉了 标识位
                packageBuf = jt808Helper.retrans(packageBuf);
                log.trace("trans data : {}", byteArrHelper.toHexString(packageBuf));
                // 校验失败 丢掉当前包 继续读取剩余的字节
                boolean verify = jt808Helper.verify(packageBuf);
                if (!verify) {
                    log.warn("verify failed {}", wrapper.getRemoteAddress());
                    return wrapper.remaining() > 0;
                }
                // 校验成功 检查 长度 并输出到下一步操作
                byte[] body = byteArrHelper.subByte(packageBuf, 2, 4);
                int sizeBuf = jt808Helper.getMsgBodyLength(body);
                boolean version2019 = jt808Helper.isVersion2019(body);
                boolean b = jt808Helper.hasPackage(body);
                int size;
                if(b){
                    if (version2019) {
                        size = sizeBuf + MEG_MIN_LEN_WITH_PKG_2019 - 2;
                    } else {
                        size = sizeBuf + MEG_MIN_LEN_WITH_PKG - 2;
                    }
                }else{
                    if (version2019) {
                        size = sizeBuf + MSG_MIN_LEN_2019 - 2;
                    } else {
                        size = sizeBuf + MSG_MIN_LEN - 2;
                    }
                }

                // 检查长度
                if(size == packageBuf.length) {
                    // 长度符合 输出
                    log.info("handle data : {}", byteArrHelper.toHexString(packageBuf));
                    wrapper.write(packageBuf);
                    return wrapper.remaining() > 0;
                }
                // 长度不符合
                log.warn("wrong data length expected {}, real {} drop !", size, packageBuf.length);
                return wrapper.remaining() > 0;
            }else{
                log.warn("wrong data structure {}", wrapper.getRemoteAddress());
                for(int i = 0;i<wrapper.remaining();){
                    if( wrapper.get() == MSG_BROKER ){
                        // 如果发送数据不正确 找到下一个0x7e 截断后 再读取一遍
                        return true;
                    }
                }
            }
        }
        // 处理成功，让父类进行接收下个包
        return false;
    }

    public byte[] encode(byte[] buf) {
        // 添加验证 转义
        return jt808Helper.trans(jt808Helper.addVerify(buf));
    }
}
