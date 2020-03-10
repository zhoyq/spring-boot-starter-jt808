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

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.service.SessionService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Jt808 协议处理静态帮助类
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/12/18
 */
@Slf4j
@Component
public class Jt808Helper {

    /* ===================================
       = 协议内一般工具
     * =================================== */

    /**
     * 平台流水号
     */
    private static int PLAT_STREAM_NUM = 1;
    public static int getPlatStreamNum(){
        return PLAT_STREAM_NUM;
    }
    public static void addPlatStreamNum(){
        PLAT_STREAM_NUM++;
    }
    private static int getPkgPlatStreamNum(int j){
        int buf = PLAT_STREAM_NUM;
        PLAT_STREAM_NUM = PLAT_STREAM_NUM+j;
        return buf;
    }

    /**
     * 获取消息体长度
     * @param bodyProp 消息体属性
     * @return 消息体长度
     */
    public static int getMsgBodyLength(byte[] bodyProp) {
        int length = 0;
        if( bodyProp.length == Const.NUMBER_2 ){
            int buf = 0x03ff;
            int body = ((bodyProp[0]<<8)&0xff00)^(bodyProp[1]&0x00ff);
            length = body&buf;
        }
        log.trace("body length is " + length);
        return length;
    }

    /**
     * 获取消息体中的流水号
     * @param b 获取消息体内的流水号
     * @return 流水号
     */
    public static int getStreamNumInMsgBody(byte[] b) {
        return ((b[0] << 8) & 0xff00) ^ (b[1] & 0x00ff);
    }

    /* ===================================
       = 封包的时候需要
     * =================================== */

    /**
     * 填充校验
     * @param bytes 需要填充校验的消息数据
     * @return 填充校验后的数据
     */
    public static byte[] addVerify(byte[] bytes) {
        byte verify = bytes[0];
        for(int i = 1;i<bytes.length;i++){
            verify = (byte) (verify^bytes[i]);
        }
        return ByteArrHelper.union(bytes, new byte[]{verify});
    }

    /**
     * 转义
     * @param b 转义之前的数据
     * @return 转义之后的数据
     */
    public static byte[] trans(byte[] b){
        for(int i =0;i<b.length-1;i++){
            if(b[i] == 0x7d ){
                b = ByteArrHelper.union(
                        ByteArrHelper.subByte(b, 0, i+1),
                        new byte[]{0x01},
                        ByteArrHelper.subByte(b, i+1)
                );
            }else if(b[i] == 0x7e){
                b = ByteArrHelper.union(
                        ByteArrHelper.subByte(b, 0, i),
                        new byte[]{0x7d,0x02},
                        ByteArrHelper.subByte(b, i+1)
                );
            }
        }
        b = ByteArrHelper.union(new byte[]{0x7e}, b);
        b = ByteArrHelper.union(b, new byte[]{0x7e});
        return b;
    }

    /* ===================================
       = 解包的时候需要
     * =================================== */

    /**
     * 验证校验
     * @param bytes 需要验证的数据
     * @return true 校验成功 false 校验失败
     */
    public static boolean verify(byte[] bytes) {
        boolean b = false;
        byte verify = bytes[0];
        for(int i = 1;i<bytes.length-1;i++){
            verify = (byte) (verify^bytes[i]);
        }
        if(verify==bytes[bytes.length-1]){
            b = true;
        }
        log.trace("verify code is " + ByteArrHelper.toHexString(verify) + " return " + b);
        return b;
    }

    /**
     * 还原
     * @param b 需要转义还原的数据
     * @return 转义还原后的数据
     */
    public static byte[] retrans(byte[] b){
        byte[] buf = ByteArrHelper.subByte(b, 1,b.length-1);
        for(int i =0;i<buf.length-1;i++){
            if(buf[i] == 0x7d && buf[i+1] == 0x01){
                buf = ByteArrHelper.union(ByteArrHelper.subByte(buf, 0, i+1), ByteArrHelper.subByte(buf, i+2));
            }else if(buf[i] == 0x7d && buf[i+1] == 0x02){
                buf = ByteArrHelper.union(
                        ByteArrHelper.subByte(buf, 0, i),
                        new byte[]{0x7e},
                        ByteArrHelper.subByte(buf, i+2)
                );
            }
        }
        return buf;
    }

