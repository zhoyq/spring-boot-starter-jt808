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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/20
 */
@Slf4j
@Setter
@Getter
public class CanDataItem {

    private byte[] pack;
    private int headInt;

    /**
     * CAN总线ID
     */
    public int canID() {
        return headInt & 0x1fffffff;
    }
    /**
     * 通道号
     * 0 CAN1 | 1 CAN2
     */
    private int canTunnel() {
        return headInt & 0x80000000 >> 31;
    }

    /**
     * 帧类型
     * 0 标准帧 | 1 扩展帧
     */
    private int frameType() {
        return headInt & 0x40000000 >> 30;
    }

    /**
     * 数据采集方式
     * 0 原始数据 | 1 采集区间的平均值
     */
    private int dataCollectModel() {
        return headInt & 0x20000000 >> 29;
    }

    public byte[] toBytes() {
        return this.pack;
    }

    public static CanDataItem fromBytes(byte[] data) {
        if (data.length != 12) {
            log.warn("CanDataItem fromBytes data is bad!");
            return null;
        }

        CanDataItem item = new CanDataItem();
        item.setPack(data);
        item.setHeadInt(ByteArrHelper.fourbyte2int(new byte[]{
                data[0], data[1], data[2], data[3]
        }));

        return item;
    }
}
