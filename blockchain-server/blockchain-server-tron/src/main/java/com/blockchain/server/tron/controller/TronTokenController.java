package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.tron.controller.api.TronTokenApi;
import com.blockchain.server.tron.entity.TronToken;
import com.blockchain.server.tron.service.ITronTokenService;
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
@Api(TronTokenApi.API_NAME)
@RestController
@RequestMapping("/coin")
public class TronTokenController {
    @Autowired
    ITronTokenService tronTokenService;

    @ApiOperation(value = TronTokenApi.All.METHOD_TITLE_NAME, notes = TronTokenApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all() {
        List<TronToken> list = tronTokenService.selectAll();
        return ResultDTO.requstSuccess(list);
    }
}
