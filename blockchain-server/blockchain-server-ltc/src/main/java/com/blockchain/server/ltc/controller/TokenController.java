package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.ltc.controller.api.TokenApi;
import com.blockchain.server.ltc.entity.Token;
import com.blockchain.server.ltc.service.ITokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(TokenApi.API_NAME)
@RestController
@RequestMapping("/coin")
public class TokenController {
    @Autowired
    ITokenService tokenService;

    @ApiOperation(value = TokenApi.All.METHOD_TITLE_NAME, notes = TokenApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all() {
        List<Token> list = tokenService.selectAll();
        return ResultDTO.requstSuccess(list);
    }
}
