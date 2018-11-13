package com.stark;

import com.stark.transaction.SpendableOutputResult;
import com.stark.transaction.TXInput;
import com.stark.transaction.TXOutput;
import com.stark.transaction.Transaction;
import com.stark.utils.RedisUtils;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 区块链模型
 */
public class BlockChain {

    @Getter
    private String lastBlockHash;

    private BlockChain(String lastBlockHash) {
        this.lastBlockHash = lastBlockHash;
    }

    //从Redis中获取chain
    public static BlockChain initBlockchainFromDB() throws Exception {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new Exception("ERROR: Fail to init blockchain from db. ");
        }
        return new BlockChain(lastBlockHash);
    }

    //打包交易进行挖矿
    public void mineBlock(Transaction[] transactions) throws Exception {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new Exception("ERROR: Fail to get last block hash ! ");
        }
        Block block = Block.newBlock(lastBlockHash, transactions);
        addBlock(block);
    }

    private void addBlock(Block block) {
        RedisUtils.getInstance().putLastBlockHash(block.getHash());
        RedisUtils.getInstance().putBlock(block);
        this.lastBlockHash = block.getHash();
    }

    /**
     * 添加区块数据
     */
    public void addBlock(Transaction[] transactions) throws RuntimeException {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            throw new RuntimeException("Rail to add block into blockchain");
        }
        addBlock(Block.newBlock(lastBlockHash, transactions));
    }

    /**
     * 创建创世区块
     */
    private static Block newGenesisBlock(Transaction coinbase) {
        return Block.newBlock("", new Transaction[]{coinbase});
    }

    public static BlockChain newBlockChain(String address) {
        String lastBlockHash = RedisUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Transaction cionbaseTX = Transaction.newCoinbaseTX(address, "");
            Block genesisBlock = newGenesisBlock(cionbaseTX);
            lastBlockHash = genesisBlock.getHash();
            RedisUtils.getInstance().putBlock(genesisBlock);
            RedisUtils.getInstance().putLastBlockHash(lastBlockHash);
        }
        return new BlockChain(lastBlockHash);
    }

    public BlockChainIterator getBlockChainIterator() {
        return new BlockChainIterator(lastBlockHash);
    }

    //查找钱包地址对应的所有未花费的交易
    private Transaction[] findUnSpentTxs(String address) {
        Map<String, int[]> allSpentTXOs = getAllSpentTXOs(address);

        Transaction[] unSpentTxs = {};

        for (BlockChainIterator blockChainIterator = getBlockChainIterator(); blockChainIterator.hasNext(); ) {
            Block block = blockChainIterator.next();
            for (Transaction transaction : block.getTransactions()) {
                String txId = Hex.encodeHexString(transaction.getTxId());
                int[] spentOutIndexArray = allSpentTXOs.get(txId);
                for (int outIndex = 0; outIndex < transaction.getOutputs().length; outIndex++) {
                    if (spentOutIndexArray != null && ArrayUtils.contains(spentOutIndexArray, outIndex)) {
                        continue;
                    }
                    //保存不存在allSpentTXOs 中的交易
                    if (transaction.getOutputs()[outIndex].canBeUnlockedWith(address)) {
                        unSpentTxs = ArrayUtils.add(unSpentTxs, transaction);
                    }
                }
            }
        }
        return unSpentTxs;
    }

    /**
     * 寻找能够花费的交易
     *
     * @param address 钱包地址
     * @param amount  花费金额
     */
    public SpendableOutputResult findSpendableOutputs(String address, int amount) throws Exception {
        Transaction[] unspentTXs = findUnSpentTxs(address);
        int accumulated = 0;
        Map<String, int[]> unspentOuts = new HashMap<>();
        for (Transaction tx : unspentTXs) {

            String txId = Hex.encodeHexString(tx.getTxId());

            for (int outId = 0; outId < tx.getOutputs().length; outId++) {

                TXOutput txOutput = tx.getOutputs()[outId];

                if (txOutput.canBeUnlockedWith(address) && accumulated < amount) {
                    accumulated += txOutput.getValue();

                    int[] outIds = unspentOuts.get(txId);
                    if (outIds == null) {
                        outIds = new int[]{outId};
                    } else {
                        outIds = ArrayUtils.add(outIds, outId);
                    }
                    unspentOuts.put(txId, outIds);
                    if (accumulated >= amount) {
                        break;
                    }
                }
            }
        }
        return new SpendableOutputResult(accumulated, unspentOuts);
    }

    //从交易输入中查询区块链中所有已被花费的交易输出
    private Map<String, int[]> getAllSpentTXOs(String address) {
        Map<String, int[]> spentTXOs = new HashMap<>();
        for (BlockChainIterator blockChainIterator = getBlockChainIterator(); blockChainIterator.hasNext(); ) {
            Block block = blockChainIterator.next();
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.isCoinbase()) {
                    continue;
                }
                for (TXInput txInput : transaction.getInputs()) {
                    if (txInput.canUnlockOutputWith(address)) {
                        String inTxId = Hex.encodeHexString(txInput.getTxId());
                        int[] spentOutIndexArray = spentTXOs.get(inTxId);
                        if (spentOutIndexArray == null) {
                            spentTXOs.put(inTxId, new int[]{txInput.getTxOutputIndex()});
                        } else {
                            spentOutIndexArray = ArrayUtils.add(spentOutIndexArray, txInput.getTxOutputIndex());
                            spentTXOs.put(inTxId, spentOutIndexArray);
                        }
                    }
                }
            }
        }
        return spentTXOs;
    }

    //查询钱包地址的所有UTXO
    public TXOutput[] findUTXO(String address) {
        Transaction[] unspentTxs = findUnSpentTxs(address);
        TXOutput[] utxos = {};
        if (unspentTxs == null || unspentTxs.length == 0) {
            return utxos;
        }
        for (Transaction tx : unspentTxs) {
            for (TXOutput txOutput : tx.getOutputs()) {
                if (txOutput.canBeUnlockedWith(address)) {
                    utxos = ArrayUtils.add(utxos, txOutput);
                }
            }
        }
        return utxos;
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
