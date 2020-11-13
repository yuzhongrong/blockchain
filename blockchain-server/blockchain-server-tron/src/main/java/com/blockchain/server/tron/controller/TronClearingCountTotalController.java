package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.tron.controller.api.TronCountTotalApi;
import com.blockchain.server.tron.dto.wallet.TronCountTotalInfoDTO;
import com.blockchain.server.tron.service.ITronClearingCountTotalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@RestController
@RequestMapping("/countTotal")
public class TronClearingCountTotalController {
    @Autowired
    ITronClearingCountTotalService tronClearingCountTotalService;

    @ApiOperation(value = TronCountTotalApi.All.METHOD_TITLE_NAME, notes = TronCountTotalApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all() {
        List<TronCountTotalInfoDTO> totalInfos = tronClearingCountTotalService.selectInfoAll();
        return ResultDTO.requstSuccess(totalInfos);
    }

    @ApiOperation(value = TronCountTotalApi.Total.METHOD_TITLE_NAME, notes = TronCountTotalApi.Total.METHOD_TITLE_NOTE)
    @PostMapping("/total")
    public ResultDTO total() {
        List<TronCountTotalInfoDTO> totalInfos = tronClearingCountTotalService.insertTotals();
        return ResultDTO.requstSuccess(totalInfos);
    }

}
