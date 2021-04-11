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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 透传消息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/20
 */
@Slf4j
@Setter
@Getter
public class DataTransportInfo {
    /**
     * 透传消息类型
     * {@link DataTransportType}
     */
    private DataTransportType type;

    /**
     * 透传消息数据
     */
    private byte[] data;

    /**
     * 0x8900
     * 设置 下行 苏标数据
     * @param ids 外设ID列表
     */
    public void setSuData(List<DataTransportDeviceId> ids) {
        if (ids == null || ids.size() == 0) {
            log.warn("no data");
            return;
        }
        this.data = new byte[ids.size() + 1];
        data[0] = (byte)ids.size();
        for (int i = 0; i < ids.size(); i++) {
            data[i + 1] = ids.get(i).getValue();
        }
    }

    /**
     * 0x0900
     * 获取上行数据中的消息列表总数
     */
    public int getSuDataCount() {
        if (data == null || data.length == 0) {
            return 0;
        }
        return data[0];
    }

    /**
     * 0x0900
     * 获取苏标设备状态数据
     * @return 设备状态数据封装
     */
    public List<DataTransportDeviceStatus> getSuDeviceStatus() {
        if (this.type == null || this.type.getValue() != DataTransportType.SU_STATUS.getValue()) {
            return null;
        }

        return DataTransportDeviceStatus.fromBytes(ByteArrHelper.subByte(this.data, 2));
    }

    /**
     * 0x0900
     * 获取苏标设备信息数据
     * @return 设备信息数据封装
     */
    public List<DataTransportDeviceInfo> getSuDeviceInfo() {
        if (this.type == null || this.type.getValue() != DataTransportType.SU_INFO.getValue()) {
            return null;
        }

        return DataTransportDeviceInfo.fromBytes(ByteArrHelper.subByte(this.data, 2));
    }

    /**
     * 返回拼合号的消息数据
     */
    public byte[] toBytes(){
        return ByteArrHelper.union(new byte[]{this.type.getValue()}, this.data);
    }

    /**
     * 转换 消息体 到 对象上
     * @param msgBody 消息体数据
     * @return 对象数据
     */
    public static DataTransportInfo fromMsgBody(byte[] msgBody) {
        DataTransportInfo info = new DataTransportInfo();
        info.setType(DataTransportType.VALUE_OF(msgBody[0]));
        info.setData(ByteArrHelper.subByte(msgBody, 1));
        return info;
    }


}
