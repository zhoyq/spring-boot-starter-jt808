/*
 *  Copyright (c) 2021. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2021.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2021. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 苏标 数据下行透传 查询基本信息 外设ID
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/9
 */
@Slf4j
@Getter
@Setter
public class DataTransportDeviceInfo {

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 产品型号
     */
    private String productModel;

    /**
     * 硬件版本号
     */
    private String hardwareVersion;

    /**
     * 软件版本号
     */
    private String softwareVersion;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 客户代码
     */
    private String customerCode;

    @Setter
    @Getter
    private static class Ret {
        private int len;
        private String content;
    }

    private static Ret handleProp(int pos, byte[] subByte) throws Exception{
        int length = subByte[pos] & 0xff;
        if (length > 32) {
            throw new Exception("length over 32 limit!");
        }
        if (length != 0) {
            byte[] bytes = ByteArrHelper.subByte(subByte, pos + 1, length);
            Ret ret = new Ret();
            ret.setContent(Jt808Helper.toAsciiString(bytes));
            ret.setLen(length + 1);
            return ret;
        }
        return null;
    }

    public static List<DataTransportDeviceInfo> fromBytes(byte[] subByte) {
        List<DataTransportDeviceInfo> ret = new ArrayList<>();
        int pos = 0;
        while(pos < subByte.length){
            try {
                DataTransportDeviceInfo info = new DataTransportDeviceInfo();

                Ret companyRet = handleProp(pos, subByte);
                assert companyRet != null;
                pos += companyRet.getLen();
                info.setCompanyName(companyRet.getContent());

                Ret productModelRet = handleProp(pos, subByte);
                assert productModelRet != null;
                pos += productModelRet.getLen();
                info.setProductModel(productModelRet.getContent());

                Ret hardwareVersionRet = handleProp(pos, subByte);
                assert hardwareVersionRet != null;
                pos += hardwareVersionRet.getLen();
                info.setHardwareVersion(hardwareVersionRet.getContent());

                Ret softwareVersionRet = handleProp(pos, subByte);
                assert softwareVersionRet != null;
                pos += softwareVersionRet.getLen();
                info.setSoftwareVersion(softwareVersionRet.getContent());

                Ret deviceIdRet = handleProp(pos, subByte);
                assert deviceIdRet != null;
                pos += deviceIdRet.getLen();
                info.setDeviceId(deviceIdRet.getContent());

                Ret customerCodeRet = handleProp(pos, subByte);
                assert customerCodeRet != null;
                pos += customerCodeRet.getLen();
                info.setCustomerCode(customerCodeRet.getContent());

                ret.add(info);
            }catch (Exception e){
                log.warn(e.getMessage());
                break;
            }
        }
        return ret;
    }
}
