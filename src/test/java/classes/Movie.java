package classes;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

    @JsonProperty
    @JsonAlias({"title"})
    private String name;
    @JsonProperty
    private int id;

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
}
