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

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/11
 */
@Slf4j
@Setter
@Getter
public class TerminalParameters {
    private List<TerminalParameter> parameters;

    public void addParameter(TerminalParameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }

        parameters.add(parameter);
    }

    /**
     * 获取终端参数个数
     */
    public int getCount() {
        return this.parameters == null ? 0 : this.parameters.size();
    }

    /**
     * 转换消息到二进制
     */
    public byte[] toBytes() {
        byte[] data = new byte[]{(byte) this.parameters.size()};
        for (TerminalParameter parameter : parameters) {
            data = ByteArrHelper.union(data, parameter.toBytes());
        }
        return data;
    }

    public static TerminalParameters fromBytes(byte[] data) {
        if (data == null) {
            return null;
        }
        TerminalParameters terminalParameters = new TerminalParameters();
        int pos = 0;
        while(pos < data.length){
            int length = data[pos + 2];
            int totalLength = length + 3;
            byte[] buf = ByteArrHelper.subByte(data, pos, pos + totalLength);
            pos += totalLength;

            terminalParameters.addParameter(TerminalParameter.fromBytes(buf));
        }
        return terminalParameters;
    }
}
