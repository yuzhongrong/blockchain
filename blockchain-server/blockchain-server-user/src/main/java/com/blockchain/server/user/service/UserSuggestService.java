package com.blockchain.server.user.service;

import com.blockchain.server.user.entity.UserSuggestion;

import java.util.List;

public interface UserSuggestService {

    List<UserSuggestion> list(String userName);

}
