package ru.netology.data;

import java.util.HashMap;
import java.util.Map;
import ru.netology.data.DataHelper.CardInfo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ApiHelper {

    private static final String BASE_URL = "http://localhost:8080";

    private ApiHelper() {
    }

    private static Map<String, String> buildRequestBody(CardInfo card) {
        Map<String, String> body = new HashMap<>();
        body.put("number", card.getNumber());
        body.put("month", card.getMonth());
        body.put("year", card.getYear());
        body.put("owner", card.getOwner());
        body.put("cvc", card.getCvc());
        return body;
    }

    public static Response requestPayment(CardInfo card) {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(buildRequestBody(card))
                .when()
                .post("api/v1/pay");
    }

    public static Response requestCredit(CardInfo card) {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(buildRequestBody(card))
                .when()
                .post("/api/v1/credit");
    }
}
