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

package com.zhoyq.server.jt808.starter.service;

import com.zhoyq.server.jt808.starter.dto.*;

import java.util.List;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
public interface DataService {
    /**
     * 保存终端上传的 RSA 公钥
     * 相关消息ID：0x0A00
     * @param sim 对应的12位电话号码 默认首位为0
     * @param e RSA 公钥 {e, n} 中的 e
     * @param n RSA 公钥 {e, n} 中的 n
     */
    void terminalRsa(String sim, byte[] e, byte[] n);

    /**
     * 终端心跳
     * 相关消息ID：0x0002
     * @param sim 卡号
     */
    void terminalHeartbeat(String sim);

    /**
     * 终端注销
     * 相关消息ID：0x0003
     * @param sim 卡号
     */
    void terminalCancel(String sim);

    /**
     * 终端注册
     * 相关消息ID：0x0100
     * @param sim 卡号
     * @param province 省域ID
     * @param city 市县域ID
     * @param manufacturer 制造商ID
     * @param deviceType 终端型号
     * @param deviceId 终端ID
     * @param licenseColor 车牌颜色
     * @param registerLicense 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]
     * @return 鉴权码 返回值有 0000001 车辆被注册 0000002 无车辆 0000003 终端被注册 0000004 无终端 或者 真正的鉴权码
     */
    String terminalRegister(String sim, int province, int city, String manufacturer, String deviceType,
                            String deviceId, byte licenseColor, String registerLicense);

    /**
     * 保存终端鉴权信息
     * 相关消息ID：0x0102
     * @param sim 卡号
     * @param authId 鉴权码
     * @param imei 2019版的 IMEI 号
     * @param softVersion 2019版的 软件版本号
     */
    void terminalAuth(String sim, String authId, String imei, String softVersion);

    /**
     * 终端应答消息
     * 应答类消息都会传入此方法进行处理
     * 相关消息ID：0x0001 0x0104 0x0107 0x0108 0x0302 0x0500 0x0608 0x0700 0x0802 0x0805
     * 相关苏标消息ID：0x1210
     * @param sim 卡号
     * @param platformStreamNumber 应答对应平台流水号
     * @param platformCommandId 应答对应平台消息ID
     * @param msgId 消息ID
     * @param msgBody 对应应答的消息体数据
     */
    void terminalAnswer(String sim, int platformStreamNumber, String platformCommandId, String msgId, byte[] msgBody);

    /**
     * 查询终端参数应答消息中的终端参数
     * 注意: 本方法会和 terminalAnswer 同时运行，terminalAnswer 主要负责记录，此方法更方便解析
     * 相关消息ID：0x0104
     * @param parameters 终端参数列表
     */
    void terminalParameters(String sim, TerminalParameters parameters);

    /**
     * 查询终端属性应答消息中的属性数据
     * 注意: 本方法会和 terminalAnswer 同时运行，terminalAnswer 主要负责记录，此方法更方便解析
     * 相关消息ID：0x0107
     * @param property 终端属性
     */
    void terminalProperty(String sim, TerminalProperty property);

    /**
     * 终端升级结果通知
     * 注意: 本方法会和 terminalAnswer 同时运行，terminalAnswer 主要负责记录，此方法更方便解析
     * 相关消息ID：0x0108
     * @param sim 卡号
     * @param type 升级类型
     * @param result 升级结果
     */
    void terminalUpdateResult(String sim, TerminalUpdatePkgType type, TerminalUpdateResult result);

    /**
     * 保存驾驶员信息
     * 相关消息ID：0x0702
     * @param sim 卡号
     * @param driverInfo 驾驶员信息
     */
    void driverInfo(String sim, DriverInfo driverInfo);

    /**
     * 保存定位信息、报警信息（包含持续和瞬间）、附加信息
     * 相关消息ID：0x0200（仅定位） 0x0201（仅定位） 0x0704（仅定位） 0x0801（定位和多媒体）
     * @param sim 卡号
     * @param locationInfo 定位信息 + 报警信息 + 附加信息 | 计算后的平台信息（可以放到计划任务延迟计算）
     * @param mediaId 连接多媒体数据的ID
     */
    void terminalLocation(String sim, LocationInfo locationInfo, Integer mediaId);

