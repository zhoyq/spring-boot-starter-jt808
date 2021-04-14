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
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;

/**
 * 终端升级包
 * @author zhoyq
 * @date 2018-06-27
 */
@Setter
@Getter
public class TerminalUpdatePkg {

    /**
     * 升级类型
     */
    private TerminalUpdatePkgType updateType;
    /**
     * 制造商ID
     */
    private String producerId;
    /**
     * 终端固件版本号
     */
    private String version;
    /**
     * 升级数据包
     */
    private byte[] data;

    public byte[] toBytes() {
        byte[] producerId = ByteArrHelper.hexStr2bytes(this.getProducerId());
        // 不足五字节 补足
        while (producerId.length < 5) {
            producerId = ByteArrHelper.union(new byte[]{0x00}, producerId);
        }
        byte[] version = this.version.getBytes(Charset.forName("GBK"));
        return ByteArrHelper.union(
                new byte[]{this.updateType.getValue()},
                // 超过 五字节 截断
                ByteArrHelper.subByte(producerId, producerId.length - 5),
                new byte[]{(byte)version.length},
                version,
                ByteArrHelper.int2fourbytes(data.length),
                this.data);
    }
}
