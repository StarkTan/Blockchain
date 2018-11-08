package com.stark;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 区块链模型
 */
public class BlockChain {

    @Getter
    private List<Block> blockList = new LinkedList<>();

    private void addBlock(Block block) {
        blockList.add(block);
    }

    /**
     * 添加区块数据
     */
    public void addBlock(String data) {
        Block preBlock = blockList.get(blockList.size() - 1);
        addBlock(Block.newBlock(preBlock.getHash(), data));
    }

    /**
     * 创建创世区块
     */
    private static Block newGenesisBlock() {
        return Block.newBlock("", "Genesis Block");
    }

    public static BlockChain newBlockChain() {
        BlockChain blockChain = new BlockChain();
        blockChain.addBlock(newGenesisBlock());
        return blockChain;
    }


}
