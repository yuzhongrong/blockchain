package com.blockchain.server.user.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.user.controller.api.UserReplyApi;
import com.blockchain.server.user.dto.UserReplyDTO;
import com.blockchain.server.user.service.UserReplyService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(UserReplyApi.API)
@RestController
@RequestMapping("/reply")
public class UserReplyController extends BaseController {
    @Autowired
    private UserReplyService userReplyService;


    @ApiOperation(value = UserReplyApi.List.METHOD_TITLE_NAME, notes = UserReplyApi.List.METHOD_TITLE_NOTE)
    @GetMapping("/list")
    public ResultDTO list(@ApiParam(UserReplyApi.List.USER_NAME) @RequestParam(value = "userName", required = false) String userName,
                          @ApiParam(UserReplyApi.PAGE_NUM) @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                          @ApiParam(UserReplyApi.PAGE_SIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return generatePage(userReplyService.listAll(userName));
    }

    @ApiOperation(value = UserReplyApi.Insert.METHOD_TITLE_NAME, notes = UserReplyApi.Insert.METHOD_TITLE_NOTE)
    @GetMapping("/insert")
    public ResultDTO insert(UserReplyDTO replyDTO) {
        userReplyService.insert(replyDTO);
        return ResultDTO.requstSuccess();
    }

}