    /**
     * 验证分包
     * @param bodyProp 消息体属性
     * @return 是否分包
     */
    public static boolean hasPackage(byte[] bodyProp) {
        if( bodyProp.length == Const.NUMBER_2 ){
            byte buf = (byte) (bodyProp[0] & 0x20);
            return buf!=0;
        }else{
            return false;
        }
    }

    /**
     * 分包发送
     * @param buf 需要发送的数据
     * @param session 会话对象
     */
    public static void sentByPkg(byte[] buf, IoSession session) {
        byte[] body = ByteArrHelper.subByte(buf, 12);
        int pkgCount = body.length%1023==0?body.length/1023:body.length/1023+1;
        byte[] msgId = ByteArrHelper.subByte(buf, 0, 2);
        byte[] phoneNum = ByteArrHelper.subByte(buf, 4, 10);
        int streamNum = getPkgPlatStreamNum(pkgCount);
        for(int i = 1;i<=pkgCount;i++){
            byte[] buff = new byte[]{};
            buff = ByteArrHelper.union(buff, msgId);
            // 最后一个
            if(pkgCount == i){
                byte[] bodybuf = ByteArrHelper.subByte(body, (i-1)*1023);
                byte[] bodyLength = new byte[]{(byte) ((bodybuf.length>>>8)&0xff),(byte) (bodybuf.length&0xff)};
                buff = ByteArrHelper.union(
                        buff,
                        new byte[]{(byte) (bodyLength[0]^0x20) ,bodyLength[1]},
                        phoneNum,
                        new byte[]{(byte) ((streamNum>>>8)&0xff),(byte) (streamNum&0xff)},
                        new byte[]{(byte) ((pkgCount>>>8)&0xff),(byte) (pkgCount&0xff)},
                        new byte[]{(byte) ((i>>>8)&0xff),(byte) (i&0xff)},
                        bodybuf
                );
            }else{
                byte[] bodybuf = ByteArrHelper.subByte(body, (i-1)*1023, i*1023);
                buff = ByteArrHelper.union(
                        buff,
                        new byte[]{0x23,(byte) 0xFF},
                        phoneNum,
                        new byte[]{(byte) ((streamNum>>>8)&0xff),(byte) (streamNum&0xff)},
                        new byte[]{(byte) ((pkgCount>>>8)&0xff),(byte) (pkgCount&0xff)},
                        new byte[]{(byte) ((i>>>8)&0xff),(byte) (i&0xff)},
                        bodybuf
                );
            }
            session.write(buff);
            streamNum ++;
        }
    }

    /**
     * 分包发送
     * @param buf 需要发送的数据
     * @param session 会话对象
     */
    public static void sentByPkg(byte[] buf, ChannelHandlerContext session) {
        byte[] body = ByteArrHelper.subByte(buf, 12);
        int pkgCount = body.length%1023==0?body.length/1023:body.length/1023+1;
        byte[] msgId = ByteArrHelper.subByte(buf, 0, 2);
        byte[] phoneNum = ByteArrHelper.subByte(buf, 4, 10);
        int streamNum = getPkgPlatStreamNum(pkgCount);
        for(int i = 1;i<=pkgCount;i++){
            byte[] buff = new byte[]{};
            buff = ByteArrHelper.union(buff, msgId);
            // 最后一个
            if(pkgCount == i){
                byte[] bodybuf = ByteArrHelper.subByte(body, (i-1)*1023);
                byte[] bodyLength = new byte[]{(byte) ((bodybuf.length>>>8)&0xff),(byte) (bodybuf.length&0xff)};
                buff = ByteArrHelper.union(
                        buff,
                        new byte[]{(byte) (bodyLength[0]^0x20) ,bodyLength[1]},
                        phoneNum,
                        new byte[]{(byte) ((streamNum>>>8)&0xff),(byte) (streamNum&0xff)},
                        new byte[]{(byte) ((pkgCount>>>8)&0xff),(byte) (pkgCount&0xff)},
                        new byte[]{(byte) ((i>>>8)&0xff),(byte) (i&0xff)},
                        bodybuf
                );
            }else{
                byte[] bodybuf = ByteArrHelper.subByte(body, (i-1)*1023, i*1023);
                buff = ByteArrHelper.union(
                        buff,
                        new byte[]{0x23,(byte) 0xFF},
                        phoneNum,
                        new byte[]{(byte) ((streamNum>>>8)&0xff),(byte) (streamNum&0xff)},
                        new byte[]{(byte) ((pkgCount>>>8)&0xff),(byte) (pkgCount&0xff)},
                        new byte[]{(byte) ((i>>>8)&0xff),(byte) (i&0xff)},
                        bodybuf
                );
            }
            session.writeAndFlush(buff);
            streamNum ++;
        }
    }

