package praktikum.tests.stellarburgers.tests;


import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Test;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Tokens;
import io.qameta.allure.junit4.AllureJunit4;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;


public class AuthLoginTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanup;

    @After
    public void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup).statusCode(anyOf(is(200), is(202), is(401)));
        }
    }

    @Test
    @Story("Positive: Successful login")
    @Description("вход под существующим пользователем")
    public void shouldLoginExistingUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        accessTokenToCleanup = Tokens.extractAccessToken(userClient.register(u).statusCode(200).extract().asString());

        userClient.login(u.email, u.password)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("user.email", equalTo(u.email)); // успешный логин возвращает пользователя и токены :contentReference[oaicite:7]{index=7}
    }

    @Test
    @Story("Negative: Wrong credentials")
    @Description("вход с неверным логином и паролем")
    public void shouldFailLoginWithWrongCredsTest() {
        userClient.login("wrong_"+Data.email(), "wrong_"+Data.pass())
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect")); // 401 и это сообщение :contentReference[oaicite:8]{index=8}
    }
}
