package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.ltc.common.constants.InTxConstants;
import com.blockchain.server.ltc.controller.api.WalletInTxApi;
import com.blockchain.server.ltc.service.IWalletTransferService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 充值记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(WalletInTxApi.API_NAME)
@RestController
@RequestMapping("/inTx")
public class WalletInTransferController extends BaseController {
    @Autowired
    IWalletTransferService walletTransferService;

    @ApiOperation(value = WalletInTxApi.Success.METHOD_TITLE_NAME, notes = WalletInTxApi.Success.METHOD_TITLE_NOTE)
    @PostMapping("/success")
    public ResultDTO success(@ApiParam(WalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(WalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(WalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(InTxConstants.SUCCESS);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletInTxApi.Error.METHOD_TITLE_NAME, notes = WalletInTxApi.Error.METHOD_TITLE_NOTE)
    @PostMapping("/error")
    public ResultDTO error(@ApiParam(WalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(WalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(WalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(InTxConstants.ERROR);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletInTxApi.All.METHOD_TITLE_NAME, notes = WalletInTxApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(WalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                         @ApiParam(WalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(WalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletInTxApi.selectById.METHOD_TITLE_NAME,
            notes = WalletInTxApi.selectById.METHOD_TITLE_NOTE)
    @GetMapping("/selectById")
    public ResultDTO selectById(@ApiParam(WalletInTxApi.selectById.METHOD_API_ID) @RequestParam("id") String id) {
        WalletTxDTO inTransfer = walletTransferService.findInTransfer(id);
        return ResultDTO.requstSuccess(inTransfer);
    }
}
