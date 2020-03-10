/*
 *  Copyright (c) 2020. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2020.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2020. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.pack;

import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.entity.LocationInfo;
import com.zhoyq.server.jt808.starter.entity.MediaInfo;
import com.zhoyq.server.jt808.starter.helper.Analyzer;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.DataService;
import com.zhoyq.server.jt808.starter.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多媒体数据上传
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2018/7/31
 */
@Slf4j
@Jt808Pack(msgId = 0x0801)
public class Handler0x0801 implements PackHandler {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ThreadPoolExecutor tpe;
    @Override
    public byte[] handle( byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0801 多媒体数据上传 MediaInfoUpload");

        tpe.execute(() -> {
            String phone = ByteArrHelper.toHexString(phoneNum);
            // 多媒体信息
            byte[] mediaInfoData = ByteArrHelper.subByte(msgBody, 0, 8);
            // 位置信息
            byte[] locationData = ByteArrHelper.subByte(msgBody, 8, 36);
            // 多媒体数据包
            byte[] mediaData = ByteArrHelper.subByte(msgBody, 36);

            // 存储多媒体信息
            MediaInfo mediaInfo = Analyzer.analyzeMediaInfo(mediaInfoData);
            dataService.mediaInfo(phone, mediaInfo);

            // 存储定位数据
            LocationInfo locationInfo = Analyzer.analyzeLocation(locationData);
            dataService.terminalLocation(phone, locationInfo);

            // 存储多媒体数据包
            dataService.mediaPackage(phone, mediaData);
        });
        return ResHelper.getPlatAnswer(phoneNum,streamNum,msgId,(byte) 0);
    }
}
