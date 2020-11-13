package com.blockchain.server.ltc.inner;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.WalletChangeDTO;
import com.blockchain.common.base.dto.WalletOrderDTO;

import com.blockchain.server.ltc.inner.api.WalletTransferInnerApi;
import com.blockchain.server.ltc.service.IWalletTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(WalletTransferInnerApi.MARKET_CONTROLLER_API)
@RestController
@RequestMapping("/inner/walletTx")
public class WalletTransferInner {
    private static final Logger LOG = LoggerFactory.getLogger(WalletTransferInner.class);
    @Autowired
    private IWalletTransferService walletTransferService;

    @ApiOperation(value = WalletTransferInnerApi.Order.METHOD_API_NAME, notes = WalletTransferInnerApi.Order.METHOD_API_NOTE)
    @PostMapping("/order")
    public ResultDTO order(@ApiParam(WalletTransferInnerApi.Order.WALLETORDERDTO) @RequestBody WalletOrderDTO walletOrderDTO) {
        LOG.info("冻结、解冻余额，walletOrderDTO：" + walletOrderDTO.toString());
        return ResultDTO.requstSuccess(walletTransferService.handleOrder(walletOrderDTO));
    }

    @ApiOperation(value = WalletTransferInnerApi.Change.METHOD_API_NAME, notes = WalletTransferInnerApi.Change.METHOD_API_NOTE)
    @PostMapping("/change")
    public ResultDTO change(@ApiParam(WalletTransferInnerApi.Change.WALLETCHANGEDTO) @RequestBody WalletChangeDTO walletChangeDTO) {
        LOG.info("扣除、增加，walletChangeDTO：" + walletChangeDTO.toString());
        return ResultDTO.requstSuccess(walletTransferService.handleChange(walletChangeDTO));
    }

}
