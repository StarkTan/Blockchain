package com.stark;

import com.stark.transaction.TXInput;
import com.stark.transaction.TXOutput;
import com.stark.transaction.Transaction;
import com.stark.utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class BlockChainTest {

    public static void main(String[] args) {

        BlockChain blockChain = BlockChain.newBlockChain("user1");
        //blockChain.addBlock(new Transaction[]{new Transaction(new byte[]{1, 2}, new TXInput[]{},new TXOutput[]{new TXOutput(10, "13123user1")})});
        //blockChain.addBlock("Send 2 more BTC to Ivan");
        for (BlockChain.BlockChainIterator iterator = blockChain.getBlockChainIterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            System.out.println("PreHash: " + block.getPreHash());
            System.out.println("Data: " + Arrays.toString(block.getTransactions()));
            System.out.println("Hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());

            ProofOfWork proofOfWork = ProofOfWork.newProofOfWork(block);
            System.out.println("Pow valid: " + proofOfWork.validate() + "\n");
        }
        //new Jedis("localhost").flushAll();
    }
}
