package de.langomatisch.infiniterplace.api.pubsub;

public interface PubSubListener<T extends IPacket> {

    String channel();

    void onMessage(String channel, T message);

}
