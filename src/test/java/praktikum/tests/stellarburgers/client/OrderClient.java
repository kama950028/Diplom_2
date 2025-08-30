package praktikum.tests.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDERS = "/orders";

    @Step("Create order with auth and {ingredientIds.size} ingredients")
    public ValidatableResponse createWithAuth(String accessToken, List<String> ingredientIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);        // Jackson сделает ["id1","id2"]
        return given()
                .header("Authorization", normalizeToken(accessToken))
                .body(body)
                .when().post(ORDERS)
                .then();
    }

    @Step("Create order without auth and {ingredientIds.size} ingredients")
    public ValidatableResponse createNoAuth(List<String> ingredientIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);
        return given()
                .body(body)
                .when().post(ORDERS)
                .then();
    }

    private String normalizeToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token : "Bearer " + token;
    }
}

