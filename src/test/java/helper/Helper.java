package helper;

import classes.Movie;
import classes.MovieList;
import classes.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Helper {

    public static User parseUserJson(String jsonPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), User.class);
    }
    public static MovieList parseListJson(String jsonPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), MovieList.class);
    }
    public static Movie parseMovieJson(String jsonPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), Movie.class);
    }
    public static String getBodyFromFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }
}
