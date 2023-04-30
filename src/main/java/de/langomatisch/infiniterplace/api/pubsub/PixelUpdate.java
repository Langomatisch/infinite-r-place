package de.langomatisch.infiniterplace.api.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@RedisPacket("pixel-update")
public class PixelUpdate {

    private int x;
    private int y;
    private int material;

}
