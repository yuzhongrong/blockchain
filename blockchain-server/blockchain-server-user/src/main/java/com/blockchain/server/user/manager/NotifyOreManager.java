package com.blockchain.server.user.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotifyOreManager {
    private static Logger LOG = LoggerFactory.getLogger(NotifyOreManager.class);

    @Autowired
    private RestTemplate restTemplate;

    //通知ore用户通过实名认证接口地址
    @Value("${notifyOreUrl}")
    private String URL;


    /**
     * 通知ore用户通过实名认证
     */
    @Async
    public void addUserAuthentication(String userId) {
        try {
            String res = restTemplate.getForEntity(URL + userId, String.class).getBody();
            LOG.info("********* 通知ore用户通过实名认证结果：{} ********", res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
