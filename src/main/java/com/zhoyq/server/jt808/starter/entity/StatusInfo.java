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

package com.zhoyq.server.jt808.starter.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 车辆定位中的状态信息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Builder
@Setter
@Getter
public class StatusInfo {
    /**
     * 0：ACC 关；1： ACC 开
     */
    private boolean acc;

    /**
     * 0：未定位；1：定位
     */
    private boolean positioning;

    /**
     * 0：北纬；1：南纬
     */
    private boolean south;

    /**
     * 0：东经；1：西经
     */
    private boolean west;

    /**
     * 0：运营状态；1：停运状态
     */
    private boolean suspended;

    /**
     * 0：经纬度未经保密插件加密；1：经纬度已经保密插件加密
     */
    private boolean encryption;

    /**
     * 1 紧急刹车系统采集的前撞预警
     */
    private boolean brakeSystemWarning;

    /**
     * 1 车道偏移预警
     */
    private boolean laneDepartureWarning;

    /**
     * 0：空车；1：半载；2：保留；3：满载
     */
    private int cargo;

    /**
     * 0：车辆油路正常；1：车辆油路断开
     */
    private boolean oilBreak;

    /**
     * 0：车辆电路正常；1：车辆电路断开
     */
    private boolean circuitBreak;

    /**
     * 0：车门解锁；1：车门加锁
     */
    private boolean locking;

    /**
     * 0：门 1 关；1：门 1 开（前门）
     */
    private boolean opening1;

    /**
     * 0：门 2 关；1：门 2 开（中门）
     */
    private boolean opening2;

    /**
     * 0：门 3 关；1：门 3 开（后门）
     */
    private boolean opening3;

    /**
     * 0：门 4 关；1：门 4 开（驾驶席门）
     */
    private boolean opening4;

    /**
     * 0：门 5 关；1：门 5 开（自定义）
     */
    private boolean opening5;

    /**
     * 0：未使用 GPS 卫星进行定位；1：使用 GPS 卫星进行定位
     */
    private boolean gps;

    /**
     * 0：未使用北斗卫星进行定位；1：使用北斗卫星进行定位
     */
    private boolean beidou;

    /**
     * 0：未使用 GLONASS 卫星进行定位；1：使用 GLONASS 卫星进行定位
     */
    private boolean glonass;

    /**
     * 0：未使用 Galileo 卫星进行定位；1：使用 Galileo 卫星进行定位
     */
    private boolean galileo;

    /**
     * 0 车辆处于停止状态 1 车辆处于行驶状态
     */
    private boolean vehicleStatus;
}
