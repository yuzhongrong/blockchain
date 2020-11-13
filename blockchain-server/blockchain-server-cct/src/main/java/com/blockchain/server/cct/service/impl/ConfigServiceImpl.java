package com.blockchain.server.cct.service.impl;

import com.blockchain.server.cct.entity.Config;
import com.blockchain.server.cct.entity.ConfigLog;
import com.blockchain.server.cct.mapper.ConfigMapper;
import com.blockchain.server.cct.service.ConfigLogService;
import com.blockchain.server.cct.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private ConfigLogService logService;

    @Override
    @Transactional
    public int updateConfig(String sysUserId, String ipAddr, String tag, String key, String val, String status) {
        Date now = new Date();
        Config config = selectByKey(key);

        ConfigLog log = new ConfigLog();
        log.setIpAddress(ipAddr);
        log.setSysUserId(sysUserId);
        log.setId(UUID.randomUUID().toString());
        log.setDataKey(key);
        log.setDataValue(config.getDataValue());
        log.setDataValueBefore(config.getDataValue());
        log.setDataStatus(config.getDataStatus());
        log.setDataStatusBefore(config.getDataStatus());
        log.setDataTag(config.getDataTag());
        log.setCreateTime(now);

        if (StringUtils.isNotBlank(val)) {
            log.setDataValue(val);
            config.setDataValue(val);
        }
        if (StringUtils.isNotBlank(status)) {
            log.setDataStatus(status);
            config.setDataStatus(status);
        }
        if (StringUtils.isNotBlank(tag)) {
            log.setDataTag(tag);
            config.setDataTag(tag);
        }
        config.setModifyTime(now);

        logService.insertConfigLog(log);
        return configMapper.updateByPrimaryKeySelective(config);
    }

    @Override
    public List<Config> listConfig() {
        return configMapper.selectAll();
    }

    @Override
    public Config selectByKey(String key) {
        return configMapper.selectByKey(key);
    }
}
