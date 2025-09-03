package praktikum.tests.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Tokens;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@ExtendWith(AllureJunit5.class)
public class AuthRegisterTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanup;

    @AfterEach
    void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup)
                    .statusCode(anyOf(is(SC_OK), is(SC_ACCEPTED), is(SC_UNAUTHORIZED)));
        }
    }

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    @Story("Positive: Successful registration")
    @Description("Создать уникального пользователя")
    void shouldRegisterUniqueUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());

        ValidatableResponse r = userClient.register(u)
                .statusCode(SC_OK)
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("user.email", equalTo(u.email));

        accessTokenToCleanup = Tokens.extractAccessToken(r.extract().asString());
    }

    @Test
    @DisplayName("Ошибка регистрации: пользователь уже существует")
    @Story("Negative: Already registered")
    @Description("Создать пользователя, который уже зарегистрирован")
    void shouldFailOnDuplicateUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        accessTokenToCleanup = Tokens.extractAccessToken(
                userClient.register(u).statusCode(SC_OK).extract().asString()
        );

        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Ошибка регистрации: пустой email")
    @Story("Negative: Missing required field")
    @Description("Регистрация с пустым email")
    void shouldFailOnMissingEmailTest() {
        User u = User.of("", Data.pass(), Data.name());

        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка регистрации: пустой пароль")
    @Story("Negative: Missing required field")
    @Description("Регистрация с пустым паролем")
    void shouldFailOnMissingPasswordTest() {
        User u = User.of(Data.email(), "", Data.name());

        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка регистрации: пустое имя")
    @Story("Negative: Missing required field")
    @Description("Регистрация с пустым именем")
    void shouldFailOnMissingNameTest() {
        User u = User.of(Data.email(), Data.pass(), "");

        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
