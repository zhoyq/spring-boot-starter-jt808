package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuAlarmFileInfo {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件类型
     * 0x00 图片
     * 0x01 音频
     * 0x02 视频
     * 0x03 文本
     * 0x04 其他
     */
    private int fileType;
    /**
     * 文件大小
     */
    private int fileLength;

    public static SuAlarmFileInfo fromBytes(byte[] data) {
        SuAlarmFileInfo ret = new SuAlarmFileInfo();
        int fileNameLength = data[0];

        try {
            ret.setFileName(Jt808Helper.toGBKString(ByteArrHelper.subByte(data, 1, 1 + fileNameLength)));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            return null;
        }

        ret.setFileType(data[1 + fileNameLength]);
        ret.setFileLength(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(data, 2 + fileNameLength)));
        return ret;
    }
}
