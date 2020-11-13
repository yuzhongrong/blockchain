package com.blockchain.server.user.service.impl;

import com.blockchain.server.user.entity.UserSuggestion;
import com.blockchain.server.user.mapper.UserSuggestMapper;
import com.blockchain.server.user.service.UserSuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSuggestServiceImpl implements UserSuggestService {
    @Autowired
    private UserSuggestMapper userSuggestMapper;


    @Override
    public List<UserSuggestion> list(String userName) {
        return userSuggestMapper.list(userName);
    }

}
