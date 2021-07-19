package com.zhoyq.server.jt808.starter.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-07-19
 */
@Setter
@Getter
public class AudioOrVideoProp {

    private byte audioEncoding;
    private byte audioChannelNumber;
    private byte audioRadio;
    private byte audioBit;
    private byte[] audioLength;
    private byte audioOutSupport;
    private byte videoEncoding;
    private byte maxAudioChannelNumber;
    private byte maxVideoChannelNumber;

    public static AudioOrVideoProp fromBytes(byte[] msgBody) {
        if (msgBody.length != 10) {
            return null;
        }

        AudioOrVideoProp prop = new AudioOrVideoProp();
        prop.setAudioEncoding(msgBody[0]);
        prop.setAudioChannelNumber(msgBody[1]);
        prop.setAudioRadio(msgBody[2]);
        prop.setAudioBit(msgBody[3]);
        prop.setAudioLength(new byte[]{msgBody[4], msgBody[5]});
        prop.setAudioOutSupport(msgBody[6]);
        prop.setVideoEncoding(msgBody[7]);
        prop.setMaxAudioChannelNumber(msgBody[8]);
        prop.setMaxVideoChannelNumber(msgBody[9]);
        return prop;
    }
}
