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

import com.zhoyq.server.jt808.starter.config.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 苏标 数据下行透传 查询基本信息 外设ID
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/9
 */
@Slf4j
@Getter
@Setter
public class DataTransportDeviceStatus {
    /**
     * 外设ID
     */
    private int deviceId;
    /**
     * 工作状态
     */
    private int workStatus;

    /**
     * 报警状态: 摄像头异常
     */
    private boolean cameraAbnormal;

    /**
     * 报警状态: 主存储器异常
     */
    private boolean mainStorageAbnormal;

    /**
     * 报警状态: 辅存储器异常
     */
    private boolean secondaryStorageAbnormal;

    /**
     * 报警状态: 红外补光异常
     */
    private boolean infraredFillLightAbnormal;

    /**
     * 报警状态: 扬声器异常
     */
    private boolean speakerAbnormal;

    /**
     * 报警状态: 电池异常
     */
    private boolean batteryAbnormal;

    /**
     * 报警状态: 通信模块异常
     */
    private boolean communicationModuleAbnormal;

    /**
     * 报警状态: 定位模块异常
     */
    private boolean positioningModuleAbnormal;

    public static List<DataTransportDeviceStatus> fromBytes(byte[] subByte) {
        List<DataTransportDeviceStatus> ret = new ArrayList<>();
        int pos = 0;
        while(pos < subByte.length) {
            try {
                DataTransportDeviceStatus status = new DataTransportDeviceStatus();
                status.setDeviceId(subByte[pos++]);
                pos++;
                status.setWorkStatus(subByte[pos++]);
                byte data1 = subByte[pos++];
                byte data2 = subByte[pos++];
                byte data3 = subByte[pos++];
                byte data4 = subByte[pos++];
                status.setCameraAbnormal((data4 & Const.BIN_0X01) == Const.BIN_0X01);
                status.setMainStorageAbnormal((data4 & Const.BIN_0X02) == Const.BIN_0X02);
                status.setSecondaryStorageAbnormal((data4 & Const.BIN_0X04) == Const.BIN_0X04);
                status.setInfraredFillLightAbnormal((data4 & Const.BIN_0X08) == Const.BIN_0X08);
                status.setSpeakerAbnormal((data4 & Const.BIN_0X10) == Const.BIN_0X10);
                status.setBatteryAbnormal((data4 & Const.BIN_0X20) == Const.BIN_0X20);
                status.setCommunicationModuleAbnormal((data3 & Const.BIN_0X04) == Const.BIN_0X04);
                status.setPositioningModuleAbnormal((data3 & Const.BIN_0X08) == Const.BIN_0X08);
                ret.add(status);
            }catch (Exception e){
                log.warn(e.getMessage());
                break;
            }
        }
        return ret;
    }
}
