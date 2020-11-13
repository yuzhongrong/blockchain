package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.tron.controller.api.TronTotalApi;
import com.blockchain.server.tron.dto.wallet.TronTotalInfoDTO;
import com.blockchain.server.tron.service.ITronClearingTotalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@RestController
@RequestMapping("/total")
public class TronClearingTotalController {
    @Autowired
    ITronClearingTotalService tronClearingTotalService;

    @ApiOperation(value = TronTotalApi.All.METHOD_TITLE_NAME, notes = TronTotalApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all(@ApiParam(TronTotalApi.All.METHOD_API_USERID) @RequestParam(value = "userId") String userId) {
        List<TronTotalInfoDTO> totalInfos = tronClearingTotalService.selectInfoAll(userId);
        return ResultDTO.requstSuccess(totalInfos);
    }

    @ApiOperation(value = TronTotalApi.Save.METHOD_TITLE_NAME, notes = TronTotalApi.Save.METHOD_TITLE_NOTE)
    @PostMapping("/save")
    public ResultDTO save(@ApiParam(TronTotalApi.Save.METHOD_API_TOTALID) @RequestParam(value = "totalId") String totalId,
                          @ApiParam(TronTotalApi.Save.METHOD_API_AMOUNT) @RequestParam(value = "amount") BigDecimal amount) {
        tronClearingTotalService.updateCorr(totalId, amount);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TronTotalApi.Total.METHOD_TITLE_NAME, notes = TronTotalApi.Total.METHOD_TITLE_NOTE)
    @PostMapping("/total")
    public ResultDTO total(@ApiParam(TronTotalApi.Total.METHOD_API_USERID) @RequestParam(value = "userId") String userId) {
        List<TronTotalInfoDTO> totalInfos = tronClearingTotalService.insertTotals(userId);
        return ResultDTO.requstSuccess(totalInfos);
    }

}
