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
import java.security.spec.*;

/**
 * RSA 加密解密帮助类
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/6
 */
@Slf4j
public class RsaHelper {
    /**
     * 产生 公钥和密钥
     * @param keySize 密钥大小
     * @return 密钥对
     */
    public static KeyPair genRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
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
    public static PublicKey publicKey(byte[] modulus, byte[] publicExponent)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        BigInteger modulusBigInt = new BigInteger(modulus);
        BigInteger publicExponentBigInt = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusBigInt, publicExponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过 n 和 e 获取公钥
     * @param modulusBigInt n
     * @param publicExponentBigInt e
     * @return 公钥
     * @throws InvalidKeySpecException 异常
     * @throws NoSuchAlgorithmException 异常
     */
    public static PublicKey publicKey(BigInteger modulusBigInt, BigInteger publicExponentBigInt)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusBigInt, publicExponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过n和d获取私钥
     * @param modulus n
     * @param privateExponent d
     * @return 私钥
     * @throws InvalidKeySpecException 异常
     * @throws NoSuchAlgorithmException 异常
     */
    public static PrivateKey privateKey(byte[] modulus, byte[] privateExponent)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        BigInteger modulusBigInt = new BigInteger(modulus);
        BigInteger publicExponentBigInt = new BigInteger(privateExponent);
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulusBigInt, publicExponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 通过 n 和 d 获取私钥
     * @param modulusBigInt n
     * @param publicExponentBigInt d
     * @return 私钥
     * @throws InvalidKeySpecException 异常
     * @throws NoSuchAlgorithmException 异常
     */
    public static PrivateKey privateKey(BigInteger modulusBigInt, BigInteger publicExponentBigInt)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulusBigInt, publicExponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * TODO 处理超长数据加密
     * 公钥加密
     * @param data 数据
     * @param publicKey 公钥
     * @return 解密数据
     * @throws InvalidKeyException 异常
     * @throws BadPaddingException 异常
     * @throws IllegalBlockSizeException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchPaddingException 异常
     */
    public static byte[] rsaEncodeByPublicKey(byte[] data, PublicKey publicKey)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * TODO 处理超长数据解密
     * 私钥解密
     * @param data 数据
     * @param privateKey 私钥
     * @return 加密数据
     * @throws InvalidKeyException 异常
     * @throws BadPaddingException 异常
     * @throws IllegalBlockSizeException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchPaddingException 异常
     */
    public static synchronized byte[] rsaDecodeByPrivateKey(byte[] data, PrivateKey privateKey)
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
    public static byte[] rsaSign(byte[] data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
    public static boolean rsaVerify(byte[] data, PublicKey publicKey, byte[] sign)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }
}
