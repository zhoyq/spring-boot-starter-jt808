package com.zhoyq.server.jt808.starter.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-19
 */
public class AlarmAttachUploadTest {

    @Test
    public void generatePlatformAlarmId() {
        byte[] bytes = AlarmAttachUpload.generatePlatformAlarmId();
        assertEquals(bytes.length, 32);
    }
}
