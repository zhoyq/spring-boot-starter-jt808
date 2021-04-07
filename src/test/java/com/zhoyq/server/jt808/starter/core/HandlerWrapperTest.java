package com.zhoyq.server.jt808.starter.core;

import com.zhoyq.server.jt808.starter.config.Const;
import org.junit.Test;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-07
 */
public class HandlerWrapperTest {

    public byte[] hexStr2bytes(String hex){
        if(hex.length()%2 != 0) {
            hex = "0" + hex;
        }
        hex = hex.toUpperCase();
        byte[] res = new byte[hex.length()/2];
        for(int i = 0;i<res.length;i++){
            int n = i*2;
            int n_1 = n+1;
            char c = hex.charAt(n);
            char c_1 = hex.charAt(n_1);
            int buf = Const.HEX_STR.indexOf(c);
            int buf_1 = Const.HEX_STR.indexOf(c_1);
            res[i] = (byte)(((buf<<4)&0x000000F0)^(buf_1&0x0000000f));
        }
        return res;
    }

    public byte[] subByte(byte[] data, int start) {
        byte[] buf = new byte[data.length - start];
        for (int n = 0, i = start; i < data.length; i++, n++) {
            buf[n] = data[i];
        }
        return buf;
    }

    public byte[] subByte(byte[] data, int start, int end) {
        byte[] buf = new byte[end - start];
        for (int n = 0, i = start; i < end; i++, n++) {
            buf[n] = data[i];
        }
        return buf;
    }

    public String toHexString(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String str = Integer.toHexString(buf[i]);
            if (str.length() > 2) {
                str = str.substring(str.length() - 2);
            } else if (str.length() < 2) {
                str = "0" + str;
            }
            sb.append(str);
        }
        return sb.toString().toUpperCase();
    }

    @Test
    public void handlePackage() {
        int offset = 0;
        byte[] originData = hexStr2bytes("07020007018911882267020D0221040710481637");
        byte[] msgBody;
        final byte[] msgId = new byte[]{originData[offset++],originData[offset++]};
        final byte[] msgBodyProp = new byte[]{originData[offset++],originData[offset++]};
        // 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析
        byte[] phoneNum;
        if ((msgBodyProp[0] & 0x40) == 0x40) {
            // 忽略 协议版本解析
            offset++;
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++]
            };
        } else {
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++]
            };
        }
        final byte[] streamNum = new byte[]{originData[offset++],originData[offset++]};
        int msgLen = 1024;
//        if(originData.length > msgLen){
//            // 超长的数据一定是分包合并后的数据 直接获取后边的数据即可 因为已经处理了尾部的校验位
//
//            // 过滤掉消息包封装项
//            // 感谢 https://github.com/bigbeef 提交的建议
//
//            offset += 4;
//            msgBody = subByte(originData, offset);
//        }else{
//            int bodyLength = originData.length-offset;
//            msgBody = new byte[bodyLength];
//            for(int i=0;i<msgBody.length;i++){
//                msgBody[i] = originData[offset++];
//            }
//        }
        msgBody = subByte(originData, offset);

        System.out.println(toHexString(msgBody));
    }
}
