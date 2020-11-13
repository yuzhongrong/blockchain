package com.blockchain.server.eth.controller;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.eth.common.constants.StatusConstants;
import com.blockchain.server.eth.entity.EthParadrop;
import com.blockchain.server.eth.entity.EthParadropAddr;
import com.blockchain.server.eth.service.IEthParadropAddrService;
import com.blockchain.server.eth.service.IEthParadropService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/paradrop")
public class ParadropController extends BaseController {
    @Autowired
    IEthParadropAddrService ethParadropAddrService;
    @Autowired
    IEthParadropService ethParadropService;

    @GetMapping("/list")
    public ResultDTO walletInList(@RequestParam(value = "pageNum", defaultValue = BaseConstant.PAGE_DEFAULT_INDEX, required = false) Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = BaseConstant.PAGE_DEFAULT_SIZE, required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EthParadropAddr> list = ethParadropAddrService.list();
        for (EthParadropAddr row:list) {
            row.setSendPk(null);
        }
        return generatePage(list);
    }

    @GetMapping("/info")
    public ResultDTO info() {
        EthParadrop paradrop = ethParadropService.getInfo();
        paradrop.setId(null);
        paradrop.setSendRatio(null);
        return ResultDTO.requstSuccess(paradrop);
    }

    @PostMapping("/updateInfo")
    public ResultDTO updateInfo(BigDecimal sendCount, BigDecimal sendAmount) {
        EthParadrop paradrop = ethParadropService.updateInfo(sendCount, sendAmount);
        return ResultDTO.requstSuccess();
    }

    @GetMapping("/open")
    public ResultDTO open() {
        ethParadropService.updateStatus(StatusConstants.Y);
        return ResultDTO.requstSuccess();
    }

    @GetMapping("/colse")
    public ResultDTO colse() {
        ethParadropService.updateStatus(StatusConstants.N);
        return ResultDTO.requstSuccess();
    }

    @GetMapping("/openAddr")
    public ResultDTO openAddr(String addr) {
        ethParadropAddrService.updateStatusAndDesc(addr, StatusConstants.Y, null);
        return ResultDTO.requstSuccess();
    }

    @GetMapping("/colseAddr")
    public ResultDTO colseAddr(String addr) {
        ethParadropAddrService.updateStatusAndDesc(addr, StatusConstants.N, null);
        return ResultDTO.requstSuccess();
    }

    @PostMapping("/insertAddr")
    public ResultDTO insertAddr(String addr, String pk) {
        ethParadropAddrService.inserWallet(addr, pk);
        return ResultDTO.requstSuccess();
    }

    @GetMapping("/delAddr")
    public ResultDTO delAddr(String addr) {
        ethParadropAddrService.delectAddr(addr);
        return ResultDTO.requstSuccess();
    }
}
