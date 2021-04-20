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
public class SuAttachFileInfo {

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件长度
     */
    private int fileLength;

    public static SuAttachFileInfo fromBytes(byte[] data) {
        SuAttachFileInfo file = new SuAttachFileInfo();

        try {
            file.setFileName(Jt808Helper.toGBKString(ByteArrHelper.subByte(data, 1, data.length - 4)));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            return null;
        }

        file.setFileLength(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(data, data.length - 4)));

        return file;
    }
}
