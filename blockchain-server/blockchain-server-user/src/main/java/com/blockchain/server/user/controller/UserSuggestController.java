package com.blockchain.server.user.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.user.controller.api.UserSuggestApi;
import com.blockchain.server.user.service.UserSuggestService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(UserSuggestApi.USER_SUGGEST_API)
@RestController
@RequestMapping("/suggest")
public class UserSuggestController extends BaseController {
    @Autowired
    private UserSuggestService userSuggestService;


    @ApiOperation(value = UserSuggestApi.List.METHOD_TITLE_NAME, notes = UserSuggestApi.List.METHOD_TITLE_NOTE)
    @GetMapping("/list")
    public ResultDTO list(@ApiParam(UserSuggestApi.List.USER_NAME) @RequestParam(value = "userName", required = false) String userName,
                          @ApiParam(UserSuggestApi.PAGE_NUM) @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                          @ApiParam(UserSuggestApi.PAGE_SIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return generatePage(userSuggestService.list(userName));
    }

}
