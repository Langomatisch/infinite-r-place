package de.langomatisch.infiniterplace.api.pubsub;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisDatabase {

    private static final Gson GSON = new Gson();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Jedis jedis;

    public RedisDatabase(RedisCredentials redisCredentials) {
        jedis = new Jedis(redisCredentials.getHost(), redisCredentials.getPort(), 2000);//
        jedis.auth(redisCredentials.getPassword());
        jedis.connect();
    }

    public void publish(Object message) {
        executorService.submit(() -> {
            String channelFromPacket = getChannelFromPacket(message.getClass());
            if (channelFromPacket == null) throw new IllegalArgumentException("Packet is not annotated with @RedisPacket");
            System.out.println("publishing to " + channelFromPacket + " " + GSON.toJson(message));
            jedis.publish(channelFromPacket, GSON.toJson(message));
        });
    }

    public void publish(String channel, Object type) {
        jedis.publish(channel, GSON.toJson(type));
    }

    public <T> void subscribe(Class<T> tClass, PubSubListener<T> listener) {
        executorService.submit(() -> {
            String channelFromPacket = getChannelFromPacket(tClass);
            System.out.println("got channel " + channelFromPacket);
            if (channelFromPacket == null)
                throw new IllegalArgumentException("Packet is not annotated with @RedisPacket");
            jedis.subscribe(new JedisPubSub() {

                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("got message " + message);
                    listener.onMessage(channel, GSON.fromJson(message, tClass));
                }
            }, channelFromPacket);
        });
    }

    private String getChannelFromPacket(Class<?> iPacket) {
        System.out.println("trying to get channel from " + iPacket.getName());
        if (!iPacket.isAnnotationPresent(RedisPacket.class)) return null;
        RedisPacket annotation = iPacket.getAnnotation(RedisPacket.class);
        if (annotation == null) return null;
        return annotation.value();
    }

}
