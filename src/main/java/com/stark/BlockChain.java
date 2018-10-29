package com.stark;

import com.stark.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

//区块链
public class BlockChain {

    //这里暂时只用一个List就OK
    private List<Block> blocks;

    public BlockChain() {
        blocks = new ArrayList<>();
        //添加初始块
        blocks.add(new Block("Genesis Block".getBytes(), new byte[]{}));
    }

    public void addBlock(byte[] data) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("data can't be empty");
        }
        Block preBlock = blocks.get(blocks.size() - 1);
        blocks.add(new Block(data, preBlock.getHash()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append("================\n");
            sb.append("data : ").append(new String(block.getData())).append("\n");
            sb.append("preHash : ").append(ByteUtils.byte2Hex(block.getPrevBlockHash())).append("\n");
            sb.append("hash : ").append(ByteUtils.byte2Hex(block.getHash())).append("\n");
        }
        sb.append("length : ").append(blocks.size());
        return sb.toString();
    }
}
