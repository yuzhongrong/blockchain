package com.blockchain.server.user.mapper;

import com.blockchain.server.user.dto.AuthenticationApplyDto;
import com.blockchain.server.user.entity.AuthenticationApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Harvey
 * @date 2019/3/7 11:10
 * @user WIN10
 */
@Repository
public interface AuthenticationApplyMapper extends Mapper<AuthenticationApply> {

}
