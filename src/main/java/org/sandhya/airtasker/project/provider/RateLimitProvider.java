package org.sandhya.airtasker.project.provider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

public class RateLimitProvider extends BaseRateLimitProvider {
    Logger logger = LoggerFactory.getLogger(RateLimitProvider.class);

    Jedis redisClient;

    @Value("${max_allowed:12}")
    public int MAX_ALLOWED;

    @Value("${rate.interval.sec:3600}")
    public int RATE_INTERVAL_SEC;

    @Value("$rate.limit.algo:hour")
    public String RATE_LIMIT_ALGO;

    @Value("${redis.host:localhost}")
    public String REDIS_HOST;

    @Value("${redis.port:6379}")
    public int REDIS_PORT;


    @PostConstruct
    public void postConstruct() {
        logger.debug(REDIS_HOST + " :: " + REDIS_PORT);
        redisClient = new Jedis(REDIS_HOST, REDIS_PORT);
    }
    @Override
    public long isCallPermitted(String key) {
        long sec = LocalDateTime.now().getSecond();
        long currentTSSec = System.currentTimeMillis()/1000L;
        long currentTS = 0;
        // to capture the window of duration
        long diff = 0;

        if (RATE_LIMIT_ALGO.equals("seconds")) {
            currentTS = currentTSSec ;
            diff  = currentTS - RATE_INTERVAL_SEC - 1;

        } else {
            // Get the current minute timestamp to create a sliding counter.
            currentTS = currentTSSec - sec;
            diff = currentTS - RATE_INTERVAL_SEC;
        }

        // variable to determine when to try next
        long windowStart = currentTS ;
        int totalCountThisInterval = 0;
        logger.debug("interval --> " + RATE_INTERVAL_SEC);
        logger.debug("api key ---> " + key);
        logger.debug("max allowed --->" + MAX_ALLOWED);


        redisClient.watch(key);
        Transaction t = redisClient.multi();

        Response<Map<String, String>> hashMap = t.hgetAll(key);
        t.exec();
        t = redisClient.multi();
        Map<String, String> hMap =  hashMap.get();

        for (String field : hMap.keySet()){
            // find all fields which are outside this given interval
            // if found, delete it.

            if ( Long.valueOf(field) <  diff ) {
                logger.debug("deleting field :::" + field);
                t.hdel(key, field);
            } else {
                windowStart = Math.min(windowStart, Long.valueOf(field));
                // add the total usage so far in the given interval window
                totalCountThisInterval += Integer.valueOf(hMap.get(field));
            }

        }

        logger.info("Total requests made in the interval " + totalCountThisInterval);
        if (totalCountThisInterval > MAX_ALLOWED) {
            return ( (windowStart + RATE_INTERVAL_SEC ) - currentTS);
        }

        // if the usage is within the acceptable limit, increment value of existing key
        // or create a new key with a value of 1.
        t.hincrBy(key, String.valueOf(currentTS), 1);
        List<Object> resp =  t.exec();
        if (resp != null) {
            logger.debug("successful transaction");
            return 0;
        } else {
            logger.debug("failed transaction");
            return -1;
        }
    }
}
