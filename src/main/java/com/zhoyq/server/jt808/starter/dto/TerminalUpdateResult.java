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

package com.zhoyq.server.jt808.starter.dto;

import lombok.Getter;

/**
 * 终端升级结果
 * @author zhoyq
 * @date 2018-06-27
 */
@Getter
public enum TerminalUpdateResult {

    /**
     * 成功
     */
    SUCCESS(0x00),
    /**
     * 失败
     */
    FAILED(0x01),
    /**
     * 取消
     */
    CANCEL(0x02),

    /**
     * 苏标：未找到目标设备
     */
    NOT_FOUND(0x10),
    /**
     * 苏标：硬件型号不支持
     */
    NOT_SUPPORT_HARDWARE(0x11),
    /**
     * 苏标：软件版本相同
     */
    SAME_VERSION(0x12),
    /**
     * 苏标：软件版本不支持
     */
    NOT_SUPPORT_SOFTWARE(0x13),
    ;

    private byte value;
    TerminalUpdateResult(int value) {
        this.value = (byte)value;
    }

    public static TerminalUpdateResult VALUE_OF(byte b) {
        for (TerminalUpdateResult value : TerminalUpdateResult.values()) {
            if (value.getValue() == b) {
                return value;
            }
        }
        return null;
    }
}
