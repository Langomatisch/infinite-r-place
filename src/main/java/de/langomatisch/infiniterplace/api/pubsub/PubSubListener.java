package de.langomatisch.infiniterplace.api.pubsub;

public interface PubSubListener<T> {


    void onMessage(String channel, T message);

}
