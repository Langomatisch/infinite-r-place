package de.langomatisch.infiniterplace.api.pubsub;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RedisPacket {

    String value();

}
