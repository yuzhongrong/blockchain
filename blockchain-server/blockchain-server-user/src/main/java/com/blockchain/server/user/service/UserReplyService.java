package com.blockchain.server.user.service;

import com.blockchain.server.user.dto.UserReplyDTO;

import java.util.List;

public interface UserReplyService {

    List<UserReplyDTO> listAll(String userName);

    void insert(UserReplyDTO replyDTO);

}
