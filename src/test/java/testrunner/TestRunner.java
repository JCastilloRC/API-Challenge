package testrunner;
import classes.MovieList;
import classes.Movie;
import helper.Helper;
import helper.TestNGListener;
import hooks.Hooks;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@Listeners(TestNGListener.class)
public class TestRunner extends Hooks {
    private static final Logger LOGGER = LogManager.getLogger("TestRunner");
    private static final String NEW_LIST_BODY_PATH = "testdata\\newlist.json";
    private static final String MOVIE_PATH = "testdata\\movie.json";
    private MovieList movieList;
    private Movie movie;

    @Test(groups = "ListCreation")
    @Description("Create a new movie list successfully")
    @Feature("Lists")
    public void createListHappyPath() throws IOException {
        movieList = Helper.parseListJson(NEW_LIST_BODY_PATH);
        LOGGER.info("Sending a POST request to create a movie list with body: \n" + Helper.getBodyFromFile(NEW_LIST_BODY_PATH));
        Response response = movieList.createList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was created successfully.")
        );
        LOGGER.info("Movie list created with id: " + movieList.getId());
    }
    @Test
    @Description("Trying to create a new movieList unsuccessfully")
    @Feature("Lists")
    public void createListSadPath(){
        MovieList errorMovieList = new MovieList();
        LOGGER.info("Sending a POST request to create a movie list with empty body fields");
        Response response = errorMovieList.createList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(422)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("Invalid parameters: Your request parameters are incorrect.")
        );
    }
    @Test(groups = "AddMovie",dependsOnGroups = "ListCreation")
    @Description("Adding movie to a movie list successfully")
    @Feature("Lists")
    public void addMovieHappyPath() throws IOException {
        movie = Helper.parseMovieJson(MOVIE_PATH);
        LOGGER.info("Sending a POST request to add movie '"+movie.getName()+"' with id '" +movie.getId() +"'");
        LOGGER.info("The target movie list has id '"+ movieList.getId()+"'");
        Response response = movieList.addMovie(session, movie);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was updated successfully.")
        );
    }
    @Test(dependsOnGroups = "ListCreation")
    @Description("Trying do add a nonexistent movie to a movie list")
    @Feature("Lists")
    public void addMovieSadPath() {
        Movie errorMovie = new Movie();
        LOGGER.info("Sending a POST request to add an nonexistent movie (empty id) ");
        LOGGER.info("The target movie list has id '"+ movieList.getId()+"'");
        Response response = movieList.addMovie(session, errorMovie);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }
    @Test(groups = "ViewDetails",dependsOnGroups = {"ListCreation", "AddMovie"} )
    @Description("Getting the details of a movieList successfully ")
    @Feature("Lists")
    public void getListDetailsHappyPath() throws IOException {
        LOGGER.info("Sending a GET request to movie list details with id '" + movieList.getId() +"'");
        Response response = movieList.getDetails(session);
        List <Movie> moviesFromList = response.jsonPath().getList("items", Movie.class);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(200)
        );
        assertThat("Wrong 'created by'",
                response.jsonPath().getString("created_by"),
                equalTo(session.getCurrentUser().getUsername())
        );
        assertThat("Wrong list name",
                response.jsonPath().getString("name"),
                equalTo(movieList.getName())
        );
        assertThat("Wrong list description",
                response.jsonPath().getString("description"),
                equalTo(movieList.getDescription())
        );
        assertThat("No movie was added to the list",
                moviesFromList.size(),
                greaterThan(0)
        );
        assertThat("Movie ID from list does not match to movie previously added",
                moviesFromList.get(0).getId(),
                equalTo(movie.getId())
        );
    }
    @Test(dependsOnGroups = {"ListCreation", "ViewDetails"} )
    @Description("Delete a movieList successfully")
    @Feature("Lists")
    public void deleteListHappyPath(){
        LOGGER.info("Sending a DELETE request for movie list with ID: " + movieList.getId());
        Response response = movieList.deleteList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was updated successfully.")
        );
    }
    @Test(dependsOnGroups = {"ListCreation", "ViewDetails"} )
    @Description("Trying to delete a movie list unsuccessfully")
    @Feature("Lists")
    public void deleteListSadPath(){
        MovieList errorMovieList = new MovieList();LOGGER.info("- - - - - - - > START TEST CASE 'Delete movieList sad path'");
        LOGGER.info("Sending a DELETE request for movie list with empty ID (not yet created)");
        Response response = errorMovieList.deleteList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }
}
