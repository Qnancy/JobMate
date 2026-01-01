package cn.edu.zju.cs.jobmate.utils.generators;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Snowflake ID Generator.
 */
@Slf4j
public class SnowflakeIdGenerator {

    // Worker ID (0~31) distinguishes different machines.
    private final long workerId;

    // Datacenter ID (0~31) distinguishes different data centers.
    private final long datacenterId;

    // Sequence number (0~4095) within the same millisecond to avoid ID collisions.
    private long sequence = 0L;

    // Custom epoch (Twitter's epoch).
    private final long twepoch = 1288834974657L;

    // Number of bits allocated for worker ID.
    private final long workerIdBits = 5L;

    // Number of bits allocated for datacenter ID.
    private final long datacenterIdBits = 5L;

    // Maximum worker ID value.
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // Maximum datacenter ID value.
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // Number of bits allocated for sequence number.
    private final long sequenceBits = 12L;

    // Shift for worker ID.
    private final long workerIdShift = sequenceBits;

    // Shift for datacenter ID.
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // Shift for timestamp.
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // Mask for sequence number.
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // Last timestamp when an ID was generated.
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Snowflake generator worker id out of range");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Snowflake generator datacenter id out of range");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * Generate the next Snowflake ID.
     * 
     * @return next unique Snowflake ID
     * @throws BusinessException if system clock moves backwards
     */
    public synchronized long nextId() throws BusinessException {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            log.error("Clock moved backwards, refusing to generate id for {} milliseconds", lastTimestamp - timestamp);
            throw new BusinessException(ErrorCode.CLOCK_MOVE_BACKWARD);
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                while ((timestamp = System.currentTimeMillis()) <= lastTimestamp) {}
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }
}
