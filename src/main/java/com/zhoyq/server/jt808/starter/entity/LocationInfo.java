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

import java.util.List;

/**
 * 定位信息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Builder
@Setter
@Getter
public class LocationInfo {
    /**
     * 报警信息
     */
    private AlarmInfo alarmInfo;

    /**
     * 状态信息
     */
    private StatusInfo statusInfo;

    /**
     * 纬度
     */
    private double longitude;

    /**
     * 经度
     */
    private double latitude;

    /**
     * 高程
     */
    private int height;

    /**
     * 速度
     */
    private double speed;

    /**
     * 方向
     */
    private int direction;

    /**
     * 时间 YY-MM-DD-hh-mm-ss GMT+8 时间
     */
    private String datetime;

    /**
     * 附加信息
     */
    private List<LocationAttachInfo> attachInfo;
}
