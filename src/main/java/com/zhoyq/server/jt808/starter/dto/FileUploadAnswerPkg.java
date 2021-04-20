package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Setter
@Getter
public class FileUploadAnswerPkg {

    /**
     * 偏移量
     */
    private int offset;
    /**
     * 数据长度
     */
    private int dataLength;

    public byte[] toBytes() {
        return ByteArrHelper.union(
                ByteArrHelper.int2fourbytes(offset),
                ByteArrHelper.int2fourbytes(dataLength)
        );
    }
}
