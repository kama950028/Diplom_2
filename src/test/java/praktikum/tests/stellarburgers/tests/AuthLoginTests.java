package praktikum.tests.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.extension.ExtendWith;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Tokens;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@ExtendWith(AllureJunit5.class)
public class AuthLoginTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanup;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.of(Data.email(), Data.pass(), Data.name());
        accessTokenToCleanup = Tokens.extractAccessToken(
                userClient.register(testUser).statusCode(SC_OK).extract().asString()
        );
    }

    @AfterEach
    void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup)
                    .statusCode(anyOf(is(SC_OK), is(SC_ACCEPTED), is(SC_UNAUTHORIZED)));
        }
    }

    @Test
    @DisplayName("Успешный вход с корректными данными")
    @Story("Positive: Successful login")
    @Description("Успешный вход под существующим пользователем")
    void shouldLoginExistingUserTest() {
        userClient.login(testUser.email, testUser.password)
                .statusCode(SC_OK)
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("user.email", equalTo(testUser.email));
    }

    @Test
    @DisplayName("Ошибка входа с неверным email")
    @Story("Negative: Wrong email")
    @Description("Попытка входа с неверным email")
    void shouldFailLoginWithWrongEmailTest() {
        userClient.login("wrong_" + testUser.email, testUser.password)
                .statusCode(SC_UNAUTHORIZED)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Ошибка входа с неверным паролем")
    @Story("Negative: Wrong password")
    @Description("Попытка входа с неверным паролем")
    void shouldFailLoginWithWrongPasswordTest() {
        userClient.login(testUser.email, "wrong_" + testUser.password)
                .statusCode(SC_UNAUTHORIZED)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
