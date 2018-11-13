package com.stark.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交易
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TXInput implements Serializable {
    //交易ID的hash值
    private byte[] txId;

    //交易输出索引,(一笔交易含有多笔交易输出)
    private int txOutputIndex;

    //解锁脚本：验证交易输出中需要的数据
    private String scriptSig;

    //判断解锁脚本能否解锁交易
    public boolean canUnlockOutputWith(String unlockingData) {
        return scriptSig.endsWith(unlockingData);
    }
}
