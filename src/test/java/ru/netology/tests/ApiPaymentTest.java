package ru.netology.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.ApiHelper;
import ru.netology.data.DataHelper.CardInfo;
import ru.netology.data.DBHelper;
import ru.netology.data.DataHelper;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class ApiPaymentTest {

    @BeforeEach
    void setUp() {
        DBHelper.cleanDatabase();
    }

    @Test
    void shouldApprovePaymentViaApi() {
        CardInfo validCard = DataHelper.getApprovedCardInfo();

        Response response = ApiHelper.requestPayment(validCard);

        response.then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));

        assertEquals("APPROVED", DBHelper.getLastPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentViaApi() {
        CardInfo declinedCard = DataHelper.getDeclinedCardInfo();

        Response response = ApiHelper.requestPayment(declinedCard);

        response.then()
                .statusCode(200)
                .body("status", equalTo("DECLINED"));

        assertEquals("DECLINED", DBHelper.getLastPaymentStatus());
    }

    @Test
    void shouldApproveCreditViaApi() {
        CardInfo validCard = DataHelper.getApprovedCardInfo();

        Response response = ApiHelper.requestCredit(validCard);

        response.then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));

        assertEquals("APPROVED", DBHelper.getLastCreditStatus());
    }

    @Test
    void shouldDeclineCreditViaApiWithDeclinedCard() {
        CardInfo declinedCard = DataHelper.getDeclinedCardInfo();

        Response response = ApiHelper.requestCredit(declinedCard);

        response.then()
                .statusCode(200)
                .body("status", equalTo("DECLINED"));

        assertEquals("DECLINED", DBHelper.getLastCreditStatus());
    }
}
