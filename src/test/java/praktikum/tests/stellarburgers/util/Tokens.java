package praktikum.tests.stellarburgers.util;


import io.restassured.path.json.JsonPath;

public class Tokens {
    public static String extractAccessToken(String respBody) {
        String token = JsonPath.from(respBody).getString("accessToken");
        return token == null ? null : token;
    }
}

