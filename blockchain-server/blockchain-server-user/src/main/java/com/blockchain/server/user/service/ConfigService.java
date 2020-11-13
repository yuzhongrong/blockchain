package com.blockchain.server.user.service;

import com.blockchain.server.user.entity.Config;

import java.util.List;

/**
 * @author huangxl
 * @create 2019-02-25 17:48
 */
public interface ConfigService {

    /**
     * 获取参数列表
     */
    List<Config> list();

    /**
     * 更新参数
     */
    void update(Config config);

}