    /**
     * 事件上报
     * 相关消息ID：0x0301
     * @param sim 卡号
     * @param eventReportAnswerId 事件ID
     */
    void eventReport(String sim, byte eventReportAnswerId);

    /**
     * 提问应答
     * 注意: 本方法会和 terminalAnswer 同时运行，terminalAnswer 主要负责记录，此方法更方便解析
     * 相关消息ID：0x0302
     * @param sim 卡号
     * @param answerStreamNumber 应答流水号
     * @param answerId 答案ID
     */
    void questionAnswer(String sim, int answerStreamNumber, byte answerId);

    /**
     * 消息点播
     * 相关消息ID：0x0303
     * @param sim 卡号
     * @param type 订阅消息类型
     */
    void orderInfo(String sim, byte type);

    /**
     * 消息取消点播
     * 相关消息ID：0x0303
     * @param sim 卡号
     * @param type 取消订阅消息类型
     */
    void cancelOrderInfo(String sim, byte type);

    /**
     * 电子运单
     * 相关消息ID：0x0701
     * @param sim 卡号
     * @param data 电子运单数据包内容
     */
    void eBill(String sim, byte[] data);

    /**
     * 保存CAN总线数据
     * 相关消息ID：0x0705
     * @param sim 卡号
     * @param canDataInfo CAN 总线数据
     */
    void canData(String sim, CanDataInfo canDataInfo);

    /**
     * 保存多媒体信息
     * 相关消息ID：0x0800 0x0801
     * @param sim 卡号
     * @param mediaInfo 多媒体信息
     */
    void mediaInfo(String sim, MediaInfo mediaInfo);

    /**
     * 存储多媒体实体信息
     * 相关消息ID：0x0801
     * @param sim 卡号
     * @param mediaData 数据包
     * @param mediaId 连接多媒体数据的ID
     */
    void mediaPackage(String sim, byte[] mediaData, Integer mediaId);

    /**
     * 保存透传数据
     * 相关消息ID：0x0900
     * @param sim 卡号
     * @param dataTransportInfo 上行透传数据
     */
    void dataTransport(String sim, DataTransportInfo dataTransportInfo);

    /**
     * 保存压缩上报数据 已经解压缩
     * 相关消息ID：0x0901
     * @param sim 卡号
     * @param data 上报数据
     */
    void compressData(String sim, byte[] data);

    // ==========

    /**
     * 获取终端RSA
     * 无具体相关消息ID
     * @param sim 卡号
     * @return RSA bit0 - bit3 e, bit4 - bit 132 n
     */
    byte[] terminalRsa(String sim);

    /**
     * 查询数据库存储的所有卡号与鉴权码之间的关系
     * 无具体相关消息ID
     * @return 关系
     */
    List<SimAuthDto> simAuth();

    // ==========

    /**
     * 苏标：报警附件信息
     * 关联消息ID：0x1210
     * @param sim 卡号
     * @param alarmInfo 报警附件信息
     */
    void suAlarmAttachInfo(String sim, SuAlarmAttachInfo alarmInfo);

    /**
     * 苏标：报警文件信息
     * 关联消息ID：0x1211 0x1212
     * @param sim 卡号
     * @param suAlarmFileInfo 报警文件信息
     * @param data 文件数据
     */
    void suAlarmFileInfo(String sim, SuAlarmFileInfo suAlarmFileInfo, byte[] data);

    /**
     * 1078：终端上传音视频属性
     * 关联消息ID：0x1003
     * @param sim 卡号
     * @param prop 属性
     */
    void uploadAudioOrVideoProp(String sim, AudioOrVideoProp prop);

    /**
     * 1078: 终端上传乘客流量
     * @param sim 卡号
     * @param info 乘客流量信息
     */
    void uploadPassengerFlow(String sim, PassengerFlowInfo info);
}
