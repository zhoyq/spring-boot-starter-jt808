package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-12-20
 */
@Slf4j
@Getter
@Setter
public class PassengerFlowInfo {

    /**
     * 起始时间
     */
    private Date dateStart;
    /**
     * 结束时间
     */
    private Date dateStop;
    /**
     * 上车人数
     */
    private Integer numberIn;
    /**
     * 下车人数
     */
    private Integer numberOut;

    public static PassengerFlowInfo fromBytes(byte[] msgBody) {
        if (msgBody.length != 16) {
            return null;
        }

        PassengerFlowInfo info = new PassengerFlowInfo();
        info.setDateStart(Jt808Helper.bytes2date(ByteArrHelper.subByte(msgBody, 0, 6)));
        info.setDateStop(Jt808Helper.bytes2date(ByteArrHelper.subByte(msgBody, 6, 12)));
        info.setNumberIn(ByteArrHelper.twobyte2int(ByteArrHelper.subByte(msgBody, 12, 14)));
        info.setNumberOut(ByteArrHelper.twobyte2int(ByteArrHelper.subByte(msgBody, 14, 16)));
        return info;
    }
}
