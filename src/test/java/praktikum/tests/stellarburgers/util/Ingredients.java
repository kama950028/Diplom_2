package praktikum.tests.stellarburgers.util;


import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;

import java.util.List;

import static io.restassured.RestAssured.given;

public class Ingredients {
    @Step("Fetch any {howMany} ingredient ids")
    public static List<String> anyIds(int howMany) {
        String json = given().when().get("/ingredients")
                .then().statusCode(200).extract().asString(); // GET /api/ingredients
        List<String> all = JsonPath.from(json).getList("data._id");
        return all.subList(0, Math.min(howMany, all.size()));
    }
}
