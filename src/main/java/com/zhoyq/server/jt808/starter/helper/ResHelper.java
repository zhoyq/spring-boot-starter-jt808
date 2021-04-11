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

package com.zhoyq.server.jt808.starter.helper;

import com.zhoyq.server.jt808.starter.dto.*;
import com.zhoyq.server.jt808.starter.dto.DataTransportInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhoyq
 * @date 2018-06-22
 */
@Slf4j
public class ResHelper {

    /**
     * 包装命令
     * @param msgId 消息ID
     * @param phoneNum 电话
     * @return 命令
     */
    private static byte[] warp (byte[] msgId, byte[] phoneNum) {
        int platStreamNum = PlatStreamHelper.getPlatStreamNum(phoneNum);
        if (phoneNum.length == 10) {
            // 2019
            return ByteArrHelper.union(
                    msgId,
                    new byte[]{0x40, 0x00, 0x01},
                    phoneNum,
                    new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)});
        } else if (phoneNum.length == 6) {
            // 2011 2013
            return ByteArrHelper.union(
                    msgId,
                    new byte[]{0x00, 0x00},
                    phoneNum,
                    new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)});
        } else {
            return null;
        }
    }

    /**
     * 包装命令
     * @param msgId 消息ID
     * @param phoneNum 电话
     * @param msgBody 消息体
     * @return 命令
     */
    private static byte[] warp (byte[] msgId, byte[] phoneNum, byte[] msgBody) {
        int bodyLen = msgBody.length;
        int platStreamNum = PlatStreamHelper.getPlatStreamNum(phoneNum);
        if (phoneNum.length == 10) {
            // 2019
            return ByteArrHelper.union(
                    msgId,
                    new byte[]{(byte)(((bodyLen>>>8) & 0x03) | 0x40),(byte) (bodyLen&0xff), 0x01},
                    phoneNum,
                    new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)},
                    msgBody);
        } else if (phoneNum.length == 6) {
            // 2011 2013
            return ByteArrHelper.union(
                    msgId,
                    new byte[]{(byte)((bodyLen>>>8) & 0x03),(byte) (bodyLen&0xff)},
                    phoneNum,
                    new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)},
                    msgBody);
        } else {
            return null;
        }
    }

    /**
     * 0x8001 平台通用应答
     * @param phoneNum          sim卡号
     * @param terminalStreamNum 对应终端流水号
     * @param terminalMsgId     对应终端消息Id
     * @param by                应答参数 0 成功/确认 1 失败 2 消息有误 3 不支持 4 报警处理确认
     * @return   -  返回应答
     */
    public static byte[] getPlatAnswer(byte[] phoneNum,byte[] terminalStreamNum,byte[] terminalMsgId,byte by) {
        return warp(
                new byte[]{(byte) 0x80,0x01},
                phoneNum,
                ByteArrHelper.union(terminalStreamNum, terminalMsgId, new byte[]{by}));
    }

    /**
     * 0x8003 补传分包请求
     * @param phoneNum           sim卡号
     * @param originStreamNum    对应要求补传的原始消息第一包的流水号
     * @param num                重传包总数
     * @param idList             重传包id列表
     * @return - 返回应答
     */
    public static byte[] getPkgReq(byte[] phoneNum,byte[] originStreamNum,byte num,byte[] idList){
        return warp(
                new byte[]{(byte) 0x80,0x03},
                phoneNum,
                ByteArrHelper.union(originStreamNum, new byte[]{num}, idList)
        );
    }

    /**
     * v2019 新增
     * 0x8004 查询服务器时间应答
     * @param phoneNum 卡号
     * @return 命令
     */
    public static byte[] queryServerDateTime(byte[] phoneNum) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String timeHex = sdf.format(new Date(System.currentTimeMillis()));
        byte[] timeBytes = ByteArrHelper.hexStr2bytes(timeHex);
        return warp(
                new byte[]{(byte)0x80, 0x04},
                phoneNum,
                timeBytes
        );
    }

    /**
     * 0x8100 终端注册应答
     * @param phoneNum  电话号码
     * @param terminalStreamNum 终端对应信息流水
     * @param result 结果 0 成功 1 车辆已经被注册 2 数据库中无车辆 3 终端已被注册 4 数据库中无终端
     * @param auth 结果为0的时候的鉴权码
     * @return - 返回应答
     */
    public static byte[] getTerminalRegisterAnswer(byte[] phoneNum, byte[] terminalStreamNum, byte result, String auth) {
        byte[] e;
        // 如果应答为0 则后缀鉴权码 否则不携带鉴权码
        if(result == 0){
            try {
                e = ByteArrHelper.union(new byte[]{result},auth.getBytes("GBK"));
            } catch (UnsupportedEncodingException e1) {
                log.warn(e1.getMessage());
                // 出现异常返回错误结果
                e = new byte[]{5};
            }
        }else{
            e = new byte[]{result};
        }
        return warp(
                new byte[]{(byte) 0x81,0x00},
                phoneNum,
                ByteArrHelper.union(terminalStreamNum, e)
        );
    }

    /**
     * 0x8103 设置终端参数
     * @param phoneNum SIM卡号
     * @param num 数量
     * @param list 列表
     * @return 命令
     */
    public static byte[] setTerminalParameters(byte[] phoneNum, byte num, List<TerminalParameter> list){
        byte[] buf = new byte[]{num};
        for(TerminalParameter p: list){
            buf = ByteArrHelper.union(buf, p.toBytes());
        }
        return warp(
                new byte[]{(byte) 0x81,0x03},
                phoneNum,
                buf
        );
    }

    /**
     * ## 0x8103 设置终端参数
     *
     * **实例代码**
     *
     * ```java
     * // 查询终端心跳间隔
     * TerminalParameters params = new TerminalParameters();
     * params.setParameters(Collections.singletonList(TerminalParameterId.HeartbeatSplitTime));
     * // or params.addParameter(TerminalParameterId.HeartbeatSplitTime);
     * byte[] data = ResHelper.setTerminalParameters(phoneNum, params);
     * session.write(phone, data);
     * ```
     *
     * @param phoneNum SIM卡号
     * @param parameters 参数列表
     * @return 命令
     */
    public static byte[] setTerminalParameters(byte[] phoneNum, TerminalParameters parameters) {
        return warp(
                new byte[]{(byte) 0x81,0x03},
                phoneNum,
                parameters.toBytes()
        );
    }

    /**
     * 0x8104 查询所有终端参数
     * @param phoneNum       电话号码
     * @return - 命令
     */
    public static byte[] searchTerminalParameters(byte[] phoneNum){
        return warp(
                new byte[]{(byte) 0x81,0x04},
                phoneNum
        );
    }

    /**
     * 0x8105 终端控制
     * @param phoneNum       电话号码
     * @param comm           命令字
     * @param value          参数
     * @return 命令
     */
    public static byte[] terminalControll(byte[] phoneNum,byte comm,byte[] value){
        byte[] answer;
        if(comm==1||comm==2){
            answer = ByteArrHelper.union(new byte[]{comm}, value);
        }else{
            answer = new byte[]{comm};
        }
        return warp(
                new byte[]{(byte) 0x81,0x05},
                phoneNum,
                answer
        );
    }

    /**
     * 0x8106 查询指定终端参数
     * @param phoneNum      电话号码
     * @param num           参数总数
     * @param parameters    参数id列表
     * @return 命令
     */
    public static byte[] searchSpecifyTerminalParameters(byte[] phoneNum,byte num,byte[] parameters){
        return warp(
                new byte[]{(byte) 0x81,0x06},
                phoneNum,
                ByteArrHelper.union(new byte[]{num}, parameters)
        );
    }

    /**
     * 0x8107 查询终端属性
     * @param phoneNum 电话号码
     * @return 命令
     */
    public static byte[] searchTerminalProps(byte[] phoneNum){
        return warp(
                new byte[]{(byte) 0x81,0x07},
                phoneNum
        );
    }

    /**
     * 0x8108 下发终端升级包
     * @param phoneNum 电话号码
     * @param tup 更新包
     * @return 命令
     */
    public static byte[] sentTerminalUpdatePkg(byte[] phoneNum ,TerminalUpdatePkg tup){
        return warp(
                new byte[]{(byte) 0x81,0x08},
                phoneNum,
                ByteArrHelper.union(
                        new byte[]{tup.getUpdateType()},
                        tup.getProducerId(),
                        new byte[]{tup.getVersionLength()},
                        tup.getVersion(),
                        tup.getDataLength(),
                        tup.getData())
        );
    }

    /**
     * 0x8201 位置信息查询
     * @param phoneNum 电话号码
     * @return 命令
     */
    public static byte[] searchLocationInfo(byte[] phoneNum){
        return warp(
                new byte[]{(byte) 0x82,0x01},
                phoneNum
        );
    }

    /**
     * 0x8202 临时位置跟踪
     * @param phoneNum 电话号码
     * @param space 时间间隔 s
     * @param date 有效期 s
     * @return 命令
     */
    public static byte[] temporaryLocationTrace(byte[] phoneNum,byte[] space,byte[] date){
        return warp(
                new byte[]{(byte) 0x82,0x02},
                phoneNum,
                ByteArrHelper.union(space, date)
        );
    }

    /**
     * 0x8203 人工确认报警消息
     * @param phoneNum 电话号码
     * @param alarmStreamNum 报警流水
     * @param alarmType 报警类型
     * @return 命令
     */
    public static byte[] makeSureAlarms(byte[] phoneNum,byte[] alarmStreamNum,byte[] alarmType){
        return warp(
                new byte[]{(byte) 0x82,0x03},
                phoneNum,
                ByteArrHelper.union(alarmStreamNum, alarmType)
        );
    }

    /**
     * 0x8300 文本信息下发
     * 注意：由于2019版本增加了一个文本类型，所以在下发时会根据 phoneNum 长度判断版本封装 消息体
     * @param phoneNum 电话号码
     * @param sign 标识位
     * @param type 文本类型
     * @param text 文本信息 最长1024字节 需自己控制长度
     * @return 命令
     */
    public static byte[] sentTextInfo(byte[] phoneNum, byte sign, byte type, String text){
        byte[] str;
        try {
            str = text.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            str = new byte[]{};
        }
        return warp(
                new byte[]{(byte) 0x83, 0x00},
                phoneNum,
                phoneNum.length == 10 ? ByteArrHelper.union(new byte[]{sign, type}, str) : ByteArrHelper.union(new byte[]{sign}, str)
        );
    }

    /**
     * 0x8301 事件设置
     * @param phoneNum      电话号码
     * @param type          设置类型
     * @param num           事件总数
     * @param list          事件项列表
     * @return 命令
     */
    public static byte[] setEvent(byte[] phoneNum, byte type, byte num, List<Event> list){
        byte[] buf = new byte[]{};
        for(Event e:list){
            byte[] buff = ByteArrHelper.union(new byte[]{e.getId(),e.getLength()}, e.getContent());
            buf = ByteArrHelper.union(buf, buff);
        }
        return warp(
                new byte[]{(byte) 0x83,0x01},
                phoneNum,
                ByteArrHelper.union(new byte[]{type, num}, buf)
        );
    }

    /**
     * 0x8302 提问下发
     * @param phoneNum      电话号码
     * @param sign          标识
     * @param length        问题内容长度
     * @param question      问题
     * @param list          候选答案列表
     * @return 命令
     */
    public static byte[] sentQuestion(byte[] phoneNum, byte sign, byte length, String question, List<CandidateAnswer> list){
        byte[] ques;
        try {
            ques = question.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            ques = new byte[]{};
        }
        byte[] buf = new byte[]{};
        for(CandidateAnswer c:list){
            byte[] buff = ByteArrHelper.union(new byte[]{c.getId()},c.getLength(),c.getContent());
            buf = ByteArrHelper.union(buf, buff);
        }
        return warp(
                new byte[]{(byte) 0x83, 0x02},
                phoneNum,
                ByteArrHelper.union(new byte[]{sign, length}, ques, buf)
        );
    }

    /**
     * 0x8303 信息点播菜单设置
     * @param phoneNum       电话号码
     * @param type           设置类型
     * @param num            信息项总数
     * @param list           信息项列表
     * @return 命令
     */
    public static byte[] setInfoOrderMenu(byte[] phoneNum, byte type, byte num, List<InfoForOrder> list){
        byte[] buf = new byte[]{};
        for(InfoForOrder i:list){
            byte[] buff = ByteArrHelper.union(new byte[]{i.getType()}, i.getLength(), i.getName());
            buf = ByteArrHelper.union(buf, buff);
        }
        return warp(
                new byte[]{(byte) 0x83,0x03},
                phoneNum,
                ByteArrHelper.union(new byte[]{type, num}, buf)
        );
    }

    /**
     * 0x8304 信息服务
     * @param phoneNum      电话号码
     * @param type          信息类型
     * @param length        信息长度
     * @param content       信息内容
     * @return 命令
     */
    public static byte[] InfoService(byte[] phoneNum, byte type, byte[] length, String content){
        byte[] str;
        try {
            str = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            str = new byte[]{};
        }
        return warp(
                new byte[]{(byte) 0x83,0x04},
                phoneNum,
                ByteArrHelper.union(new byte[]{type}, length, str)
        );
    }

    /**
     * 0x8400 电话回拨
     * @param phoneNum        卡号
     * @param sign            标识
     * @param tel             回拨电话
     * @return 命令
     */
    public static byte[] telephoneCallBack(byte[] phoneNum, byte sign, String tel){
        byte[] str;
        try {
            str = tel.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            str = new byte[]{};
        }
        return warp(
                new byte[]{(byte) 0x84,0x00},
                phoneNum,
                ByteArrHelper.union(new byte[]{sign}, str)
        );
    }

    /**
     * 0x8401 设置电话本
     * @param phoneNum      卡号
     * @param type          设置类型
     * @param num           联系人数量
     * @param list          联系人项
     * @return 命令
     */
    public static byte[] setTelBook(byte[] phoneNum, byte type, byte num, List<Contact> list){
        byte[] buf = new byte[]{};
        for(Contact c:list){
            byte[] buf1 = ByteArrHelper.union(new byte[]{c.getSign(),c.getPhoneNumLength()}, c.getPhoneNum());
            byte[] buf2 = ByteArrHelper.union(buf1, new byte[]{c.getNameLength()});
            byte[] buf3 = ByteArrHelper.union(buf2, c.getName());
            buf = ByteArrHelper.union(buf, buf3);
        }
        return warp(
                new byte[]{(byte) 0x84,0x01},
                phoneNum,
                ByteArrHelper.union(new byte[]{type, num}, buf)
        );
    }

    /**
     * 0x8500 车辆控制
     * 注意：2019版本 增加参数 会按照号码长度判断发送
     * @param phoneNum       卡号
     * @param controlTypeNum   控制类型数量 2019版本
     * @param controlType   控制类型 2019版本
     * @param controlSign   控制标识 2011、2013版本
     * @return 命令
     */
    public static byte[] vehicleControl(byte[] phoneNum,int controlTypeNum, byte[] controlType, byte controlSign){
        byte[] controlTypeNumber = ByteArrHelper.int2twobytes(controlTypeNum);
        return warp(
                new byte[]{(byte) 0x85,0x00},
                phoneNum,
                phoneNum.length == 10 ? ByteArrHelper.union(controlTypeNumber, controlType) : new byte[]{controlSign}
        );
    }

    /**
     * 0x8600 设置圆形区域
     * @param phoneNum        卡号
     * @param prop            设置属性
     * @param num             区域总数
     * @param list            区域项
     * @return 命令
     */
    public static byte[] setCircleArea(byte[] phoneNum, byte prop, byte num, List<CircleArea> list){
        byte[] buf = new byte[]{};
        for(CircleArea c:list){
            buf = ByteArrHelper.union(buf,c.getId(), c.getProp(),c.getLat(),c.getLon(),c.getRadius(),c.getBeginTime(),c.getEndTime(),c.getHighestSpeed(),new byte[]{c.getOverSpeedTime()});
        }
        return warp(
                new byte[]{(byte) 0x86,0x00},
                phoneNum,
                ByteArrHelper.union(new byte[]{prop, num}, buf)
        );
    }

    /**
     * 0x8601 删除圆形区域
     * @param phoneNum       卡号
     * @param areaNum        区域数
     * @param areaIds        区域id列表
     * @return 命令
     */
    public static byte[] delCircleArea(byte[] phoneNum,byte areaNum,byte[] areaIds){
        return warp(
                new byte[]{(byte) 0x86,0x01},
                phoneNum,
                ByteArrHelper.union(new byte[]{areaNum}, areaIds)
        );
    }

    /**
     * 0x8602 设置矩形区域
     * @param phoneNum 卡号
     * @param prop 属性
     * @param num 数量
     * @param list 列表
     * @return 命令
     */
    public static byte[] setRectangleArea(byte[] phoneNum, byte prop, byte num, List<RectangleArea> list){
        byte[] buf = new byte[]{};
        for(RectangleArea r:list){
            buf = ByteArrHelper.union( buf,r.getId(), r.getProp(),r.getLeftUpLat(),r.getLeftUpLon(),r.getRightDownLat(),r.getRightDownLon(),r.getBeginTime(),r.getEndTime(),r.getHighestSpeed(),new byte[]{r.getOverSpeedTime()});
        }
        return warp(
                new byte[]{(byte) 0x86,0x02},
                phoneNum,
                ByteArrHelper.union(new byte[]{prop, num}, buf)
        );
    }

    /**
     * 0x8603 删除矩形区域
     * @param phoneNum 卡号
     * @param num 数量
     * @param areaIds 区域ID
     * @return 命令
     */
    public static byte[] delRectangleArea(byte[] phoneNum,byte num,byte[] areaIds){
        return warp(
                new byte[]{(byte) 0x86,0x03},
                phoneNum,
                ByteArrHelper.union(new byte[]{num}, areaIds)
        );
    }

    /**
     * 0x8604 设置多边形区域
     * @param phoneNum 卡号
     * @param p 多边形区域
     * @return 命令
     */
    public static byte[] setPolygonArea(byte[] phoneNum,PolygonArea p){
        byte[] buf = ByteArrHelper.union(p.getId(), p.getProp(),p.getBeginTime(),p.getEndTime(),p.getHighestSpeed(),new byte[]{p.getOverSpeedTime()}, p.getPointNum());
        for(Point po:p.getList()){
            buf = ByteArrHelper.union(buf,po.getLat(), po.getLon());
        }
        return warp(
                new byte[]{(byte) 0x86,0x04},
                phoneNum,
                buf
        );
    }

    /**
     * 0x8605 删除多边形区域
     * @param phoneNum 卡号
     * @param num 数量
     * @param areaIds 区域ID
     * @return 命令
     */
    public static byte[] delPolygonArea(byte[] phoneNum,byte num,byte[] areaIds){
        return warp(
                new byte[]{(byte) 0x86,0x05},
                phoneNum,
                ByteArrHelper.union(new byte[]{num}, areaIds)
        );
    }

    /**
     * 0x8606 设置路线
     * @param phoneNum 卡号
     * @param route 线路
     * @return 命令
     */
    public static byte[] setRoute(byte[] phoneNum,Route route){
        byte[] buf = ByteArrHelper.union(route.getId(), route.getProp(),route.getBeginTime(),route.getEndTime(),route.getPointNum());
        for(TurnPoint tp : route.getList()){
            buf = ByteArrHelper.union(buf, tp.getId(),tp.getRouteId(),tp.getLat(),tp.getLon(), new byte[]{tp.getWidth(),
                    tp.getProp()},tp.getDriveOverValue(),tp.getDriveLowerValue(),tp.getHighestSpeed(), new byte[]{tp.getOverSpeedTime()});
        }
        return warp(
                new byte[]{(byte) 0x86,0x06},
                phoneNum,
                buf
        );
    }

    /**
     * 0x8607 删除路线
     * @param phoneNum 卡号
     * @param num 数量
     * @param areaIds 区域ID
     * @return 命令
     */
    public static byte[] delRoute(byte[] phoneNum,byte num,byte[] areaIds){
        return warp(
                new byte[]{(byte) 0x86,0x07},
                phoneNum,
                ByteArrHelper.union(new byte[]{num}, areaIds)
        );
    }

    /**
     * v2019
     * 0x8608 查询区域或者线路数据
     * @param phoneNum 卡号
     * @param searchType 查询类型
     * @param searchNum 查询数量
     * @param searchIds 查询ID列表
     * @return 命令
     */
    public static byte[] searchAreaOrRoute(byte[] phoneNum,byte searchType,byte[] searchNum,byte[] searchIds){
        return warp(
                new byte[]{(byte) 0x86,0x08},
                phoneNum,
                ByteArrHelper.union(new byte[]{searchType}, searchNum, searchIds)
        );
    }

    /**
     * 0x8700 行驶记录采集命令
     * @param phoneNum       电话号码
     * @param comm           命令字
     * @param data           GB/T 19056 相关规定
     * @return 命令
     */
    public static byte[] getDriveHistory(byte[] phoneNum,byte comm,byte[] data){
        return warp(
                new byte[]{(byte) 0x87,0x00},
                phoneNum,
                ByteArrHelper.union(new byte[]{comm}, data)
        );
    }

    /**
     * 0x8701 行驶记录参数下传
     * @param phoneNum 卡号
     * @param comm 命令
     * @param data 数据
     * @return 命令
     */
    public static byte[] sentDriveHistory(byte[] phoneNum,byte comm,byte[] data){
        return warp(
                new byte[]{(byte) 0x87,0x01},
                phoneNum,
                ByteArrHelper.union(new byte[]{comm}, data)
        );
    }

    /**
     * 0x8702 驾驶员身份信息上报
     * @param phoneNum 卡号
     * @return 命令
     */
    public static byte[] driverInfoUpload(byte[] phoneNum){
        return warp(
                new byte[]{(byte) 0x87,0x02},
                phoneNum
        );
    }

    /**
     * 0x8800 多媒体上传应答
     * @param phoneNum 卡号
     * @param mediaId 多媒体ID
     * @param pkgNum 包数量
     * @param pkgIds 包ID
     * @return 命令
     */
    public static byte[] mediaUploadAnswer(byte[] phoneNum,byte[] mediaId,byte pkgNum,byte[] pkgIds){
        return warp(
                new byte[]{(byte) 0x88,0x00},
                phoneNum,
                ByteArrHelper.union(mediaId, new byte[]{pkgNum}, pkgIds)
        );
    }

    /**
     * 0x8801 摄像头立即拍摄
     * @param phoneNum 卡号
     * @param info 信息
     * @return 命令
     */
    public static byte[] cameraTakePhotoRightNow(byte[] phoneNum, CameraInfo info){
        return warp(
                new byte[]{(byte) 0x88,0x01},
                phoneNum,
                ByteArrHelper.union(
                        new byte[]{info.getId()},
                        info.getComm(),
                        info.getSpaceTime(),
                        new byte[]{info.getSaveSign(),info.getResolution(),
                                info.getQuality(),info.getLuminance(),
                                info.getContrast(),info.getSaturation(),info.getTone()})
        );
    }

    /**
     * 0x8802 存储多媒体检索
     * @param phoneNum 卡号
     * @param s 搜索信息
     * @return 命令
     */
    public static byte[] searchStoredMedia(byte[] phoneNum,SearchStoredMediaData s){
        return warp(
                new byte[]{(byte) 0x88,0x02},
                phoneNum,
                ByteArrHelper.union(new byte[]{s.getType(),s.getRouteId(),s.getEventCode()}, s.getBeginTime(), s.getEndTime())
        );
    }

    /**
     * 0x8803 存储多媒体上传
     * @param phoneNum 卡号
     * @param s 存储多媒体数据
     * @return 命令
     */
    public static byte[] storedMediaDataUpload(byte[] phoneNum,StoredMediaDataUpload s){
        return warp(
                new byte[]{(byte) 0x88,0x03},
                phoneNum,
                ByteArrHelper.union(
                        new byte[]{s.getType(),s.getRouteId(),s.getEventCode()},
                        s.getBeginTime(),
                        s.getEndTime(),
                        new byte[]{s.getDelSign()})
        );
    }

    /**
     * 0x8804 录音开始命令
     * @param phoneNum 卡号
     * @param comm 命令
     * @param recordTime 记录事件
     * @param saveSign 保存标识
     * @param audioSamplingRate 采样率
     * @return 命令
     */
    public static byte[] recordStart(byte[] phoneNum,byte comm,byte[] recordTime,byte saveSign,byte audioSamplingRate){
        return warp(
                new byte[]{(byte) 0x88,0x04},
                phoneNum,
                ByteArrHelper.union(new byte[]{comm}, recordTime, new byte[]{saveSign,audioSamplingRate})
        );
    }

    /**
     * 0x8805 单条存储多媒体信息检索上传
     * @param phoneNum 卡号
     * @param id 唯一标识
     * @param delSign 删除标识
     * @return 命令
     */
    public static byte[] oneStoredMediaSearchAndUpload(byte[] phoneNum,byte[] id,byte delSign){
        return warp(
                new byte[]{(byte) 0x88,0x05},
                phoneNum,
                ByteArrHelper.union(id, new byte[]{delSign})
        );
    }

    /**
     * 0x8900 数据下行透传
     * @param phoneNum 卡号
     * @param type 类型
     * @param data 数据
     * @return 命令
     */
    public static byte[] sentData(byte[] phoneNum, byte type, byte[] data){
        return warp(
                new byte[]{(byte) 0x89,0x00},
                phoneNum,
                ByteArrHelper.union(new byte[]{type}, data)
        );
    }

    /**
     * ## 0x8900 数据下行透传
     *
     * **实例代码**
     *
     * ```java
     * // 下发 苏标 查询驾驶辅助设备状态信息
     * DataTransportInfo info = new DataTransportInfo();
     * info.setType(DataTransportType.SU_STATUS);
     * info.setSuData(Collections.singletonList(DataTransportDeviceId.DRIVING_ASSISTANCE));
     * byte[] data = ResHelper.sentData(phoneNum, info);
     * // session 如何获取请查看项目 readme
     * session.write(sim, data);
     * ```
     *
     * @param phoneNum 卡号
     * @param data 数据 {@link DataTransportInfo}
     * @return 命令
     */
    public static byte[] sentData(byte[] phoneNum, DataTransportInfo data){
        return warp(
                new byte[]{(byte) 0x89,0x00},
                phoneNum,
                data.toBytes()
        );
    }

    /**
     * 0x8A00 平台RSA公钥
     * @param phoneNum 卡号
     * @param p1 rsa
     * @param p2 rsa
     * @return 命令
     */
    public static byte[] platRsa(byte[] phoneNum,byte[] p1,byte[] p2){
        return warp(
                new byte[]{(byte) 0x8A,0x00},
                phoneNum,
                ByteArrHelper.union(p1, p2)
        );
    }
}
