package com.blockchain.server.user.mapper;

import com.blockchain.server.user.dto.UserReplyDTO;
import com.blockchain.server.user.entity.UserReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserReplyMapper extends Mapper<UserReply> {

    List<UserReplyDTO> listAll(@Param("userName") String userName);

}
