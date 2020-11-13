package com.blockchain.server.tron;

import com.blockchain.server.base.BaseConf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huangxl
 * @create 2018-11-13 10:15
 */

@SpringBootApplication(scanBasePackageClasses = {BaseConf.class, TronApplication.class})
public class TronApplication {
    public static void main(String[] args) {
        SpringApplication.run(TronApplication.class, args);
    }
}
