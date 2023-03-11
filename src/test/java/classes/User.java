package classes;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonProperty;

public class User {
    private String username;
    private String password;
    @JsonAlias( {"api_key"} )
    private String apiKey;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getApiKey() {
        return apiKey;
    }

}
