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

package com.zhoyq.server.jt808.starter.helper;

import com.zhoyq.server.jt808.starter.config.Const;

import java.math.BigInteger;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/1/20
 */
public class ByteArrHelper {

    // 求取最大公约数
    /*
    1.求任意方程ax+by=n的一个整数解
    用扩展欧几里得算法求解ax+by=gcd（a，b）后，利用它可以进一步解任意方程ax+by=n得到一个整数解，其步骤如下：
    （1）判断方程ax+by=n是否存在整数解，有解的条件是gcd（a，b）可以整除n
    （2）用扩展欧几里得算法求ax+by=gcd（a，b）的一个解（x0，y0）
    （3） 在ax0+by0=gcd（a，b）两边同时乘以n/gcd（a，b），得：
    ax0n/gcd（a，b）+by0n/gcd（a，b）=n
    （4）对照ax+by=n，得到它的一个解（x，y）是：
    x=x0n/gcd（a，b）
    y=y0n/gcd（a，b）
     */
    public static BigInteger findGcd(BigInteger n, BigInteger n1) {
        while (!n1.equals(BigInteger.ZERO)) {
            BigInteger rem = n.mod(n1);
            n = n1;
            n1 = rem;
        }
        return n;
    }

    /**
     * 将字节数组翻译成16进制字符串
     *
     * @param buf 字节数据
     * @return 十六进制字符串
     */
    public static String toHexString(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String str = Integer.toHexString(b);
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
     * @param subByte 字节数据
     * @return BCD编码字符串
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
     * @param subByte 字节数据
     * @return BCD编码数据
     */
    public static String getBCDStrByArr(byte[] subByte) {
        StringBuilder buf = new StringBuilder();
        for (byte b : subByte) {
            buf.append(getBCDStr(new byte[]{b}));
        }
        return buf.toString();
    }

    /**
     * 将字节转换成16进制字符串
     *
     * @param buf 字节码
     * @return 十六进制字符
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
     * 拼接多个字节数组
     *
     * @param b 数组列表
     * @return 拼接后的数组
     */
    public static byte[] union(byte[] ... b) {
        byte[] buf;
        int len = 0;
        for (byte[] bytes : b) {
            len += bytes.length;
        }
        buf = new byte[len];
        int pos = 0;
        for (byte[] bytes : b) {
            for (byte aByte : bytes) {
                buf[pos] = aByte;
                pos++;
            }
        }
        return buf;
    }

    /**
     * 截取指定位置到末尾的字节数组 start是数组脚标 从0开始
     *
     * @param data 原始数组
     * @param start 截取起点
     * @return 截取后的数组
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
     * @param data 原始数组
     * @param start 截取起点
     * @param end 截取终点
     * @return 截取后的数组
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
     * @param b 需要转换的字节数据
     * @return 转换后的整型变量
     */
    public static int fourbyte2int(byte[] b) {
        return ((((b[0] << 24) & 0xff000000) ^ ((b[1] << 16) & 0x00ff0000))
                ^ ((b[2] << 8) & 0x0000ff00)) ^ (b[3] & 0x000000ff);
    }

    /**
     * 二字节数组转int
     *
     * @param b 需要转换的字节数据
     * @return 转换后的整型变量
     */
    public static int twobyte2int(byte[] b) {
        return ((b[0] << 8) & 0xff00) ^ (b[1] & 0x00ff);
    }

    /**
     * int 转 二字节数组
     *
     * @param n 需要转换的整型数据
     * @return 转换后的字节变量
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
     * @param n 需要转换的整型数据
     * @return 转换后的字节变量
     */
    public static byte[] int2fourbytes(int n) {
        byte[] buf = new byte[4];
        buf[0] = (byte) ((n >>> 24) & 0x000000ff);
        buf[1] = (byte) ((n >>> 16) & 0x000000ff);
        buf[2] = (byte) ((n >>> 8) & 0x000000ff);
        buf[3] = (byte) (n & 0x000000ff);
        return buf;
    }

    /**
     * 十六进制字符串转字节码
     * @param hex 需要转换的十六进制字符串
     * @return 转换后的字节码
     */
    public static byte[] hexStr2bytes(String hex){
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

    /**
     * 长整型转字节码
     * @param values 需要转换的长整型数据
     * @return 转换后的字节码
     */
    public static byte[] long2eightbytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * 字节码转长整型
     * @param buffer 需要转换的字节数据
     * @return 转换后的长整型
     */
    public static long eightbytes2long(byte[] buffer) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values|= (buffer[i] & 0xff);
        }
        return values;
    }
}
