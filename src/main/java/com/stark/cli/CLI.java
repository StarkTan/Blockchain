package com.stark.cli;

import com.stark.BlockChain;
import com.stark.transaction.TXOutput;
import com.stark.transaction.Transaction;

public class CLI {
    //查询钱包余额
    private void getBalance(String address) {
        BlockChain blockchain = BlockChain.newBlockChain(address);
        TXOutput[] txOutputs = blockchain.findUTXO(address);
        int balance = 0;
        if (txOutputs != null && txOutputs.length > 0) {
            for (TXOutput txOutput : txOutputs) {
                balance += txOutput.getValue();
            }
        }
        System.out.printf("Balance of '%s': %d\n", address, balance);
    }

    //转账
    private void send(String from, String to, int amount) throws Exception {
        BlockChain blockchain = BlockChain.newBlockChain(from);
        Transaction transaction = Transaction.newUTXOTransaction(from, to, amount, blockchain);
        blockchain.mineBlock(new Transaction[]{transaction});
        System.out.println("Success!");
    }

    public static void main(String[] args) throws Exception {
        CLI cli = new CLI();
        cli.getBalance("user1");
        cli.getBalance("user2");
        cli.send("user1", "user2", 10);
        //cli.send("user2", "user1", 5);

        cli.getBalance("user1");
        cli.getBalance("user2");
    }
}
