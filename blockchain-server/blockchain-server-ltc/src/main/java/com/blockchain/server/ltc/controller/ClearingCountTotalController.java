package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.ltc.controller.api.TotalApi;
import com.blockchain.server.ltc.dto.CountTotalInfoDTO;
import com.blockchain.server.ltc.service.IClearingCountTotalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@RestController
@RequestMapping("/countTotal")
public class ClearingCountTotalController {
    @Autowired
    IClearingCountTotalService clearingCountTotalService;

    @ApiOperation(value = TotalApi.All.METHOD_TITLE_NAME, notes = TotalApi.All.METHOD_TITLE_NOTE)
    @GetMapping("/all")
    public ResultDTO all() {
        List<CountTotalInfoDTO> totalInfos = clearingCountTotalService.selectInfoAll();
        return ResultDTO.requstSuccess(totalInfos);
    }


    @ApiOperation(value = TotalApi.Total.METHOD_TITLE_NAME, notes = TotalApi.Total.METHOD_TITLE_NOTE)
    @PostMapping("/total")
    public ResultDTO total() {
        List<CountTotalInfoDTO> totalInfos = clearingCountTotalService.insertTotals();
        return ResultDTO.requstSuccess(totalInfos);
    }

}
