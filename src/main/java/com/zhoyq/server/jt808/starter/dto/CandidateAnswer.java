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

package com.zhoyq.server.jt808.starter.dto;

/**
 * 候选答案
 * @author zhoyq
 * @date 2018-06-27
 */
public class CandidateAnswer {
    private byte id;
    private byte[] length;
    private byte[] content;
    public byte getId() {
        return id;
    }
    public void setId(byte id) {
        this.id = id;
    }
    public byte[] getLength() {
        return length;
    }
    public void setLength(byte[] length) {
        this.length = length;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }

}