    public static byte[] allPkg(SessionService service, String phone, int totalPkgNum) {
        Map<Integer,byte[]> map = service.getPackages(phone);
        byte[] buf = ByteArrHelper.subByte(map.get(1), 0, map.get(1).length-1);
        for(int i = 2;i<=totalPkgNum;i++){
            byte[] pkg = map.get(i);
            buf = ByteArrHelper.union(buf, ByteArrHelper.subByte(pkg, 16, map.get(1).length-1));
        }
        map.clear();
        return buf;
    }

    public static boolean pkgAllReveived(SessionService service, String phone, int totalPkgNum) {
        return service.containsPackages(phone) && service.getPackages(phone).size() == totalPkgNum;
    }

//    /**
//     * 创建报警对象
//     * {@link Alarm}
//     * @param alarmName 报警名称
//     * @param vehcielId 车辆标识
//     * @param terminalId 终端标识
//     * @param sim 卡号
//     * @param gTime 报警时间
//     * @param oneTime 是否一次性报警
//     * @return 返回创建好的报警对象
//     */
//    private static Alarm createTerminalAlarm(String alarmName,int vehcielId,String terminalId,String sim,
//                                             String gTime,boolean oneTime) {
//        Alarm alarm = new Alarm();
//        alarm.setAlarmOrigin(AlarmOrigin.TERMINAL);
//        alarm.setVehicleId(vehcielId);
//        alarm.setSim(sim);
//        alarm.setTerminalId(terminalId);
//        alarm.setName(alarmName);
//        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//        Date startTime = null;
//        Date endTime = null;
//        try{
//            startTime = sdf.parse(gTime);
//            if(oneTime){
//                endTime = sdf.parse(gTime);
//            }
//        }catch(Exception e){
//            log.warn(e.getMessage());
//        }
//        alarm.setStartTime(startTime);
//        alarm.setStopTime(endTime);
//        return alarm;
//    }
//
//    /**
//     * 检查并且保存更新报警
//     * @param alarmByte 报警数据
//     * @param buf 报警验证数据
//     * @param idsPos 报警纪录位置
//     * @param ids 报警纪录数据
//     * @param alarmName 报警名称
//     * @param terminalId 车辆ID
//     * @param gTime 报警时间
//     */
//    private static void checkAlarm(DataPersistenceService dataPersistenceService,
//                                   byte alarmByte,byte buf,int idsPos,Integer[] ids,String alarmName,String terminalId,String gTime){
//        // 保存一份到 mq
//        if( (alarmByte & buf) == buf && ids[idsPos] == null ){
//            BindInfo bindInfo = dataPersistenceService.findBindInfoByTerminalId(terminalId);
//            Alarm alarm = Jt808Helper.createTerminalAlarm(alarmName,bindInfo.getVehicleId(),bindInfo.getTerminalId(),
//                    bindInfo.getSim(),gTime,false);
//            ids[idsPos] = dataPersistenceService.saveAlarm(alarm);
//        }
//        if( (alarmByte & buf) == 0 && ids[idsPos] != null){
//            dataPersistenceService.updateAlarmEndtime(ids[idsPos],gTime);
//            ids[idsPos] = null;
//        }
//    }
//
//

//
//    /**
//     * 位置信息汇报
//     * @param phoneNum 电话号码字节数组
//     * @param msgBody 消息体
//     */
//    public static void locationInformationProcessing(
//            ThreadPoolExecutor tpe,
//            MemeryService memeryService,
//            DataPersistenceService dataPersistenceService,
//            byte[] phoneNum,
//            byte[] msgBody
//    ){
//
//        // 报警标识，状态，纬度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]，经度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]
//        // 高程 [单位 m]，速度 [单位 0.1 km/h]，方向 [0~359 正北为0 顺时针]，时间 [yy-mm-dd-hh-mm-ss]，附加
//        byte[]  alarms,status,lon,lat,
//                altitude,speed,direction,dateTime,attach;
//
//        alarms = ByteArrHelper.subByte(msgBody, 0, 4);
//        status = ByteArrHelper.subByte(msgBody, 4, 8);
//        lat = ByteArrHelper.subByte(msgBody, 8, 12);
//        lon = ByteArrHelper.subByte(msgBody, 12, 16);
//        altitude = ByteArrHelper.subByte(msgBody, 16, 18);
//        speed = ByteArrHelper.subByte(msgBody, 18, 20);
//        direction = ByteArrHelper.subByte(msgBody, 20, 22);
//        dateTime = ByteArrHelper.subByte(msgBody, 22, 28);
//        attach = ByteArrHelper.subByte(msgBody, 28);
//
//        double lonDouble = (double) (ByteArrHelper.fourbyte2int(lon)) / (double) 1000000;
//        double latDouble = (double) (ByteArrHelper.fourbyte2int(lat)) / (double) 1000000;
//        int altitudeInt = ByteArrHelper.twobyte2int(altitude);
//        String dateTimeStr = getDataTime(dateTime);
//        double speedDouble = (double) (ByteArrHelper.twobyte2int(speed)) / (double) 10;
//        int course = ByteArrHelper.twobyte2int(direction);
//        String deviceId = memeryService.getDeviceId(phoneNum);
//        // 以下保存信息均保存一份到 mq 用于发送到页面上
//        saveLocationInfo(tpe, dataPersistenceService, phoneNum, status,alarms,attach,dateTimeStr,lonDouble,
//                latDouble,altitudeInt,speedDouble,course,deviceId);
//        saveAlarmInfo(
//                tpe,
//                memeryService,
//                dataPersistenceService,
//                phoneNum,
//                alarms,
//                deviceId,
//                dateTimeStr
//        );
//        tpe.execute(()->{
//            // 偏航报警 限制点报警 区域报警 分段报警
//            // 记录平台报警的情况
//            yawAlarm(phoneNum,lonDouble, latDouble, speedDouble,dateTimeStr);
//            areaAlarm(phoneNum, lonDouble, latDouble, speedDouble, dateTimeStr);
//            pointAlarm(phoneNum, lonDouble, latDouble, speedDouble, dateTimeStr);
//            partSpeedLimit(phoneNum,lonDouble, latDouble, speedDouble, dateTimeStr);
//        });
//    }

