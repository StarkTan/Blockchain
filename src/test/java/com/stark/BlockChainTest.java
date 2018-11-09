package com.stark;

public class BlockChainTest {

    public static void main(String[] args) {
        BlockChain blockChain = BlockChain.newBlockChain();
        blockChain.addBlock("Send 1 BTC to Ivan");
        blockChain.addBlock("Send 2 more BTC to Ivan");
        for (BlockChain.BlockChainIterator iterator = blockChain.getBlockChainIterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            System.out.println("PreHash: " + block.getPreHash());
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());

            ProofOfWork proofOfWork = ProofOfWork.newProofOfWork(block);
            System.out.println("Pow valid: " + proofOfWork.validate() + "\n");
        }
    }
}
