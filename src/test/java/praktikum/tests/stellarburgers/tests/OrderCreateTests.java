package praktikum.tests.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.OrderClient;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Ingredients;
import praktikum.tests.stellarburgers.util.Tokens;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;


@ExtendWith(AllureJunit5.class)
public class OrderCreateTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();

    private String accessToken;
    private String accessTokenToCleanup;

    @BeforeEach
    void setUp() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        ValidatableResponse r = userClient.register(u).statusCode(SC_OK);
        accessToken = Tokens.extractAccessToken(r.extract().asString());
        accessTokenToCleanup = accessToken;
    }

    @AfterEach
    void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup)
                    .statusCode(anyOf(is(SC_OK), is(SC_ACCEPTED), is(SC_UNAUTHORIZED)));
        }
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией")
    @Story("Positive: Create order with auth")
    @Description("Создание заказа с авторизацией и валидными ингредиентами")
    void shouldCreateOrderWithAuthAndIngredientsTest() {
        List<String> ids = Ingredients.anyIds(2);

        orderClient.createWithAuth(accessToken, ids)
                .statusCode(SC_OK)
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Ошибка создания заказа без авторизации")
    @Story("Negative: Create order without auth")
    @Description("Попытка создать заказ без авторизации")
    void shouldFailCreateOrderWithoutAuthTest() {
        List<String> ids = Ingredients.anyIds(2);

        orderClient.createNoAuth(ids)
                .statusCode(SC_UNAUTHORIZED)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка создания заказа: без ингредиентов (с авторизацией)")
    @Story("Negative: Create order without ingredients")
    @Description("Попытка создать заказ с авторизацией, но без ингредиентов")
    void shouldFailCreateOrderWithAuthWithoutIngredientsTest() {
        orderClient.createWithAuth(accessToken, List.of())
                .statusCode(SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка создания заказа: неверный хэш ингредиента (с авторизацией)")
    @Story("Negative: Create order with invalid ingredient hash")
    @Description("Попытка создать заказ с авторизацией и некорректным id ингредиента")
    void shouldFailCreateOrderWithInvalidIngredientHashTest() {
        List<String> invalidIds = List.of("ffffffffffffffffffffffff");

        orderClient.createWithAuth(accessToken, invalidIds)
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .contentType(ContentType.JSON)
                .body("success", is(false));
    }


}
