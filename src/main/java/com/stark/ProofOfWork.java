package com.stark;

import com.stark.utils.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

/**
 * 工作量证明
 */
@Data
public class ProofOfWork {

    //难度目标位
    public static final int TARGET_BITS = 20;

    //区块
    private Block block;

    //难度目标值
    private BigInteger target;

    private ProofOfWork(Block block, BigInteger target) {
        this.block = block;
        this.target = target;
    }

    /**
     * 创建新的工作量证明，设定难度目标值
     */
    public static ProofOfWork newProofOfWork(Block block) {
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft(256 - TARGET_BITS);
        return new ProofOfWork(block, targetValue);
    }

    public PowResult run() {
        long nonce = 0;
        String shaHex = "";
        System.out.printf("Mining the block containing: %s \n", block.getData());
        long startTime = System.currentTimeMillis();
        while (nonce < Long.MAX_VALUE) {
            byte[] data = prepareData(nonce);
            shaHex = DigestUtils.sha256Hex(data);
            if (new BigInteger(shaHex, 16).compareTo(target) < 0) {
                System.out.printf("Elapsed Time: %s seconds \n", (float) (System.currentTimeMillis() - startTime));
                System.out.printf("correct hash Hex: %s \n\n", shaHex);
                break;
            } else {
                nonce++;
            }
        }
        return new PowResult(nonce, shaHex);
    }

    public boolean validate() {
        byte[] data = prepareData(block.getNonce());
        return new BigInteger(DigestUtils.sha256Hex(data), 16).compareTo(target) < 0;
    }

    /**
     * 准备数据，注意要先进行byte转换
     */
    private byte[] prepareData(long nonce) {
        byte[] preHashBytes = {};
        if (StringUtils.isNoneBlank(getBlock().getPreHash())) {
            preHashBytes = new BigInteger(getBlock().getPreHash(), 16).toByteArray();
        }
        return ByteUtils.meger(
                preHashBytes,
                getBlock().getData().getBytes(),
                ByteUtils.toBytes(getBlock().getTimestamp()),
                ByteUtils.toBytes(TARGET_BITS),
                ByteUtils.toBytes(nonce)
        );
    }
}
