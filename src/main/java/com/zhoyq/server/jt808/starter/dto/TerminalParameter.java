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

package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.nio.charset.Charset;

/**
 * TODO 终端控制参数
 * @author zhoyq
 * @date 2018-06-27
 */
@Setter
@Getter
public class TerminalParameter {
    /**
     * 参数ID
     */
    private TerminalParameterId parameterId;

    /**
     * 参数值
     */
    private byte[] value;

    public void setDwordValue(int value){
        this.value = ByteArrHelper.int2fourbytes(value);
    }

    public void setStringValue(String value){
        this.value = value.getBytes(Charset.forName("GBK"));
    }

    public void setWordValue(int value){
        this.value = ByteArrHelper.int2twobytes(value);
    }

    public void setByteValue(byte value) {
        this.value = new byte[]{value};
    }

    public void setIllegalDrivingTime(int fromHour, int fromMinute, int toHour, int toMinute) {
        this.value = new byte[]{(byte) fromHour, (byte) fromMinute, (byte) toHour, (byte) toMinute};
    }

//    public void setAudioOrVideoSetting() {
//
//    }

    /**
     * 转换消息到二进制
     */
    public byte[] toBytes() {
        return ByteArrHelper.union(parameterId.getValue(), new byte[]{(byte)value.length}, value);
    }

    /**
     * 获取终端参数对象
     * @param data 获取对象二进制数据
     * @return 终端参数对象
     */
    public static TerminalParameter fromBytes(byte[] data) {
        TerminalParameter terminalParameter = new TerminalParameter();
        terminalParameter.setParameterId(TerminalParameterId.VALUE_OF(new byte[]{data[0], data[1], data[2], data[3]}));
        terminalParameter.setValue(ByteArrHelper.subByte(data, 5));
        return terminalParameter;
    }
}

