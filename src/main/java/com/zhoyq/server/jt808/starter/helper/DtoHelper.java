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

import com.zhoyq.server.jt808.starter.dto.CameraInfo;
import com.zhoyq.server.jt808.starter.dto.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/6
 */
@Component
public class DtoHelper {
    @Autowired
    private ByteArrHelper byteArrHelper;

    public Parameter genParameter(byte[] data){
        Parameter p = new Parameter();
        p.setParameterId(byteArrHelper.subByte(data, 0, 4));
        p.setLength(data[4]);
        p.setValue(byteArrHelper.subByte(data, 5, p.getLength() + 5));
        return p;
    }

    public CameraInfo genCameraInfo(byte[] data){
        CameraInfo c = new CameraInfo();
        c.setId(data[0]);
        c.setComm(byteArrHelper.subByte(data, 1, 3));
        c.setSpaceTime(byteArrHelper.subByte(data, 3, 5));
        c.setSaveSign(data[5]);
        c.setResolution(data[6]);
        c.setQuality(data[7]);
        c.setLuminance(data[8]);
        c.setContrast(data[9]);
        c.setSaturation(data[10]);
        c.setTone(data[11]);
        return c;
    }
}
