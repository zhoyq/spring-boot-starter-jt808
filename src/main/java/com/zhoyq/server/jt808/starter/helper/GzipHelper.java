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

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/20
 */
@Slf4j
public class GzipHelper {
    /**
     * 使用gzip对数据进行压缩
     */
    public static byte[] gzip(byte[] data){
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            int count;
            byte[] buf = new byte[1024];
            while ((count = bais.read(buf)) != -1) {
                gos.write(buf, 0, count);
            }
            gos.finish();
            gos.flush();
            gos.close();
            baos.flush();
            baos.close();
            bais.close();
            return baos.toByteArray();
        } catch (Exception e){
            log.warn(e.getMessage());
        }
        return null;
    }


    /**
     * 使用gzip对数据进行解压缩
     */
    public static byte[] ungzip(byte[] data){
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPInputStream gis = new GZIPInputStream(bais);
            int count;
            byte[] buf = new byte[1024];
            while ((count = gis.read(buf)) != -1) {
                baos.write(buf, 0, count);
            }
            gis.close();
            baos.flush();
            baos.close();
            bais.close();
            return baos.toByteArray();
        }catch(Exception e){
            log.warn(e.getMessage());
        }
        return null;
    }
}
