package com.blockchain.server.user.mapper;

import com.blockchain.server.user.entity.UserAuthentication;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Harvey
 * @date 2019/3/7 10:30
 * @user WIN10
 */
@Repository
public interface UserAuthenticationMapper extends Mapper<UserAuthentication> {

}
