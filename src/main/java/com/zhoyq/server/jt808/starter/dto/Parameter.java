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

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;

/**
 * 终端控制参数
 * @author zhoyq
 * @date 2018-06-27
 */
public class Parameter {

    // 参数ID
    private byte[] parameterId;
    // 参数值长度
    private byte length;
    // 参数值
    private byte[] value;

    public Parameter(byte[] b){
        this.parameterId = ByteArrHelper.subByte(b, 0, 4);
        this.length = b[4];
        this.value = ByteArrHelper.subByte(b, 5, this.length + 5);
    }

    public byte[] getParameterId() {
        return parameterId;
    }
    public void setParameterId(byte[] parameterId) {
        this.parameterId = parameterId;
    }
    public byte getLength() {
        return length;
    }
    public void setLength(byte length) {
        this.length = length;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }


}

