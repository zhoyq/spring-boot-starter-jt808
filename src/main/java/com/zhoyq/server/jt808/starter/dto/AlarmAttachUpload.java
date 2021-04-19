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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * TODO 报警附件上传指令数据 0x9208
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/13
 */

@Slf4j
@Setter
@Getter
public class AlarmAttachUpload {
    /**
     * 服务器IP地址
     */
    private String attachServerIp;
    /**
     * 使用TCP传输时 服务器端口号
     */
    private int attachServerTcpPort;
    /**
     * 使用UDP传输时 服务器端口号
     */
    private int attachServerUdpPort;
    /**
     * 报警标识号 来自终端 16字节
     */
    private byte[] terminalAlarmId;
    /**
     * 报警编号 来自平台 32 字节
     */
    private byte[] platformAlarmId;

    public byte[] toBytes() {
        try {
            byte[] serverIp = attachServerIp.getBytes("GBK");
            return ByteArrHelper.union(new byte[]{(byte)serverIp.length}, serverIp,
                    ByteArrHelper.int2twobytes(attachServerTcpPort),
                    ByteArrHelper.int2twobytes(attachServerUdpPort),
                    terminalAlarmId,
                    platformAlarmId,
                    new byte[16]
            );
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static byte[] generatePlatformAlarmId() {
        return UUID.randomUUID().toString().replaceAll("-", "").getBytes(StandardCharsets.US_ASCII);
    }
}
