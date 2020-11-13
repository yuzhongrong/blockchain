package com.blockchain.server.tron.inner;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.WalletChangeDTO;
import com.blockchain.common.base.dto.WalletOrderDTO;
import com.blockchain.server.tron.service.ITronWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Harvey
 * @date 2019/2/19 18:09
 * @user WIN10
 */
@Api(TronTransferInnerApi.TRON_WALLET_API)
@RestController
@RequestMapping("/inner/walletTx")
public class TronTransferInnerController {
    private static final Logger LOG = LoggerFactory.getLogger(TronTransferInnerController.class);
    @Autowired
    private ITronWalletService tronWalletService;

    @ApiOperation(value = TronTransferInnerApi.Order.MATHOD_API_NAME, notes = TronTransferInnerApi.Order.MATHOD_API_NOTE)
    @PostMapping("/order")
    public ResultDTO order(@ApiParam(TronTransferInnerApi.Order.MATHOD_API_WALLET_ORDER_DTO) @RequestBody WalletOrderDTO walletOrderDTO) {
        LOG.info("冻结、解冻余额，walletOrderDTO：" + walletOrderDTO.toString());
        tronWalletService.updateWalletOrder(walletOrderDTO);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TronTransferInnerApi.Change.MATHOD_API_NAME, notes = TronTransferInnerApi.Change.MATHOD_API_NOTE)
    @PostMapping("/change")
    public ResultDTO change(@ApiParam(TronTransferInnerApi.Change.MATHOD_API_WALLET_CHANGE_DTO) @RequestBody WalletChangeDTO walletChangeDTO) {
        LOG.info("扣除、增加，walletChangeDTO：" + walletChangeDTO.toString());
        tronWalletService.handleWalletChange(walletChangeDTO);
        return ResultDTO.requstSuccess();
    }

}
