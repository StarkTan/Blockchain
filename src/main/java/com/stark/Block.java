package com.stark;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 区块模型
 */
@Data
public class Block implements Serializable {

    //区块哈希值
    private String hash;

    //前一个区块哈希值
    private String preHash;

    //区块数据
    private String data;

    //时间戳
    private long timestamp;

    //工作量证明计时器
    private long nonce;

    private Block(String preHash, String data, long timestamp) {
        this.preHash = preHash;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static Block newBlock(String preHash, String data) {
        Block block = new Block(preHash, data, Instant.now().getEpochSecond());
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        return block;
    }
}
