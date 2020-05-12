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
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 加密解密帮助类
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/6
 */
@Slf4j
@Component
public class RsaHelper {
    /**
     * 产生 公钥和密钥
     * @param keySize 密钥大小
     * @return 密钥对
     */
    public KeyPair genRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(keySize);
        return gen.generateKeyPair();
    }

    /**
     * 通过n和e获取公钥
     * @param modulus n
     * @param publicExponent e
     * @return 公钥
     * @throws InvalidKeySpecException 异常
     * @throws NoSuchAlgorithmException 异常
     */
    public PublicKey publicKey(byte[] modulus, byte[] publicExponent)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        BigInteger modulusBigInt = new BigInteger(modulus);
        BigInteger publicExponentBigInt = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusBigInt, publicExponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 公钥解密
     * @param data 数据
     * @param publicKey 公钥
     * @return 解密数据
     * @throws InvalidKeyException 异常
     * @throws BadPaddingException 异常
     * @throws IllegalBlockSizeException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchPaddingException 异常
     */
    public synchronized byte[] rsaDecodeByPublicKey(byte[] data, PublicKey publicKey)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     * @param data 数据
     * @param privateKey 私钥
     * @return 加密数据
     * @throws InvalidKeyException 异常
     * @throws BadPaddingException 异常
     * @throws IllegalBlockSizeException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchPaddingException 异常
     */
    public byte[] rsaEncodeByPrivateKey(byte[] data, PrivateKey privateKey)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥签名
     * @param data 数据
     * @param privateKey 私钥
     * @return 签名
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     * @throws SignatureException 异常
     */
    public byte[] rsaSign(byte[] data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 公钥验证
     * @param data 数据
     * @param publicKey 公钥
     * @param sign 签名
     * @return 验证结果
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     * @throws SignatureException 异常
     */
    public boolean rsaVerify(byte[] data, PublicKey publicKey, byte[] sign)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }
}
