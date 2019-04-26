package com.pinyougou.common;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

public class SmsTest {

    public static void main(String[] args) {
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        Map<String, String> params = new HashMap<>();
        params.put("phone","18475582887");
        params.put("signName","五子连珠");
        params.put("templateCode","SMS_11480310");
        params.put("templateParam","{'number' : '8888'}");
        String content = httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms", params);//sms.pinyougou.com/sms/sendSms
        System.out.println("content = "+content);
    }
}
