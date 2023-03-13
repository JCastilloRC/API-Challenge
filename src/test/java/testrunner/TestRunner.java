package testrunner;
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

    @Test(groups = "ListCreation")
    @Description("Create a new movie list successfully")
    @Feature("Lists")
    public void createListHappyPath() throws IOException {
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
    public void addMovieHappyPath(){
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
    @Test(groups = "ViewDetails",dependsOnGroups = {"AddMovie"} )
    @Description("Getting the details of a movieList successfully ")
    @Feature("Lists")
    public void getListDetailsHappyPath(){
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
                equalTo(movieList.getMovies().get(0).getId())
        );
    }
    @Test(dependsOnGroups = {"AddMovie"} )
    @Description("Trying to get the details of a nonexistent list")
    @Feature("Lists")
    public void getListDetailsSadPath(){
        LOGGER.info("Sending a GET request to movie list details with empty ID (not created)");
        Response response = errorMovieList.getDetails(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }
    @Test(groups = "ClearList",dependsOnGroups = {"ViewDetails"} )
    @Description("Clearing a list successfully")
    @Feature("Lists")
    public void clearListHappyPath(){
        LOGGER.info("Sending a POST request to clear movie list with ID '" + movieList.getId()+"'");
        Response response = movieList.clearList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was updated successfully.")
        );
    }
    @Test(dependsOnGroups = {"ViewDetails"} )
    @Description("Trying to clear a nonexistent list")
    @Feature("Lists")
    public void clearListSadPath(){
        LOGGER.info("Sending a GET request to movie list details with empty ID (not created)");
        Response response = errorMovieList.clearList(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }
    @Test(dependsOnGroups = {"ClearList"} )
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
    @Test(dependsOnGroups = {"ClearList"} )
    @Description("Trying to delete a movie list unsuccessfully")
    @Feature("Lists")
    public void deleteListSadPath(){
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
    @Test
    @Description("Getting the details of a movie")
    @Feature("Movies")
    public void getMovieDetailsHappyPath(){
        LOGGER.info("Sending a GET request for details of movie '" + movie.getName()+"'");
        Response response = movie.getDetails(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(200)
        );
        assertThat("The movie name doesn't match",
                response.jsonPath().getString("title"),
                equalTo(movie.getName())
        );
        assertThat("The movie ID doesn't match",
                response.jsonPath().getInt("id"),
                equalTo(movie.getId())
        );
    }
    @Test
    @Description("Trying to get the details of a nonexistent movie")
    @Feature("Movies")
    public void getMovieDetailsSadPath(){
        LOGGER.info("Sending a GET request for details of movie '" + movie.getName()+"'");
        Response response = errorMovie.getDetails(session);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }
    @Test
    @Description("Rating a movie with 9.5 score")
    @Feature("Movies")
    public void ratingMovieHappyPath(){
        LOGGER.info("Sending a POST request for rating movie '" + movie.getName()+"' 9.5/10");
        Response response = movie.rateMovie(session, 9.5F);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was updated successfully.")
        );
    }
    @Test
    @Description("Trying to rate a movie with invalid score")
    @Feature("Movies")
    public void ratingMovieSadPath(){
        LOGGER.info("Sending a POST request for rating movie '" + movie.getName()+"' -10/10");
        Response response = movie.rateMovie(session, -10);
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(400)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("Value too low: Value must be greater than 0.0.")
        );
    }
}
