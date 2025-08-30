package praktikum.tests.stellarburgers;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.RestAssured.responseSpecification;

public class BaseTest {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    static {
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
        requestSpecification = req;

//        ResponseSpecification resp = new ResponseSpecBuilder()
//                .expectContentType(ContentType.JSON)
//                .build();
//        responseSpecification = resp;
    }
}
