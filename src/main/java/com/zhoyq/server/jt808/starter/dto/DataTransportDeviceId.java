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
 * 苏标 数据下行透传 查询基本信息 外设ID
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/9
 */
@Getter
public enum DataTransportDeviceId {
    /**
     * 驾驶辅助设备
     */
    DRIVING_ASSISTANCE((byte)0x64),
    /**
     * 驾驶员行为监测设备
     */
    DRIVER_BEHAVIOR_MONITORING((byte) 0x65),
    /**
     * 轮胎状态监测
     */
    TIRE_CONDITION_MONITORING((byte) 0x66),
    /**
     * 变道决策辅助
     */
    LANE_CHANGE_DECISION_ASSISTANCE((byte) 0x67);

    private byte value;
    DataTransportDeviceId(byte value){
        this.value = value;
    }
}
