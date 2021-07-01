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
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/20
 */
@Setter
@Getter
public class CanDataInfo {

    /**
     * 数据接受时间戳
     */
    private long timestamp;

    /**
     * 数据项个数
     */
    private byte[] dataNumber;

    public int dataNumber() {
        return ByteArrHelper.twobyte2int(this.dataNumber);
    }

    /**
     * 数据接收时间 hh-mm-ss-msms
     */
    private byte[] receiveTime;

    public String receiveTime() {
        return ByteArrHelper.getBCDStr(new byte[]{receiveTime[0]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{receiveTime[1]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{receiveTime[2]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{receiveTime[3]}) +
                ByteArrHelper.getBCDStr(new byte[]{receiveTime[4]});
    }

    /**
     * CAN总线数据项
     */
    private byte[] data;

    public List<CanDataItem> data() {
        List<CanDataItem> ret = new ArrayList<>();
        int pos = 0;
        while (pos < data.length) {
            byte[] buf = ByteArrHelper.subByte(data, pos,pos + 12);
            ret.add(CanDataItem.fromBytes(buf));
            pos += 12;
        }
        return ret;
    }

    public byte[] toBytes() {
        return ByteArrHelper.union(
                dataNumber,
                receiveTime,
                data
        );
    }

    public static CanDataInfo fromBytes(byte[] data) {
        CanDataInfo canDataInfo = new CanDataInfo();

        canDataInfo.setTimestamp(System.currentTimeMillis());
        canDataInfo.setDataNumber(new byte[]{data[0], data[1]});
        canDataInfo.setReceiveTime(new byte[]{data[2], data[3], data[4], data[5], data[6]});
        canDataInfo.setData(ByteArrHelper.subByte(data, 7));
        return canDataInfo;
    }
}
