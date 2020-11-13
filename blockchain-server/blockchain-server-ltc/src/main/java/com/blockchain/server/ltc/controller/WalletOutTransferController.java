package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.ltc.common.constants.OutTxConstants;
import com.blockchain.server.ltc.controller.api.WalletOutTxApi;
import com.blockchain.server.ltc.service.IWalletTransferService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(WalletOutTxApi.API_NAME)
@RestController
@RequestMapping("/outTx")
public class WalletOutTransferController extends BaseController {
    @Autowired
    IWalletTransferService walletTransferService;

    @ApiOperation(value = WalletOutTxApi.Check.METHOD_TITLE_NAME, notes = WalletOutTxApi.Check.METHOD_TITLE_NOTE)
    @PostMapping("/check")
    public ResultDTO check(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.CHECK);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    /**
     * 查询内部充值记录
     * @param paramsDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = WalletOutTxApi.FindInternalRecord.METHOD_TITLE_NAME, notes =
            WalletOutTxApi.FindInternalRecord.METHOD_TITLE_NOTE)
    @PostMapping("/findInternalRecord")
    public ResultDTO findInternalRecord( @ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                                         @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                         @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        return walletTransferService.findInternalRecord(paramsDTO);

    }

    @ApiOperation(value = WalletOutTxApi.Recheck.METHOD_TITLE_NAME, notes =
            WalletOutTxApi.Recheck.METHOD_TITLE_NOTE)
    @PostMapping("/recheck")
    public ResultDTO recheck(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.RECHECK);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.Pack.METHOD_TITLE_NAME, notes = WalletOutTxApi.Pack.METHOD_TITLE_NOTE)
    @PostMapping("/pack")
    public ResultDTO pack(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                          @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                          @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.PACK);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.Success.METHOD_TITLE_NAME, notes =
            WalletOutTxApi.Success.METHOD_TITLE_NOTE)
    @PostMapping("/success")
    public ResultDTO success(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.SUCCESS);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.Error.METHOD_TITLE_NAME, notes = WalletOutTxApi.Error.METHOD_TITLE_NOTE)
    @PostMapping("/error")
    public ResultDTO error(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.ERROR);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.Reject.METHOD_TITLE_NAME, notes =
            WalletOutTxApi.Reject.METHOD_TITLE_NOTE)
    @PostMapping("/reject")
    public ResultDTO reject(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                            @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                            @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        paramsDTO.setTxStatus(OutTxConstants.REJECT);
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.All.METHOD_TITLE_NAME, notes = WalletOutTxApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(WalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                         @ApiParam(WalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(WalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = walletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletOutTxApi.SelectById.METHOD_TITLE_NAME,
            notes = WalletOutTxApi.SelectById.METHOD_TITLE_NOTE)
    @GetMapping("/selectById")
    public ResultDTO selectById(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id) {
        WalletTxDTO inTransfer = walletTransferService.findOutTransfer(id);
        return ResultDTO.requstSuccess(inTransfer);
    }

    @ApiOperation(value = WalletOutTxApi.CheckPass.METHOD_TITLE_NAME,
            notes = WalletOutTxApi.CheckPass.METHOD_TITLE_NOTE)
    @PostMapping("/checkPass")
    public ResultDTO checkPass(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        walletTransferService.updateStatus(id,OutTxConstants.RECHECK);
        return  ResultDTO.requstSuccess();
    }

    @ApiOperation(value = WalletOutTxApi.RecheckPass.METHOD_TITLE_NAME,
            notes = WalletOutTxApi.RecheckPass.METHOD_TITLE_NOTE)
    @PostMapping("/recheckPass")
    public ResultDTO recheckPass(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        walletTransferService.handleOut(id,OutTxConstants.PACK);
//        walletTransferService.updateStatus(id,OutTxConstants.PACK);
        return  ResultDTO.requstSuccess();
    }

    @ApiOperation(value = WalletOutTxApi.GoReject.METHOD_TITLE_NAME,
            notes = WalletOutTxApi.GoReject.METHOD_TITLE_NOTE)
    @PostMapping("/goReject")
    public ResultDTO goReject(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        walletTransferService.handReject(id);
        return  ResultDTO.requstSuccess();
    }


//    @ApiOperation(value = WalletOutTxApi.PackError.METHOD_TITLE_NAME,
//            notes = WalletOutTxApi.PackError.METHOD_TITLE_NOTE)
//    @PostMapping("/packError")
//    public ResultDTO packError(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
//        walletTransferService.handPackError(id);
//        return  ResultDTO.requstSuccess();
//    }
//
//    @ApiOperation(value = WalletOutTxApi.PackSuccess.METHOD_TITLE_NAME,
//            notes = WalletOutTxApi.PackSuccess.METHOD_TITLE_NOTE)
//    @PostMapping("/packSuccess")
//    public ResultDTO packSuccess(@ApiParam(WalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
//        walletTransferService.handPackSuccess(id);
//        return  ResultDTO.requstSuccess();
//    }

}
