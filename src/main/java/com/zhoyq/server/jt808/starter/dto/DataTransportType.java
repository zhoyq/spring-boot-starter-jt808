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

import lombok.Getter;

/**
 * 808 2011 版本未定义
 * 808 2013、2019 版本定义一致
 * 苏标 新增两种消息类型
 * 1078 无新增定义
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-08
 */
@Getter
public enum DataTransportType {
    // 2011
    // 2013 2019
    /**
     * GNSS模型详细定位数据
     */
    GNSS_LOCATION_DETAIL((byte)0x00),
    /**
     * 道路运输证 IC 卡信息上传消息为 64Byte，下传消息为 24Byte。道路运输证 IC 卡认证透传超时时间为 30s。 超时后，不重发
     */
    IC_CARD((byte)0x0B),
    /**
     * 串口 1 透传消息
     */
    COM_1((byte)0x41),
    /**
     * 串口 2 透传消息
     */
    COM_2((byte)0x42),

    // 苏标

    /**
     * 外设状态信息:外设工作状态、设备报警信息
     */
    SU_STATUS((byte)0xf7),

    /**
     * 外设传感器的基本信息:公司信息、产品代码、版本号、外设ID、客户代码。
     */
    SU_INFO((byte)0xf8)
    ;

    private byte value;

    DataTransportType(byte value) {
        this.value = value;
    }

    public static DataTransportType VALUE_OF(byte value){
        for (DataTransportType dataTransportType : DataTransportType.values()) {
            if (value == dataTransportType.getValue()) {
                return dataTransportType;
            }
        }
        return null;
    }
}
