package com.stark;

import com.stark.utils.SHA256Util;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 区块
 */
@Getter
public class Block {
    private long timestamp; // 区块创建的时间

    private byte[] data; //区块储存的有效信息

    private byte[] prevBlockHash; //上一个区块的哈希值

    private byte[] hash; //当前块的Hash块

    public void setHash() {
        byte[] time = (timestamp + "").getBytes();
        byte[] target = new byte[time.length + data.length + prevBlockHash.length];
        int index = 0;
        System.arraycopy(time, 0, target, index, time.length);
        index += time.length;
        System.arraycopy(prevBlockHash, 0, target, index, prevBlockHash.length);
        index += prevBlockHash.length;
        System.arraycopy(data, 0, target, index, data.length);
        hash = SHA256Util.getSHA256Bytes(target);
    }

    public Block(byte[] data, byte[] prevBlockHash) {
        if (data == null || prevBlockHash == null || data.length == 0) {
            throw new RuntimeException("error block!");
        }
        this.data = data;
        this.prevBlockHash = prevBlockHash;
        this.timestamp = new Date().getTime();
        setHash();
    }
}
