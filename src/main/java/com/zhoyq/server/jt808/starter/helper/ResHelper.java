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

package com.zhoyq.server.jt808.starter.helper;

import com.zhoyq.server.jt808.starter.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author 刘路
 * @date 2018-06-22
 */
@Slf4j
public class ResHelper {

    /**
     * 0x8001 平台通用应答
     * @param phoneNum          sim卡号
     * @param terminalStreamNum 对应终端流水号
     * @param terminalMsgId     对应终端消息Id
     * @param by                应答参数 0 成功/确认 1 失败 2 消息有误 3 不支持 4 报警处理确认
     * @return   -  返回应答
     */
    public static byte[] getPlatAnswer(byte[] phoneNum,byte[] terminalStreamNum,byte[] terminalMsgId,byte by) {
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x80,0x01,0x00,0x05};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] d = terminalStreamNum;
        byte[] e = terminalMsgId;
        byte[] f = new byte[]{by};
        return ByteArrHelper.union(a, b, c, d, e, f);
    }
    /**
     * 0x8003 补传分包请求
     * @param phoneNum
     * @param originStreamNum    对应要求补传的原始消息第一包的流水号
     * @param num                重传包总数
     * @param idList             重传包id列表
     * @return
     */
    public static byte[] getPkgReq(byte[] phoneNum,byte[] originStreamNum,byte num,byte[] idList){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = originStreamNum.length + 1 + idList.length;
        byte[] a = new byte[]{(byte) 0x80,0x03,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, originStreamNum, new byte[]{num}, idList);
    }
    /**
     * 0x8100 终端注册应答
     * @param phoneNum  电话号码
     * @param terminalStreamNum 终端对应信息流水
     * @param result 结果 0 成功 1 车辆已经被注册 2 数据库中无车辆 3 终端已被注册 4 数据库中无终端
     * @param auth 结果为0的时候的鉴权码
     * @return
     */
    public static byte[] getTerminalRegisterAnswer(byte[] phoneNum, byte[] terminalStreamNum, byte result, String auth) {
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int i = 0;
        byte[] e;
        // 如果应答为0 则后缀鉴权码 否则不携带鉴权码
        if(result == 0){
            try {
                e = ByteArrHelper.union(new byte[]{result},auth.getBytes("GBK"));
                i = auth.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e1) {
                log.warn(e1.getMessage());
                i = 0;
                e = new byte[]{2};
            }
        }else{
            e = new byte[]{result};
        }
        int bodyLength = 3 + i;
        byte[] a = new byte[]{(byte) 0x81,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] d = terminalStreamNum;
        return ByteArrHelper.union(a, b, c, d, e);
    }
    /**
     * 0x8103 设置终端参数
     * @param phoneNum
     * @param num
     * @param list
     * @return
     */
    public static byte[] setTerminalParameters(byte[] phoneNum, byte num, List<Parameter> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1;
        byte[] buf = new byte[]{num};
        for(Parameter p: list){
            buf = ByteArrHelper.union(buf, p.getParameterId());
            buf = ByteArrHelper.union(buf, new byte[]{p.getLength()});
            buf = ByteArrHelper.union(buf, p.getValue());
            bodyLength += (p.getLength()+5);
        }
        byte[] a = new byte[]{(byte) 0x81,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, buf);
    }
    /**
     * 0x8104 查询所有终端参数
     * @param phoneNum       电话号码
     * @return
     */
    public static byte[] searchTerminalParameters(byte[] phoneNum){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x81,0x04,0x00,0x00};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c);
    }
    /**
     * 0x8105 终端控制
     * @param phoneNum       电话号码
     * @param comm           命令字
     * @param value          参数
     * @return
     */
    public static byte[] terminalControll(byte[] phoneNum,byte comm,byte[] value){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 0;
        byte[] answer = null;
        if(comm==1||comm==2){
            bodyLength = 1 + value.length;
            answer = ByteArrHelper.union(new byte[]{comm}, value);
        }else{
            bodyLength = 1;
            answer = new byte[]{comm};
        }
        byte[] a = new byte[]{(byte) 0x81,0x05,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, answer);
    }

    /**
     * 0x8106 查询指定终端参数
     * @param phoneNum      电话号码
     * @param num           参数总数
     * @param parameters    参数id列表
     * @return
     */
    public static byte[] searchSpecifyTerminalParameters(byte[] phoneNum,byte num,byte[] parameters){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + parameters.length;
        byte[] a = new byte[]{(byte) 0x81,0x06,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] d = ByteArrHelper.union(new byte[]{num}, parameters);
        return ByteArrHelper.union(a, b, c, d);
    }
    /**
     * 0x8107 查询终端属性
     * @param phoneNum
     * @return
     */
    public static byte[] searchTerminalProps(byte[] phoneNum){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x81,0x07,0x00,0x00};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c);
    }
    /**
     * 0x8108 下发终端升级包
     * @param phoneNum
     * @param tup
     * @return
     */
    public static byte[] sentTerminalUpdatePkg(byte[] phoneNum ,TerminalUpdatePkg tup){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 11 + tup.getVersionLength()+tup.getData().length;
        byte[] a = new byte[]{(byte) 0x81,0x08,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(
                new byte[]{tup.getUpdateType()},
                tup.getProducerId(),
                new byte[]{tup.getVersionLength()},
                tup.getVersion(),
                tup.getDataLength(),
                tup.getData());
        return ByteArrHelper.union( a, b, c, e);
    }
    /**
     * 0x8201 位置信息查询
     * @param phoneNum
     * @return
     */
    public static byte[] searchLocationInfo(byte[] phoneNum){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x82,0x01,0x00,0x00};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c);
    }
    /**
     * 0x8202 临时位置跟踪
     * @param phoneNum
     * @param space 时间间隔 s
     * @param date 有效期 s
     * @return
     */
    public static byte[] temporaryLocationTrace(byte[] phoneNum,byte[] space,byte[] date){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x82,0x02,0x00,0x06};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(space, date);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8203 人工确认报警消息
     * @param phoneNum
     * @param alarmStreamNum
     * @param alarmType
     * @return
     */
    public static byte[] makeSureAlarms(byte[] phoneNum,byte[] alarmStreamNum,byte[] alarmType){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x82,0x03,0x00,0x06};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(alarmStreamNum, alarmType);
        return ByteArrHelper.union( a, b, c, e);
    }
    /**
     * 0x8300 文本信息下发
     * @param phoneNum
     * @param sign 标识位
     * @param text 文本 最长1024字节 需自己控制长度
     * @return
     */
    public static byte[] sentTextInfo(byte[] phoneNum, byte sign, String text){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] str = new byte[]{};
        try {
            str = text.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        }
        int bodyLength = 1 + str.length;
        byte[] a = new byte[]{(byte) 0x83,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{sign}, str);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8301 事件设置
     * @param phoneNum
     * @param type          设置类型
     * @param num           事件总数
     * @param list          事件项列表
     * @return
     */
    public static byte[] setEvent(byte[] phoneNum, byte type, byte num, List<Event> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 2 ;
        byte[] buf = new byte[]{};
        for(Event e:list){
            byte[] buff = ByteArrHelper.union(new byte[]{e.getId(),e.getLength()}, e.getContent());
            buf = ByteArrHelper.union(buf, buff);
            bodyLength = bodyLength +(2+e.getLength());
        }
        byte[] a = new byte[]{(byte) 0x83,0x01,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{type, num},buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8302 提问下发
     * @param phoneNum
     * @param sign          标识
     * @param length        问题内容长度
     * @param question      问题
     * @param list          候选答案列表
     * @return
     */
    public static byte[] sentQuestion(byte[] phoneNum, byte sign, byte length, String question, List<CandidateAnswer> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] ques = new byte[]{};
        try {
            ques = question.getBytes("GBK");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        int bodyLength = 2 + ques.length;
        byte[] buf = new byte[]{};
        for(CandidateAnswer c:list){
            byte[] buff = ByteArrHelper.union(new byte[]{c.getId()},c.getLength(),c.getContent());
            buf = ByteArrHelper.union(buf, buff);
            bodyLength = bodyLength +(3+ByteArrHelper.twobyte2int(c.getLength()));
        }
        byte[] a = new byte[]{(byte) 0x83,0x02,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{sign, length},ques,buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8303 信息点播菜单设置
     * @param phoneNum
     * @param type           设置类型
     * @param num            信息项总数
     * @param list           信息项列表
     * @return
     */
    public static byte[] setInfoOrderMenu(byte[] phoneNum, byte type, byte num, List<InfoForOrder> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 2 ;
        byte[] buf = new byte[]{};
        for(InfoForOrder i:list){
            byte[] buff = ByteArrHelper.union(new byte[]{i.getType()}, i.getLength(), i.getName());
            buf = ByteArrHelper.union(buf, buff);
            bodyLength = bodyLength +( 3 + ByteArrHelper.twobyte2int(i.getLength()) );
        }
        byte[] a = new byte[]{(byte) 0x83,0x03,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{type, num},buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8304 信息服务
     * @param phoneNum
     * @param type          信息类型
     * @param length        信息长度
     * @param content       信息内容
     * @return
     */
    public static byte[] InfoService(byte[] phoneNum, byte type, byte[] length, String content){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] str = new byte[]{};
        try {
            str = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int bodyLength = 3 + ByteArrHelper.twobyte2int(length);
        byte[] a = new byte[]{(byte) 0x83,0x04,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{type}, length, str);
        return ByteArrHelper.union( a, b, c, e);
    }
    /**
     * 0x8400 电话回拨
     * @param phoneNum
     * @param sign            标识
     * @param tel             电话号码
     * @return
     */
    public static byte[] telephoneCallBack(byte[] phoneNum, byte sign, String tel){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] str = new byte[]{};
        try {
            str = tel.getBytes("GBK");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        int bodyLength = 1 + str.length;
        byte[] a = new byte[]{(byte) 0x84,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{sign}, str);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8401 设置电话本
     * @param phoneNum
     * @param type          设置类型
     * @param num           联系人数量
     * @param list          联系人项
     * @return
     */
    public static byte[] setTelBook(byte[] phoneNum, byte type, byte num, List<Contact> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 2;
        byte[] buf = new byte[]{};
        for(Contact c:list){
            byte[] buf1 = ByteArrHelper.union(new byte[]{c.getSign(),c.getPhoneNumLength()}, c.getPhoneNum());
            byte[] buf2 = ByteArrHelper.union(buf1, new byte[]{c.getNameLength()});
            byte[] buf3 = ByteArrHelper.union(buf2, c.getName());
            buf = ByteArrHelper.union(buf, buf3);
            bodyLength = bodyLength + (3 + c.getPhoneNumLength()+c.getNameLength());
        }
        byte[] a = new byte[]{(byte) 0x84,0x01,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{type,num},buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8500 车辆控制
     * @param phoneNum
     * @param controllSign   控制标识
     * @return
     */
    public static byte[] vehicleControll(byte[] phoneNum,byte controllSign){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x85,0x00,0x00,0x01};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, new byte[]{controllSign});
    }
    /**
     * 0x8600 设置圆形区域
     * @param phoneNum
     * @param prop            设置属性
     * @param num             区域总数
     * @param list            区域项
     * @return
     */
    public static byte[] setCircleArea(byte[] phoneNum, byte prop, byte num, List<CircleArea> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 2;
        byte[] buf = new byte[]{};
        for(CircleArea c:list){
            buf = ByteArrHelper.union(buf,c.getId(), c.getProp(),c.getLat(),c.getLon(),c.getRadius(),c.getBeginTime(),c.getEndTime(),c.getHighestSpeed(),new byte[]{c.getOverSpeedTime()});
            bodyLength = bodyLength + 33;
        }
        byte[] a = new byte[]{(byte) 0x86,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{prop,num},buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8601 删除圆形区域
     * @param phoneNum
     * @param areaNum        区域数
     * @param areaIds        区域id列表
     * @return
     */
    public static byte[] delCircleArea(byte[] phoneNum,byte areaNum,byte[] areaIds){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + areaIds.length;
        byte[] a = new byte[]{(byte) 0x86,0x01,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{areaNum}, areaIds);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8602 设置矩形区域
     * @param phoneNum
     * @param prop
     * @param num
     * @param list
     * @return
     */
    public static byte[] setRectangleArea(byte[] phoneNum, byte prop, byte num, List<RectangleArea> list){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 2;
        byte[] buf = new byte[]{};
        for(RectangleArea r:list){
            buf = ByteArrHelper.union( buf,r.getId(), r.getProp(),r.getLeftUpLat(),r.getLeftUpLon(),r.getRightDownLat(),r.getRightDownLon(),r.getBeginTime(),r.getEndTime(),r.getHighestSpeed(),new byte[]{r.getOverSpeedTime()});
            bodyLength = bodyLength + 37;
        }
        byte[] a = new byte[]{(byte) 0x86,0x02,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{prop,num},buf);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8603 删除矩形区域
     * @param phoneNum
     * @param num
     * @param areaIds
     * @return
     */
    public static byte[] delRectangleArea(byte[] phoneNum,byte num,byte[] areaIds){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + areaIds.length;
        byte[] a = new byte[]{(byte) 0x86,0x03,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{num}, areaIds);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8604 设置多边形区域
     * @param phoneNum
     * @param p
     * @return
     */
    public static byte[] setPolygonArea(byte[] phoneNum,PolygonArea p){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 23 + ByteArrHelper.twobyte2int(p.getPointNum())*8;
        byte[] buf = ByteArrHelper.union(p.getId(), p.getProp(),p.getBeginTime(),p.getEndTime(),p.getHighestSpeed(),new byte[]{p.getOverSpeedTime()}, p.getPointNum());
        for(Point po:p.getList()){
            buf = ByteArrHelper.union(buf,po.getLat(), po.getLon());
        }
        byte[] a = new byte[]{(byte) 0x86,0x04,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, buf);
    }
    /**
     * 0x8605 删除多边形区域
     * @param phoneNum
     * @param num
     * @param areaIds
     * @return
     */
    public static byte[] delPolygonArea(byte[] phoneNum,byte num,byte[] areaIds){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + areaIds.length;
        byte[] a = new byte[]{(byte) 0x86,0x05,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{num}, areaIds);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8606 设置路线
     * @param phoneNum
     * @param route
     * @return
     */
    public static byte[] setRoute(byte[] phoneNum,Route route){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 20 + ByteArrHelper.twobyte2int(route.getPointNum())*25;
        byte[] buf = ByteArrHelper.union(route.getId(), route.getProp(),route.getBeginTime(),route.getEndTime(),route.getPointNum());
        for(TurnPoint tp : route.getList()){
            buf = ByteArrHelper.union(buf, tp.getId(),tp.getRouteId(),tp.getLat(),tp.getLon(), new byte[]{tp.getWidth(),
                    tp.getProp()},tp.getDriveOverValue(),tp.getDriveLowerValue(),tp.getHighestSpeed(), new byte[]{tp.getOverSpeedTime()});
        }
        byte[] a = new byte[]{(byte) 0x86,0x06,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c, buf);
    }
    /**
     * 0x8607 删除路线
     * @param phoneNum
     * @param num
     * @param areaIds
     * @return
     */
    public static byte[] delRoute(byte[] phoneNum,byte num,byte[] areaIds){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + areaIds.length;
        byte[] a = new byte[]{(byte) 0x86,0x07,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{num}, areaIds);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8700 行驶记录采集命令
     * @param phoneNum
     * @param comm           命令字
     * @param data           GB/T 19056 相关规定
     * @return
     */
    public static byte[] getDriveHistory(byte[] phoneNum,byte comm,byte[] data){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + data.length;
        byte[] a = new byte[]{(byte) 0x87,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{comm}, data);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8701 行驶记录参数下传
     * @param phoneNum
     * @param comm
     * @param data
     * @return
     */
    public static byte[] sentDriveHistory(byte[] phoneNum,byte comm,byte[] data){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + data.length;
        byte[] a = new byte[]{(byte) 0x87,0x01,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{comm}, data);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8702 驾驶员身份信息上报
     * @param phoneNum
     * @return
     */
    public static byte[] driverInfoUpload(byte[] phoneNum){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x87,0x02,0x00,0x00};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        return ByteArrHelper.union(a, b, c);
    }
    /**
     * 0x8800 多媒体上传应答
     * @param phoneNum
     * @param mediaId
     * @param pkgNum
     * @param pkgIds
     * @return
     */
    public static byte[] mediaUploadAnswer(byte[] phoneNum,byte[] mediaId,byte pkgNum,byte[] pkgIds){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 5 + pkgNum*2;
        byte[] a = new byte[]{(byte) 0x88,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(mediaId, new byte[]{pkgNum}, pkgIds);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8801 摄像头立即拍摄
     * @param phoneNum
     * @param info
     * @return
     */
    public static byte[] cameraTakePhotoRightNow(byte[] phoneNum, CameraInfo info){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 12;
        byte[] a = new byte[]{(byte) 0x88,0x01,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(
                                new byte[]{info.getId()},
                                info.getComm(),
                        info.getSpaceTime(),
                new byte[]{info.getSaveSign(),info.getResolution(),
                        info.getQuality(),info.getLuminance(),
                        info.getContrast(),info.getSaturation(),info.getTone()});
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8802 存储多媒体检索
     * @param phoneNum
     * @param s
     * @return
     */
    public static byte[] searchStoredMedia(byte[] phoneNum,SearchStoredMediaData s){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 15;
        byte[] a = new byte[]{(byte) 0x88,0x02,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{s.getType(),s.getRouteId(),s.getEventCode()}, s.getBeginTime(), s.getEndTime());
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8803 存储多媒体上传
     * @param phoneNum
     * @param s
     * @return
     */
    public static byte[] storedMediaDataUpload(byte[] phoneNum,StoredMediaDataUpload s){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 16;
        byte[] a = new byte[]{(byte) 0x88,0x03,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(
                                new byte[]{s.getType(),s.getRouteId(),s.getEventCode()},
                                s.getBeginTime(),
                        s.getEndTime(),
                new byte[]{s.getDelSign()});
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8804 录音开始命令
     * @param phoneNum
     * @param comm
     * @param recordTime
     * @param saveSign
     * @param audioSamplingRate
     * @return
     */
    public static byte[] recordStart(byte[] phoneNum,byte comm,byte[] recordTime,byte saveSign,byte audioSamplingRate){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x88,0x04,0x00,0x05};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{comm}, recordTime, new byte[]{saveSign,audioSamplingRate});
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8805 单条存储多媒体信息检索上传
     * @param phoneNum
     * @param id
     * @param delSign
     * @return
     */
    public static byte[] oneStoredMediaSearchAndUpload(byte[] phoneNum,byte[] id,byte delSign){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        byte[] a = new byte[]{(byte) 0x88,0x05,0x00,0x05};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(id, new byte[]{delSign});
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8900 数据下行透传
     * @param phoneNum
     * @param type
     * @param data
     * @return
     */
    public static byte[] sentData(byte[] phoneNum,byte type,byte[] data){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 1 + data.length;
        byte[] a = new byte[]{(byte) 0x89,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(new byte[]{type}, data);
        return ByteArrHelper.union(a, b, c, e);
    }
    /**
     * 0x8A00 平台RSA公钥
     * @param phoneNum
     * @param p1
     * @param p2
     * @return
     */
    public static byte[] platRsa(byte[] phoneNum,byte[] p1,byte[] p2){
        int platStreamNum = Jt808Helper.getPlatStreamNum();
        int bodyLength = 132;
        byte[] a = new byte[]{(byte) 0x8A,0x00,(byte) ((bodyLength>>>8)&0x03),(byte) (bodyLength&0xff)};
        byte[] b = phoneNum;
        byte[] c = new byte[]{(byte) ((platStreamNum>>>8)&0xff),(byte) (platStreamNum&0xff)};
        byte[] e = ByteArrHelper.union(p1, p2);
        byte[] r = ByteArrHelper.union(a, b, c, e);
        return r;
    }
}
