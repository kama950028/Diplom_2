package praktikum.tests.stellarburgers.tests;


import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Tokens;

import static org.hamcrest.Matchers.*;

public class AuthRegisterTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanup;

    @After
    public void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup).statusCode(anyOf(is(200), is(202), is(401)));
        }
    }

    @Test
    @Story("Positive: Successful registration")
    @Description("создать уникального пользователя")
    public void shouldRegisterUniqueUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());

        ValidatableResponse r = userClient.register(u)
                .statusCode(200) // успех регистрации
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("user.email", equalTo(u.email)); // дока: success + user + токены :contentReference[oaicite:4]{index=4}

        accessTokenToCleanup = Tokens.extractAccessToken(r.extract().asString());
    }

    @Test
    @Story("Negative: Already registered")
    @Description("создать пользователя, который уже зарегистрирован")
    public void shouldFailOnDuplicateUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        accessTokenToCleanup = Tokens.extractAccessToken(userClient.register(u).statusCode(200).extract().asString());

        userClient.register(u)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists")); // 403 с этим текстом :contentReference[oaicite:5]{index=5}
    }

    @Test
    @Story("Negative: Missing required field")
    @Description("создать пользователя и не заполнить одно из обязательных полей")
    public void shouldFailOnMissingRequiredFieldTest() {
        User u = User.of("", Data.pass(), Data.name()); // пустой email

        userClient.register(u)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields")); // 403 с этим текстом :contentReference[oaicite:6]{index=6}
    }
}

