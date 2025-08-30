package praktikum.tests.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.tests.stellarburgers.model.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String REGISTER = "/auth/register";
    private static final String LOGIN    = "/auth/login";
    private static final String USER     = "/auth/user";

    @Step("Register user {user.email}")
    public ValidatableResponse register(User user) {
        return given().body(user).when().post(REGISTER).then();
    }

    @Step("Login user {email}")
    public ValidatableResponse login(String email, String password) {
        return given()
                .body("{\"email\":\""+email+"\",\"password\":\""+password+"\"}")
                .when().post(LOGIN).then();
    }

    @Step("Delete user via access token")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .header("Authorization", accessToken) // "Bearer ..."
                .when().delete(USER).then();
    }
}

