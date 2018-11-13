package com.stark;

import com.stark.transaction.Transaction;
import com.stark.utils.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

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

    //区块数据:交易信息
    private Transaction[] transactions;

    //时间戳
    private long timestamp;

    //工作量证明计时器
    private long nonce;

    private Block(String preHash, Transaction[] transactions, long timestamp) {
        this.preHash = preHash;
        this.transactions = transactions;
        this.timestamp = timestamp;
    }

    public static Block newBlock(String preHash, Transaction[] transactions) {
        Block block = new Block(preHash, transactions, Instant.now().getEpochSecond());
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        return block;
    }

    //对交易进行hash计算
    public byte[] hashTransaction() {
        byte[][] txIdArrays = new byte[getTransactions().length][];
        for (int i = 0; i < transactions.length; i++) {
            txIdArrays[i] = transactions[i].getTxId();
        }
        return DigestUtils.sha256(ByteUtils.meger(txIdArrays));
    }
}
