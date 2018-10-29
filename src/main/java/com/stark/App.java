package com.stark;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {

        BlockChain blockChain = new BlockChain();
        blockChain.addBlock("Block1".getBytes());
        blockChain.addBlock("Block2".getBytes());
        System.out.println(blockChain.toString());
        System.out.println(Arrays.toString(args));
    }
}