    public static String getDataTime(byte[] dateTime) {
        return ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 0, 1)) + "-" +
                ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 1, 2)) + "-" +
                ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 2, 3)) + " " +
                ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 3, 4)) + ":" +
                ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 4, 5)) + ":" +
                ByteArrHelper.getBCDStr(ByteArrHelper.subByte(dateTime, 5, 6));
    }

//    /**
//     * 保存报警信息
//     * @param phoneNum 电话
//     * @param alarms 报警位
//     * @param terminalId 终端标识
//     * @param gTime 时间
//     */
//    private static void saveAlarmInfo(ThreadPoolExecutor tpe,
//                                      MemeryService memeryService,
//                                      DataPersistenceService dataPersistenceService,
//                                      byte[] phoneNum, byte[] alarms, String terminalId, String gTime){
//        tpe.execute(()->{
//            BindInfo bindInfo = dataPersistenceService.findBindInfoByTerminalId(terminalId);
//            // =======
//            // 一次性报警
//            // =======
//            if( (alarms[Const.NUMBER_3] & Const.BIN_0X01) == Const.BIN_0X01){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("紧急报警",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_3] & Const.BIN_0X08) ==  Const.BIN_0X08){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("危险预警",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_1] & Const.BIN_0X10) == Const.BIN_0X10){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("进出区域",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_1] & Const.BIN_0X20) == Const.BIN_0X20){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("进出路线",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_1] & Const.BIN_0X40) == Const.BIN_0X40){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("路段行驶时间不足或者过长",bindInfo.getVehicleId(),
//                        terminalId, bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_0] & Const.BIN_0X08) == Const.BIN_0X08){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("车辆非法点火",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            if( (alarms[Const.NUMBER_0] & Const.BIN_0X10) == Const.BIN_0X10){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("车辆非法位移",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            // 非法开门 - 终端未设置区域时 不判断非法开门
//            if( (alarms[Const.NUMBER_0] & Const.BIN_0X80) == Const.BIN_0X80){
//                Alarm alarm = Jt808Helper.createTerminalAlarm("非法开门",bindInfo.getVehicleId(),terminalId,
//                        bindInfo.getSim(),gTime,true);
//                dataPersistenceService.saveAlarm(alarm);
//            }
//            // =======
//            // 持续性报警
//            // =======
//            // 持续性报警需要更新数据库的报警数据 添加结束时间所以在前一个报警体系内需要增加一个ID 是更新数据库的ID
//            // 判断有没有之前的信息 没有则重置记录的ID内容
//            Integer[] ids = memeryService.getPreAlarms(phoneNum);
//            if(ids == null){
//                ids = new Integer[32];
//            }
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x02,30, ids, "超速报警", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x04,29, ids, "疲劳驾驶", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x10,27, ids, "GNSS 模块发生故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x20,26, ids, "GNSS 天线未接或被剪断", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x40,25, ids, "GNSS 天线短路", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[3],(byte)0x80,24, ids, "终端主电源欠压", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x01,23, ids, "终端主电源掉电", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x02,22, ids, "终端 LCD 或显示器故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x04,21, ids, "TTS 模块故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x08,20, ids, "摄像头故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x10,19, ids, "道路运输证 IC 卡模块故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x20,18, ids, "超速预警", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[2],(byte)0x40,17, ids, "疲劳驾驶预警", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[1],(byte)0x04,13, ids, "当天累计驾驶超时", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[1],(byte)0x08,12, ids, "超时停车", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[1],(byte)0x80,8, ids, "路线偏离报警", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[0],(byte)0x01,7, ids, "车辆 VSS 故障", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[0],(byte)0x02,6, ids, "车辆油量异常", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[0],(byte)0x04,5, ids, "车辆被盗", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[0],(byte)0x20,2, ids, "碰撞预警", terminalId, gTime);
//            Jt808Helper.checkAlarm(dataPersistenceService, alarms[0],(byte)0x40,1, ids, "侧翻预警", terminalId, gTime);
//            memeryService.saveAlarms(phoneNum, ids);
//        });
//    }
//
//    private static void saveLocationInfo(
//            ThreadPoolExecutor tpe,
//            DataPersistenceService dataPersistenceService,
//            byte[] phoneNum, byte[] status, byte[] alarms, byte[] attach,String gTime,
//            double lonDouble, double latDouble, int altitudeInt, double speedDouble, int course, String terminalId  ){
//        tpe.execute(()->{
//            BindInfo bindInfo = dataPersistenceService.findBindInfoByTerminalId(terminalId);
//            // 里程 单位 km ，油量 单位 L ，行驶记录仪速度 km/h
//            double mileage = 0,gas = 0,temp = 0;
//            // 人工报警事件ID 从1开始计数 ， 位置指针
//            int humanEnsureAlarmId = 0,pos = 0;
//            // 附加信息描述
//            StringBuilder attachDesc = new StringBuilder();
//            // 自定义信息
//            byte[] customData = null;
//            if(attach != null && attach.length != 0){
//                while(pos < attach.length){
//                    int attachBuf = (int)attach[pos+1];
//                    byte[] b = ByteArrHelper.subByte(attach, pos, pos + attachBuf + 2);
//                    if(b[0] == (byte)0xE0){
//                        // 如果是E0则 直接截取所有内容
//                        b = ByteArrHelper.subByte(attach, pos);
//                        pos = attach.length;
//                    }else{ pos += attachBuf + 2; }
//                    switch (b[0]) {
//                        case 0x01:
//                            mileage = (double) (ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 2,6))) / (double) 10;
//                            break;
//                        case 0x02:
//                            gas = (double) (ByteArrHelper.twobyte2int(ByteArrHelper.subByte(b, 2,4))) / (double) 10;
//                            break;
//                        case 0x03:
//                            temp = (double) (ByteArrHelper.twobyte2int(ByteArrHelper.subByte(b, 2,4))) / (double) 10;
//                            break;
//                        case 0x04:
//                            humanEnsureAlarmId = ByteArrHelper.twobyte2int(ByteArrHelper.subByte(b, 2,4));
//                            break;
//                        case 0x11:
//                            attachDesc.append(getOverSpeedAttach(b));
//                            break;
//                        case 0x12:
//                            attachDesc.append(getInAndOutAttach(b));
//                            break;
//                        case 0x13:
//                            attachDesc.append(getDriveTimeAttach(b));
//                            break;
//                        case 0x25:
//                            attachDesc.append(getStatusExtAttach(b));
//                            break;
//                        case 0x2A:
//                            attachDesc.append("IO状态位附加信息：").append((b[3] & 0x01)==0?"":"深度休眠状态，")
//                                    .append((b[3] & 0x02)==0?"":"休眠状态，").append("；");
//                            break;
//                        case 0x2B:
//                            int ad0 = ByteArrHelper.twobyte2int(new byte[]{b[5],b[4]});
//                            int ad1 = ByteArrHelper.twobyte2int(new byte[]{b[3],b[2]});
//                            attachDesc.append("模拟量：").append(String.format("AD0 %s，AD1 %s",ad0,ad1)).append("；");
//                            break;
//                        case 0x30:
//                            attachDesc.append("无线通信网络信号强度：").append(b[2]).append("；");
//                            break;
//                        case 0x31:
//                            attachDesc.append("GNSS 定位卫星数：").append(b[2]).append("；");
//                            break;
//                        case (byte)0xE0:
//                            customData = ByteArrHelper.subByte(b,b[1] + 2);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//            Date dateTimeBuf = null;
//            try{ dateTimeBuf = sdf.parse(gTime); }catch(Exception e){log.warn(e.getMessage());}
//            PositionInformation pi = new PositionInformation(terminalId, lonDouble,latDouble,altitudeInt,speedDouble,
//                    course,dateTimeBuf,ByteArrHelper.union(alarms, status),getStatusDesc(status),mileage,gas,temp,
//                    attachDesc.toString(),humanEnsureAlarmId,customData,getPhoneNum(phoneNum),bindInfo.getVehicleId());
//            dataPersistenceService.savePositionInformation(pi);
//        });
//    }

    private static String getStatusExtAttach(byte[] b){
        String attachDesc = "扩展车辆信号状态位附加信息：";
        attachDesc += (b[5] & 0x01)==0?"":"近光灯，";
        attachDesc += (b[5] & 0x02)==0?"":"远光灯，";
        attachDesc += (b[5] & 0x04)==0?"":"右转向灯，";
        attachDesc += (b[5] & 0x08)==0?"":"左转向灯，";
        attachDesc += (b[5] & 0x10)==0?"":"制动，";
        attachDesc += (b[5] & 0x20)==0?"":"倒档，";
        attachDesc += (b[5] & 0x40)==0?"":"雾灯，";
        attachDesc += (b[5] & 0x80)==0?"":"示廓灯，";
        attachDesc += (b[4] & 0x01)==0?"":"喇叭，";
        attachDesc += (b[4] & 0x02)==0?"":"空调，";
        attachDesc += (b[4] & 0x04)==0?"":"空挡，";
        attachDesc += (b[4] & 0x08)==0?"":"缓速器，";
        attachDesc += (b[4] & 0x10)==0?"":"ABS，";
        attachDesc += (b[4] & 0x20)==0?"":"加热器，";
        attachDesc += (b[4] & 0x40)==0?"":"离合器，";
        attachDesc += "；";
        return attachDesc;
    }

    private static String getDriveTimeAttach(byte[] b){
        String attachDesc = "路段行驶时间不足/过长报警附加信息：";
        attachDesc += "路段ID为" + ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 2, 6))
                + "，路段行驶时间 " + ByteArrHelper.twobyte2int(ByteArrHelper.subByte(b, 6, 8))
                + " 秒，结果 " + ((b[8]==0)?"不足":"过长") + "；";
        return attachDesc;
    }

    private static String getInAndOutAttach(byte[] b){
        StringBuilder attachDesc = new StringBuilder();
        attachDesc.append("进出区域/路线报警附加信息：");
        switch (b[7]) {
            case 0:
                attachDesc.append("进");
                break;
            case 1:
                attachDesc.append("出");
                break;
            default:
                break;
        }
        switch (b[2]) {
            case 1:
                attachDesc.append("圆形区域，编号为");
                break;
            case 2:
                attachDesc.append("矩形区域，编号为");
                break;
            case 3:
                attachDesc.append("多边形区域，编号为");
                break;
            case 4:
                attachDesc.append("路线，编号为");
                break;
            default:
                break;
        }
        attachDesc.append(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 3,7)));
        attachDesc.append("；");
        return attachDesc.toString();
    }

    private static String getOverSpeedAttach(byte[] b){
        StringBuilder attachDesc = new StringBuilder();
        attachDesc.append("超速报警附加信息：");
        switch (b[2]) {
            case 0:
                attachDesc.append("无特定位置，编号为0");
                break;
            case 1:
                attachDesc.append("圆形区域，编号为")
                        .append(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 3, 7)));
                break;
            case 2:
                attachDesc.append("矩形区域，编号为")
                        .append(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 3, 7)));
                break;
            case 3:
                attachDesc.append("多边形区域，编号为")
                        .append(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 3, 7)));
                break;
            case 4:
                attachDesc.append("路段，编号为")
                        .append(ByteArrHelper.fourbyte2int(ByteArrHelper.subByte(b, 3, 7)));
                break;
            default:
                break;
        }
        attachDesc.append("；");
        return attachDesc.toString();
    }

    /**
     * 获取状态位描述信息
     * @param status 状态位
     * @return 状态位描述
     */
    private static String getStatusDesc(byte[] status) {
        String remarkSb = "";
        remarkSb += ((status[3] & 0x01) == 0 ? "ACC关；" : "ACC开；");
        remarkSb += ((status[3] & 0x02) == 0 ? "未定位；" : "定位；");
        remarkSb += ((status[3] & 0x04) == 0 ? "北纬；" : "南纬；");
        remarkSb += ((status[3] & 0x08) == 0 ? "东经；" : "西经；");
        remarkSb += ((status[3] & 0x10) == 0 ? "运营；" : "停运；");
        remarkSb += ((status[3] & 0x20) == 0 ? "经纬度未经保密插件加密；" : "经纬度已经保密插件加密；");
        remarkSb += ((status[2] & 0x03) == 0 ? "空车；" : "");
        remarkSb += ((status[2] & 0x03) == 0x03 ? "满载；" : "");
        // 8位和9位有一点歧义 是8-9一起读 还是9-8一起读
        // 目前按照8-9一起读
        remarkSb += ((status[2] & 0x03) == 0x01 ? "半载；" : "");
        remarkSb += ((status[2] & 0x04) == 0 ? "车辆油路正常；" : "车辆油路断开；");
        remarkSb += ((status[2] & 0x08) == 0 ? "车辆电路正常；" : "车辆电路断开；");
        remarkSb += ((status[2] & 0x10) == 0 ? "车门解锁；" : "车门加锁；");
        remarkSb += ((status[2] & 0x20) == 0 ? "门1（前门）关；" : "门1（前门）开；");
        remarkSb += ((status[2] & 0x40) == 0 ? "门2（中门）关；" : "门2（中门）开；");
        remarkSb += ((status[2] & 0x80) == 0 ? "门3（后门）关；" : "门3（后门）开；");
        remarkSb += ((status[1] & 0x01) == 0 ? "门4（驾驶席门）关；" : "门4（驾驶席门）开；");
        remarkSb += ((status[1] & 0x02) == 0 ? "门5（自定义门）关；" : "门5（自定义门）开；");
        remarkSb += ((status[1] & 0x04) == 0 ? "未使用 GPS 卫星进行定位；" : "使用 GPS 卫星进行定位；");
        remarkSb += ((status[1] & 0x08) == 0 ? "未使用北斗卫星进行定位；" : "使用北斗卫星进行定位；");
        remarkSb += ((status[1] & 0x10) == 0 ? "未使用 GLONASS 卫星进行定位；" : "使用 GLONASS 卫星进行定位；");
        remarkSb += ((status[1] & 0x20) == 0 ? "未使用 Galileo 卫星进行定位；" : "使用 Galileo 卫星进行定位；");
        return remarkSb;
    }

    /**
     * 获取多媒体信息描述
     * @param b 标识
     * @return 多媒体信息描述
     */
    public static String getMediaEventDesc(byte b) {
        String event;
        switch(b){
            case 0x00:
                event = "平台下发指令";
                break;
            case 0x01:
                event = "定时动作";
                break;
            case 0x02:
                event = "抢劫报警触发";
                break;
            case 0x03:
                event = "碰撞侧翻报警触发";
                break;
            case 0x04:
                event = "门开拍照";
                break;
            case 0x05:
                event = "门关拍照";
                break;
            case 0x06:
                event = "车门由开变关，时速从小于 20 公里到超过 20 公里";
                break;
            case 0x07:
                event = "定距拍照";
                break;
            default:
                event = "保留";
                break;
        }
        return event;
    }


    public static String toGB18030String(byte[] data) throws UnsupportedEncodingException {
        return new String(data, "GB18030").trim();
    }

}
