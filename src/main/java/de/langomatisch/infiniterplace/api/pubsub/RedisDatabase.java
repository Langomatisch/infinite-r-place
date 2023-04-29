package de.langomatisch.infiniterplace.api.pubsub;

public class RedisDatabase {

    public void publish(String channel, String message) {

    }

    public void publish(String channel, Object type) {

    }

    public <T extends IPacket> void subscribe(String channel, PubSubListener<T> listener) {

    }

    public void unsubscribe(String channel) {

    }

}
