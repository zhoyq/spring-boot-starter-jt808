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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 苏标：文件上传完成信息格式 0x9212
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/13
 */
@Slf4j
@Setter
@Getter
public class FileUploadAnswer {

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private int fileType;
    /**
     * 上传结果
     */
    private int uploadResult;
    /**
     * 不传分包列表
     */
    private List<FileUploadAnswerPkg> pkgs;


    public byte[] toBytes() {
        try {
            byte[] fileNameBytes = fileName.getBytes("GBK");
            byte[] ret = ByteArrHelper.union(
                    new byte[]{(byte)fileNameBytes.length},
                    fileNameBytes,
                    new byte[]{(byte)fileType, (byte)uploadResult, pkgs == null? 0x00 :(byte)pkgs.size()}
            );

            if(pkgs != null){
                for (FileUploadAnswerPkg pkg : pkgs) {
                    ret = ByteArrHelper.union(ret, pkg.toBytes());
                }
            }

            return ret;
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
