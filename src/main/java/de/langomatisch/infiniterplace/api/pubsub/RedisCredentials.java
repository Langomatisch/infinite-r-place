package de.langomatisch.infiniterplace.api.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisCredentials {

    private String host;
    private int port;
    private String password;

}
