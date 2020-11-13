package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.ltc.controller.api.TotalApi;
import com.blockchain.server.ltc.dto.TotalInfoDTO;
import com.blockchain.server.ltc.service.IClearingTotalService;
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
public class ClearingTotalController {
    @Autowired
    IClearingTotalService clearingTotalService;

    @ApiOperation(value = TotalApi.All.METHOD_TITLE_NAME, notes = TotalApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all(@ApiParam(TotalApi.All.METHOD_API_USERID) @RequestParam(value = "userId") String userId) {
        List<TotalInfoDTO> totalInfos = clearingTotalService.selectInfoAll(userId);
        return ResultDTO.requstSuccess(totalInfos);
    }

    @ApiOperation(value = TotalApi.Save.METHOD_TITLE_NAME, notes = TotalApi.Save.METHOD_TITLE_NOTE)
    @PostMapping("/save")
    public ResultDTO save(@ApiParam(TotalApi.Save.METHOD_API_TOTALID) @RequestParam(value = "totalId") String totalId,
                          @ApiParam(TotalApi.Save.METHOD_API_AMOUNT) @RequestParam(value = "amount") BigDecimal amount) {
        clearingTotalService.updateCorr(totalId, amount);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TotalApi.Total.METHOD_TITLE_NAME, notes = TotalApi.Total.METHOD_TITLE_NOTE)
    @PostMapping("/total")
    public ResultDTO total(@ApiParam(TotalApi.Total.METHOD_API_USERID) @RequestParam(value = "userId") String userId) {
        List<TotalInfoDTO> totalInfos = clearingTotalService.insertTotals(userId);
        return ResultDTO.requstSuccess(totalInfos);
    }

}
