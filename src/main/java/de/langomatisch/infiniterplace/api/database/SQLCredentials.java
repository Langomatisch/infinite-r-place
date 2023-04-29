package de.langomatisch.infiniterplace.api.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SQLCredentials {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;


}
