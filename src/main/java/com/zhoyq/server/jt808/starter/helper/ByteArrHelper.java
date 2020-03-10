/*
 *  Copyright (c) 2020. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2020.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2020. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.helper;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/1/20
 */
public class ByteArrHelper {

    /**
     * 将字节数组翻译成16进制字符串
     *
     * @param buf
     * @return
     */
    public static String toHexString(byte[] buf) {
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

    /**
     * 将字节数组翻译成BCD码字符串
     * 字节数组长度为1
     * @param subByte
     * @return
     */
    public static String getBCDStr(byte[] subByte) {
        byte b = subByte[0];
        byte b1 = (byte) (b & 0x0f);
        byte b2 = (byte) ((b >>> 4) & 0x0f);
        String str = ("" + b2) + b1;
        if (str.length() == 2) {
            return str;
        } else {
            return "99";
        }
    }

    /**
     * 将字节数组翻译成BCD码字符串
     *
     * @param subByte
     * @return
     */
    public static String getBCDStrByArr(byte[] subByte) {
        String buf = "";
        for(int i=0;i<subByte.length;i++){
            buf += getBCDStr(new byte[]{subByte[i]});
        }
        return buf;
    }

    /**
     * 将字节转换成16进制字符串
     *
     * @param buf
     * @return
     */
    public static String toHexString(byte buf) {
        String str = Integer.toHexString(buf);
        if (str.length() > 2) {
            str = str.substring(str.length() - 2);
        } else if (str.length() < 2) {
            str = "0" + str;
        }
        return str.toUpperCase();
    }

    /**
     * 拼接两个字节数组
     *
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] union(byte[] b1, byte[] b2) {
        byte[] buf = new byte[b1.length + b2.length];
        for (int i = 0; i < b1.length; i++) {
            buf[i] = b1[i];
        }
        for (int i = 0; i < b2.length; i++) {
            buf[b1.length + i] = b2[i];
        }
        return buf;
    }

    /**
     * 拼接多个字节数组
     *
     * @param b
     * @return
     */
    public static byte[] union(byte[] ... b) {
        byte[] buf;
        int len = 0;
        for(int i=0;i<b.length;i++){
            len += b[i].length;
        }
        buf = new byte[len];
        int pos = 0;
        for(int i=0;i<b.length;i++){
            for(int j=0;j<b[i].length;j++){
                buf[pos] = b[i][j];
                pos ++;
            }
        }
        return buf;
    }

    /**
     * 截取指定位置到末尾的字节数组 start是数组脚标 从0开始
     *
     * @param data
     * @param start
     * @return
     */
    public static byte[] subByte(byte[] data, int start) {
        byte[] buf = new byte[data.length - start];
        for (int n = 0, i = start; i < data.length; i++, n++) {
            buf[n] = data[i];
        }
        return buf;
    }

    /**
     * 截取指定位置的字节数组 start end是数组脚标 从0开始 算start 不算end
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    public static byte[] subByte(byte[] data, int start, int end) {
        byte[] buf = new byte[end - start];
        for (int n = 0, i = start; i < end; i++, n++) {
            buf[n] = data[i];
        }
        return buf;
    }

    /**
     * 四字节数组转int
     *
     * @param b
     * @return
     */
    public static int fourbyte2int(byte[] b) {
        return ((((b[0] << 24) & 0xff000000) ^ ((b[1] << 16) & 0x00ff0000))
                ^ ((b[2] << 8) & 0x0000ff00)) ^ (b[3] & 0x000000ff);
    }

    /**
     * 二字节数组转int
     *
     * @param b
     * @return
     */
    public static int twobyte2int(byte[] b) {
        return ((b[0] << 8) & 0xff00) ^ (b[1] & 0x00ff);
    }

    /**
     * int 转 二字节数组
     *
     * @param n
     * @return
     */
    public static byte[] int2twobytes(int n) {
        byte[] buf = new byte[2];
        buf[0] = (byte) ((n >>> 8) & 0x000000ff);
        buf[1] = (byte) (n & 0x000000ff);
        return buf;
    }

    /**
     * int 转 四字节数组
     *
     * @param n
     * @return
     */
    public static byte[] int2fourbytes(int n) {
        byte[] buf = new byte[4];
        buf[0] = (byte) ((n >>> 24) & 0x000000ff);
        buf[1] = (byte) ((n >>> 16) & 0x000000ff);
        buf[2] = (byte) ((n >>> 8) & 0x000000ff);
        buf[3] = (byte) (n & 0x000000ff);
        return buf;
    }
}
