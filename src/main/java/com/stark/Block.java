package com.stark;

import com.stark.utils.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.time.Instant;

/**
 * 区块模型
 */
@Data
public class Block {

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


    private Block(String hash, String preHash, String data, long timestamp) {
        this.hash = hash;
        this.preHash = preHash;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static Block newBlock(String preHash, String data) {
        Block block = new Block("", preHash, data, Instant.now().getEpochSecond());
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        return block;
    }

    private void setHash() {
        byte[] preHashBytes = {};
        if (StringUtils.isNoneBlank(preHash)) {
            preHashBytes = new BigInteger(preHash, 16).toByteArray();
        }
        byte[] headers = ByteUtils.meger(preHashBytes, data.getBytes(), ByteUtils.toBytes(timestamp));
        this.setHash(DigestUtils.sha256Hex(headers));
    }
}
