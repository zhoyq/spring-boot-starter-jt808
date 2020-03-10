/*
 *  Copyright (c) 2020. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2020.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2020. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/19
 */
@Setter
@Getter
public class DriverInfo {
    /**
     * 司机名称
     */
    private String driverName;
    /**
     * 司机身份证号 仅2011
     */
    private String idCardNumber;
    /**
     * 司机从业资格证号码
     */
    private String certificateNumber;
    /**
     * 发证机构名称
     */
    private String certificatePublishAgentName;
    /**
     * 证件有效期 仅2013
     */
    private String certificateLimitDate;

    /**
     * 驾驶员身份采集问题
     */
    private DriverAlarmInfo driverAlarmInfo;

    /**
     * 插拔卡时间 YY-MM-DD-hh-mm-ss
     */
    private String datetime;

    /**
     * 是否采集成功 因为需要采集报警所以不能只传回空值
     */
    private boolean success = false;
}
