package com.stark;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 工作量计算结果
 */
@Data
@AllArgsConstructor
public class PowResult {

    //计数器
    private long nonce;

    //hash 值
    private String hash;

}
