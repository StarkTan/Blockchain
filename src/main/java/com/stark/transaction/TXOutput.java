package com.stark.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交易输出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TXOutput implements Serializable {
    //数值
    private int value;

    //锁定脚本
    private String scriptPubKey;

    //判断解锁脚本能否解锁交易
    public boolean canBeUnlockedWith(String unlockingData) {
        return scriptPubKey.endsWith(unlockingData);
    }

}
