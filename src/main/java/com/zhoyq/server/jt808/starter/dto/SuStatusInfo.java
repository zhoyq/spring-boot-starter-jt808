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

/**
 * 苏标：车辆状态
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/19
 */
@Slf4j
@Setter
@Getter
public class SuStatusInfo {
    private byte[] data;

    /**
     * acc 状态标识
     */
    public boolean acc() {
        return (data[1] & Const.BIN_0X01) == Const.BIN_0X01;
    }

    /**
     * 左转向状态标识
     */
    public boolean turnLeft() {
        return (data[1] & Const.BIN_0X02) == Const.BIN_0X02;
    }

    /**
     * 右转向状态标识
     */
    public boolean turnRight() {
        return (data[1] & Const.BIN_0X04) == Const.BIN_0X04;
    }

    /**
     * 雨刮器状态标志
     */
    public boolean wiper() {
        return (data[1] & Const.BIN_0X08) == Const.BIN_0X08;
    }

    /**
     * 制动状态标志
     */
    public boolean brake() {
        return (data[1] & Const.BIN_0X10) == Const.BIN_0X10;
    }

    /**
     * 插卡状态标志
     */
    public boolean card() {
        return (data[1] & Const.BIN_0X20) == Const.BIN_0X20;
    }

    /**
     * 定位状态标志
     */
    public boolean location() {
        return (data[0] & Const.BIN_0X04) == Const.BIN_0X04;
    }

    public byte[] toBytes() {
        return this.data;
    }

    public static SuStatusInfo fromBytes(byte[] data) {
        if (data.length != 2) {
            log.warn("SuStatusInfo fromBytes data is too long!");
            return null;
        }
        SuStatusInfo instance = new SuStatusInfo();
        instance.setData(data);
        return instance;
    }
}
