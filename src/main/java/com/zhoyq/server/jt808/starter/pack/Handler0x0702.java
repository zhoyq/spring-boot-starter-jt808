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

package com.zhoyq.server.jt808.starter.pack;

import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.entity.DriverInfo;
import com.zhoyq.server.jt808.starter.helper.Analyzer;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 驾驶员身份信息采集上报
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2018/7/31
 */
@Slf4j
@Jt808Pack(msgId = 0x0702)
@AllArgsConstructor
public class Handler0x0702 implements PackHandler {
    private DataService dataService;
    private ThreadPoolExecutor tpe;
    private Analyzer analyzer;

    @Override
    public byte[] handle( byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0702 驾驶员身份信息采集上报 driver data collection report");

        String phone = ByteArrHelper.toHexString(phoneNum);

        DriverInfo driverInfo = analyzer.analyzeDriver(phoneNum, msgBody);
        // 如果等于空 则解析错误 直接返回失败应答
        if (driverInfo == null) {
            log.warn("{} data analyze failed!", phone);
            return ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x01);
        }
        tpe.execute(() -> dataService.driverInfo(phone, driverInfo));

        return ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
    }
}
