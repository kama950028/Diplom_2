package praktikum.tests.stellarburgers.tests;


import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import praktikum.tests.stellarburgers.BaseTest;
import praktikum.tests.stellarburgers.client.OrderClient;
import praktikum.tests.stellarburgers.client.UserClient;
import praktikum.tests.stellarburgers.model.User;
import praktikum.tests.stellarburgers.util.Data;
import praktikum.tests.stellarburgers.util.Ingredients;
import praktikum.tests.stellarburgers.util.Tokens;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderCreateTests extends BaseTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessTokenToCleanup;

    @After
    public void cleanup() {
        if (accessTokenToCleanup != null) {
            userClient.delete(accessTokenToCleanup).statusCode(anyOf(is(200), is(202), is(401)));
        }
    }

    private String ensureUserAndGetToken() {
        User u = User.of(Data.email(), Data.pass(), Data.name());
        ValidatableResponse r = userClient.register(u).statusCode(200);
        String token = Tokens.extractAccessToken(r.extract().asString());
        accessTokenToCleanup = token;
        return token;
    }

    @Test
    @Story("Positive: Create order with auth")
    @Description("с авторизацией + с ингредиентами")
    public void shouldCreateOrderWithAuthAndIngredientsTest() {
        String token = ensureUserAndGetToken();
        List<String> ids = Ingredients.anyIds(2);

        orderClient.createWithAuth(token, ids)
                .statusCode(200)               // успех
                .contentType(ContentType.JSON)
                .body("success", is(true))
                .body("order.number", notNullValue()); // успешный пример ответа из доки :contentReference[oaicite:9]{index=9}
    }

    @Test
    @Story("Negative: Create order without auth")
    @Description("без авторизации")
    public void shouldFailCreateOrderWithoutAuthTest() {
        List<String> ids = Ingredients.anyIds(2);

        // По документации: без авторизации сервер возвращает 401 и JSON-ответ
        // {"success": false, "message": "You should be authorised"}
        orderClient.createNoAuth(ids)
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Story("Negative: Create order without ingredients")
    @Description("без ингредиентов")
    public void shouldFailCreateOrderWithoutIngredientsTest() {
        String token = ensureUserAndGetToken();

        orderClient.createWithAuth(token, Collections.emptyList())
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided")); // 400 и это сообщение :contentReference[oaicite:11]{index=11}
    }

    @Test
    @Story("Negative: Create order with wrong ingredient hash")
    @Description("с неверным хешем ингредиентов")
    public void shouldFailCreateOrderWithWrongHashTest() {
        {
            String token = ensureUserAndGetToken();
            List<String> wrongIds = Arrays.asList("invalid_hash_1", "invalid_hash_2");

            orderClient.createWithAuth(token, wrongIds)
                    .statusCode(500);
        }
    }
}

