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
     * 数据接收时间 hh-mm-ss-msms
     */
    private String receiveTime;

    /**
     * CAN总线数据项
     */
    private List<CanDataItem> data;

    public static CanDataInfo fromBytes(byte[] data) {
        CanDataInfo canDataInfo = new CanDataInfo();

        canDataInfo.setTimestamp(System.currentTimeMillis());

        String time = ByteArrHelper.getBCDStr(new byte[]{data[2]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{data[3]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{data[4]}) + "-" +
                ByteArrHelper.getBCDStr(new byte[]{data[5]}) +
                ByteArrHelper.getBCDStr(new byte[]{data[6]});

        canDataInfo.setReceiveTime(time);
        canDataInfo.setData(new ArrayList<>());

        int pos = 7;
        while (pos < data.length) {
            byte[] buf = ByteArrHelper.subByte(data, pos,pos + 12);
            canDataInfo.getData().add(CanDataItem.fromBytes(buf));
            pos += 12;
        }

        return canDataInfo;
    }
}
