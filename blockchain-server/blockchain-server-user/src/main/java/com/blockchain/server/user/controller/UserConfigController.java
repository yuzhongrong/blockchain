package com.blockchain.server.user.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.user.controller.api.UserConfigApi;
import com.blockchain.server.user.entity.Config;
import com.blockchain.server.user.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(UserConfigApi.USER_API)
@RestController
@RequestMapping("/userConfig")
public class UserConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    /**
     * 查询参数列表
     */
    @ApiOperation(value = UserConfigApi.List.METHOD_API_NAME, notes = UserConfigApi.List.METHOD_API_NOTE)
    @GetMapping("/list")
    public ResultDTO list() {
        return ResultDTO.requstSuccess(configService.list());
    }

    /**
     * 修改
     */
    @ApiOperation(value = UserConfigApi.Update.METHOD_API_NAME, notes = UserConfigApi.Update.METHOD_API_NOTE)
    @GetMapping("/update")
    public ResultDTO update(Config config) {
        configService.update(config);
        return ResultDTO.requstSuccess();
    }

}
