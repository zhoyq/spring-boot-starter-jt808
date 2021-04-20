package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 苏标 报警附件信息
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuAlarmAttachInfo {
    /**
     * 终端ID
     */
    private String terminalId;
    /**
     * 报警标识号
     */
    private SuAlarmIdentificationNumber alarmIdentificationNumber;
    /**
     * 报警编号
     */
    private String alarmId;
    /**
     * 信息类型
     */
    private int infoType;
    /**
     * 附件信息
     */
    private List<SuAttachFileInfo> attachFiles;

    public static SuAlarmAttachInfo fromBytes(byte[] msgBody) {
        if (msgBody.length < 56) {
            log.warn("SuAlarmAttachInfo fromBytes data is too short!" );
            return null;
        }
        SuAlarmAttachInfo info = new SuAlarmAttachInfo();

        info.setTerminalId(ByteArrHelper.toHexString(ByteArrHelper.subByte(msgBody, 0, 7)));
        info.setAlarmIdentificationNumber(SuAlarmIdentificationNumber.fromBytes(ByteArrHelper.subByte(msgBody, 7, 23)));
        info.setAlarmId(new String(ByteArrHelper.subByte(msgBody, 23, 55), StandardCharsets.US_ASCII));
        info.setInfoType(msgBody[55]);
        info.setAttachFiles(new ArrayList<>());

        int pos = 57;

        while(pos < msgBody.length) {
            int fileNameLength = msgBody[pos];
            SuAttachFileInfo suAttachFileInfo = SuAttachFileInfo.fromBytes(ByteArrHelper.subByte(msgBody, pos, pos + fileNameLength + 5));
            pos += fileNameLength + 5;

            info.getAttachFiles().add(suAttachFileInfo);
        }

        return info;
    }
}
