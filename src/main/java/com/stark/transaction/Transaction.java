package com.stark.transaction;

import com.stark.BlockChain;
import com.stark.utils.SerializeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * 交易
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {

    private static final int SUBSIDY = 10;

    //交易的HashId
    private byte[] txId;

    //交易输入
    private TXInput[] inputs;

    //交易输出
    private TXOutput[] outputs;

    public static Transaction newUTXOTransaction(String from, String to, int amount, BlockChain blockchain) throws Exception {
        SpendableOutputResult result = blockchain.findSpendableOutputs(from, amount);
        int accumulated = result.getAccumulated();
        Map<String, int[]> unspentOuts = result.getUnspentOuts();

        if (accumulated < amount) {
            throw new Exception("ERROR: Not enough funds");
        }
        Iterator<Map.Entry<String, int[]>> iterator = unspentOuts.entrySet().iterator();

        TXInput[] txInputs = {};
        while (iterator.hasNext()) {
            Map.Entry<String, int[]> entry = iterator.next();
            String txIdStr = entry.getKey();
            int[] outIdxs = entry.getValue();
            byte[] txId = Hex.decodeHex(txIdStr);
            for (int outIndex : outIdxs) {
                txInputs = ArrayUtils.add(txInputs, new TXInput(txId, outIndex, from));
            }
        }

        TXOutput[] txOutput = {};
        txOutput = ArrayUtils.add(txOutput, new TXOutput(amount, to));
        if (accumulated > amount) {
            txOutput = ArrayUtils.add(txOutput, new TXOutput((accumulated - amount), from));
        }

        Transaction newTx = new Transaction(null, txInputs, txOutput);
        newTx.setTxId();
        return newTx;
    }

    private void setTxId() {
        this.setTxId(DigestUtils.sha256(SerializeUtils.serialize(this)));
    }

    /**
     * 创建交易
     *
     * @param to   收钱的钱包地址
     * @param data 解锁脚本数据
     */
    public static Transaction newCoinbaseTX(String to, String data) {

        if (StringUtils.isBlank(data)) {
            data = String.format("Reward to %s", to);
        }
        TXInput txInput = new TXInput(new byte[]{}, -1, data);
        TXOutput txOutput = new TXOutput(SUBSIDY, to);
        Transaction tx = new Transaction(null, new TXInput[]{txInput}, new TXOutput[]{txOutput});
        tx.setTxId();
        return tx;
    }

    /**
     * 是否为 Coinbase 交易
     */
    public boolean isCoinbase() {
        return this.getInputs().length == 1
                && this.getInputs()[0].getTxId().length == 0
                && this.getInputs()[0].getTxOutputIndex() == -1;
    }

}
