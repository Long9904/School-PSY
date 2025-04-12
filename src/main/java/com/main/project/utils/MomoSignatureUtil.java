package com.main.project.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MomoSignatureUtil {
    public static String signSHA256(String rawData, String secretKey) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] result = mac.doFinal(rawData.getBytes());
        return Hex.encodeHexString(result);
    }
}
