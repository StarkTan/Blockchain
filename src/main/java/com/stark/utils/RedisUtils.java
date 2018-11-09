package com.stark.utils;

import com.stark.Block;
import redis.clients.jedis.Jedis;

/**
 * key - value Redis存储
 */
public class RedisUtils {

    /**
     * 区块桶前缀
     */
    private static final String BLOCKS_BUCKET_PREFIX = "blocks_";

    private volatile static RedisUtils instance;

    public static RedisUtils getInstance() {
        if (instance == null) {
            synchronized (RedisUtils.class) {
                if (null == instance) {
                    instance = new RedisUtils();
                }
            }
        }
        return instance;
    }

    private Jedis jedis;

    private RedisUtils() {
        jedis = new Jedis("localhost");
    }

    /**
     * 放入最新的区块Hash值
     */
    public void putLastBlockHash(String lastHash) {
        jedis.set(SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX + "1"),
                SerializeUtils.serialize(lastHash));
    }

    /**
     * 查询最新的区块
     *
     * @return
     */
    public String getLastBlockHash() {
        byte[] lastBlockHashBytes = jedis.get(SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX + "1"));
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    //保存区块
    public void putBlock(Block block) {
        jedis.set(SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX + block.getHash()),
                SerializeUtils.serialize(block));
    }

    //获取区块
    public Block getBlock(String blockHash) {
        byte[] key = SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX + blockHash);
        return (Block) SerializeUtils.deserialize(jedis.get(key));
    }
}
