/*
 *  Copyright (c) 2021. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2021.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2021. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 苏标：报警标识号
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/19
 */
@Slf4j
@Setter
@Getter
public class SuAlarmIdentificationNumber {
    /**
     * 终端ID
     */
    private byte[] terminalId;
    /**
     * 时间 YYMMDDhhmmss
     */
    private byte[] datetime;
    /**
     * 序号
     * 同一时间点报警序号 从零开始
     */
    private byte number;
    /**
     * 附件数量
     * 标识对应报警的附件数量
     */
    private byte attachNumber;

    public String terminalId() {
        return new String(terminalId, StandardCharsets.US_ASCII);
    }

    public Date datetime() {
        return Jt808Helper.bytes2date(this.datetime);
    }

    public int number() {
        return this.number;
    }

    public int attachNumber() {
        return this.attachNumber;
    }

    public byte[] toBytes() {
        return ByteArrHelper.union(
                terminalId,
                datetime,
                new byte[]{number, attachNumber}
        );
    }

    public static SuAlarmIdentificationNumber fromBytes(byte[] data) {
        if (data.length != 16) {
            log.warn("AlarmIdentificationNumber fromBytes data is too long!");
            return null;
        }

        SuAlarmIdentificationNumber id = new SuAlarmIdentificationNumber();

        id.setTerminalId(ByteArrHelper.subByte(data, 0, 7));
        id.setDatetime(ByteArrHelper.subByte(data, 7, 13));
        id.setNumber(data[13]);
        id.setAttachNumber(data[14]);

        return id;
    }
}
