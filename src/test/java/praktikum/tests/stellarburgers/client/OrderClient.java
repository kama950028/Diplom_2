package praktikum.tests.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.tests.stellarburgers.model.OrderRequest;

import java.util.List;


import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDERS = "/orders";

    @Step("Create order with auth and {ingredientIds.size} ingredients")
    public ValidatableResponse createWithAuth(String accessToken, List<String> ingredientIds) {
    return given()
            .header("Authorization", normalizeToken(accessToken))
            .body(OrderRequest.of(ingredientIds))
            .when().post(ORDERS)
            .then();
    }

    @Step("Create order without auth and {ingredientIds.size} ingredients")
    public ValidatableResponse createNoAuth(List<String> ingredientIds) {
        return given()
                .body(OrderRequest.of(ingredientIds))
                .when().post(ORDERS)
                .then();
    }


    private String normalizeToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token : "Bearer " + token;
    }
}

