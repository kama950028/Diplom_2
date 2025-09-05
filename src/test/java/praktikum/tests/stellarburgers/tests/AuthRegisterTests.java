package praktikum.tests.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Tokens;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(AllureJunit5.class)
public class AuthRegisterTests extends BaseTest {
    private final UserClient userClient = new UserClient();

    // Храним последнего "кандидата" на удаление.
    // В тестах просто присваиваем, токен не трогаем.
    private User lastCreatedUser;

    @AfterEach
    void cleanup() {
        if (lastCreatedUser == null) {
            return;
        }
        try {
            // Пытаемся залогиниться и взять токен уже здесь.
            // Если юзер реально создавался — логин вернёт токен.
            String token = Tokens.extractAccessToken(
                    userClient.login(lastCreatedUser.email, lastCreatedUser.password)
                            .extract()
                            .asString()
            );

            if (token != null && !token.isBlank()) {
                userClient.delete(token)
                        .statusCode(anyOf(is(SC_OK), is(SC_ACCEPTED), is(SC_UNAUTHORIZED)));
            }
        } catch (Throwable ignored) {
            // Негативные сценарии (пустой пароль/емейл, дубль и т.п.) могут
            // не создавать юзера — логин упадёт, это нормально.
        } finally {
            lastCreatedUser = null;
        }
    }

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    @Story("Positive: Successful registration")
    @Description("Создать уникального пользователя")
    void shouldRegisterUniqueUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        lastCreatedUser = u;

        userClient.register(u)
                .statusCode(SC_OK)
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("user.email", equalTo(u.email));

        // Никаких Tokens.extractAccessToken(...) здесь — всё сделает @AfterEach.
    }

    @Test
    @DisplayName("Ошибка регистрации: пользователь уже существует")
    @Story("Negative: Already registered")
    @Description("Создать пользователя, который уже зарегистрирован")
    void shouldFailOnDuplicateUserTest() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        lastCreatedUser = u;

        // первый раз успешно создаём
        userClient.register(u)
                .statusCode(SC_OK);

        // второй раз получаем ошибку дубля
        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));

        // Токен не извлекаем — cleanup сам залогинится и удалит созданного юзера.
    }

    @Test
    @DisplayName("Ошибка регистрации: пустой email")
    @Story("Negative: Missing required field")
    @Description("Регистрация с пустым email")
    void shouldFailOnMissingEmailTest() {
        User u = User.of("", Data.pass(), Data.name());
        lastCreatedUser = u; // безопасно: если не создался, логин не пройдёт — просто ничего не удалим

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
        lastCreatedUser = u;

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
        lastCreatedUser = u;

        userClient.register(u)
                .statusCode(SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
