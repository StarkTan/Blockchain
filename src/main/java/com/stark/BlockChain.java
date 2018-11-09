package com.stark;

import com.stark.utils.RedisUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 区块链模型
 */
public class BlockChain {

    @Getter
    private String lastBlockHash;

    private BlockChain(String lastBlockHash) {
        this.lastBlockHash = lastBlockHash;
    }

    private void addBlock(Block block) {
        RedisUtils.getInstance().putLastBlockHash(block.getHash());
        RedisUtils.getInstance().putBlock(block);
        this.lastBlockHash = block.getHash();
    }

    /**
     * 添加区块数据
     */
    public void addBlock(String data) throws RuntimeException {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            throw new RuntimeException("Rail to add block into blockchain");
        }
        addBlock(Block.newBlock(lastBlockHash, data));
    }

    /**
     * 创建创世区块
     */
    private static Block newGenesisBlock() {
        return Block.newBlock("", "Genesis Block");
    }

    public static BlockChain newBlockChain() {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Block genesisBlock = newGenesisBlock();
            lastBlockHash = genesisBlock.getHash();
            RedisUtils.getInstance().putBlock(genesisBlock);
            RedisUtils.getInstance().putLastBlockHash(lastBlockHash);
        }
        return new BlockChain(lastBlockHash);
    }

    public BlockChainIterator getBlockChainIterator() {
        return new BlockChainIterator(lastBlockHash);
    }

    /**
     * 区块链迭代器
     */
    public class BlockChainIterator {
        private String currentBlockHash;

        public BlockChainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        public boolean hasNext() {
            if (StringUtils.isBlank(currentBlockHash)) {
                return false;
            }
            Block lastBlock = RedisUtils.getInstance().getBlock(currentBlockHash);
            return lastBlock != null;
        }

        public Block next() {
            Block currentBlock = RedisUtils.getInstance().getBlock(currentBlockHash);
            if (currentBlock != null) {
                this.currentBlockHash = currentBlock.getPreHash();
                return currentBlock;
            } else {
                return null;
            }
        }
    }
}
