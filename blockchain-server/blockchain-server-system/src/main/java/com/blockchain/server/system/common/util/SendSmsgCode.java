package com.blockchain.server.system.common.util;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 短信API服务调用
 **/
@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "feigesms")
public class SendSmsgCode {
    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(SendSmsgCode.class);

    private boolean closed;//是否开启
    private long timeout;//超时时间，单位：分钟

    private String URL;
    private String Account;
    private String Pwd;
    private String TemplateId_ZH_CN;
    private String TemplateId_ZH_HK;
    private String TemplateId_EN_US;
    private String SignId_ZH_CN;
    private String SignId_EN_US;

    private String CHINA_CODE = "86";
    private String BIG5_CODE = "852,853,886";

    /**
     * 请求短信接口，发送短信验证码
     *
     * @param mobile  手机号
     * @param smsCode 短信验证码
     * @return
     */
    @Async
    public void sendSmsg(String mobile, String smsCode, String internationalCode) {
        if (closed) {//如果没有开启短信，则不发送短信
            return;
        }

        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("Account", Account);
        requestEntity.add("Pwd", Pwd);
        requestEntity.add("Content", smsCode);
        requestEntity.add("Mobile", internationalCode + mobile);
        //国际代码
        if (CHINA_CODE.equals(internationalCode)) {
            requestEntity.add("TemplateId", TemplateId_ZH_CN);
            requestEntity.add("SignId", SignId_ZH_CN);
        } else if (BIG5_CODE.contains(internationalCode)) {
            requestEntity.add("TemplateId", TemplateId_ZH_HK);
            requestEntity.add("SignId", SignId_ZH_CN);
        } else {
            requestEntity.add("TemplateId", TemplateId_EN_US);
            requestEntity.add("SignId", SignId_EN_US);
        }

        try {
            String res = restTemplate.postForObject(URL, requestEntity, String.class);
            LOG.info("===send sms end==== : {} ", res);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}