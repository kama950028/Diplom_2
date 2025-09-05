package praktikum.tests.stellarburgers;

import io.qameta.allure.junit5.AllureJunit5;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.requestSpecification;


@ExtendWith(AllureJunit5.class) //
public class BaseTest {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    static {
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured()) // шаги HTTP в отчёт
                .build();
        requestSpecification = req;
    }
}
