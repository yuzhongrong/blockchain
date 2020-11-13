package com.blockchain.server.user.service.impl;

import com.blockchain.server.user.entity.Config;
import com.blockchain.server.user.mapper.ConfigMapper;
import com.blockchain.server.user.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author huangxl
 * @create 2019-02-25 17:50
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;


    @Override
    public List<Config> list() {
        return configMapper.selectAll();
    }

    @Override
    public void update(Config config) {
        Config update = new Config();
        update.setId(config.getId());
        update.setDataValue(config.getDataValue());
        update.setModifyTime(new Date());

        configMapper.updateByPrimaryKeySelective(update);
    }

}
