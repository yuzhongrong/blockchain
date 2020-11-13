package com.blockchain.server.user.dto;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReplyDTO extends BaseModel {
    private Long id;
    private String userOpenId;
    private String suggestionId;
    private String suggestion;
    private String userName;
    private String content;
    private Date createTime;
    private Date modifyTime;
}