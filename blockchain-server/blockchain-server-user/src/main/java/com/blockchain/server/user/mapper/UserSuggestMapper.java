package com.blockchain.server.user.mapper;

import com.blockchain.server.user.entity.UserSuggestion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserSuggestMapper extends Mapper<UserSuggestion> {

    List<UserSuggestion> list(@Param("userName") String userName);

}
