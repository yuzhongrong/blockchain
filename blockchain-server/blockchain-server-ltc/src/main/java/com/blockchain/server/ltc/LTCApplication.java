package com.blockchain.server.ltc;

import com.blockchain.server.base.BaseConf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {BaseConf.class, LTCApplication.class})
public class LTCApplication {
    public static void main(String[] args) {
        SpringApplication.run(LTCApplication.class);
    }
}
