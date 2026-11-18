package ru.netology.tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper.CardInfo;
import ru.netology.data.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.pages.MainPage;
import ru.netology.pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    private MainPage mainPage;
    private PaymentPage paymentPage;

    @BeforeEach
    void setUp() {
        Configuration.timeout = 15000;
        DBHelper.cleanDatabase();
        open("http://localhost:8080");
        mainPage = new MainPage();
        paymentPage = new PaymentPage();
        mainPage.verifyPageIsOpen();
    }


    @Test
    void shouldBuyTourWithApprovedCard() {
        CardInfo validCard = DataHelper.getApprovedCardInfo();
        mainPage.clickBuyButton();
        paymentPage.fillForm(validCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastPaymentStatus());
        assertEquals(4_500_000L, DBHelper.getLastPaymentAmount());
    }

    @Test
    void shouldBuyTourWithZeroCvc() {
        CardInfo validCard = DataHelper.getZeroCvcCardInfo();
        mainPage.clickBuyButton();
        paymentPage.fillForm(validCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastPaymentStatus());
    }

    @Test
    void shouldBuyTourOnCreditWithApprovedCard() {
        CardInfo validCard = DataHelper.getApprovedCardInfo();
        mainPage.clickBuyCreditButton();
        paymentPage.fillForm(validCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastCreditStatus());
        // В кредитной форме таблица payment_entity должна быть пуста
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldBuyTourOnCreditWithZeroCvc() {
        CardInfo validCard = DataHelper.getZeroCvcCardInfo();
        mainPage.clickBuyCreditButton();
        paymentPage.fillForm(validCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastCreditStatus());
    }

    // ==========================================
    //            DECLINED (ОТКАЗ БАНКА)
    // ==========================================

    @Test
    void shouldDeclinePaymentWithDeclinedCard() {
        CardInfo declinedCard = DataHelper.getDeclinedCardInfo();
        mainPage.clickBuyButton();
        paymentPage.fillForm(declinedCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowErrorNotification();
        assertEquals("DECLINED", DBHelper.getLastPaymentStatus());
    }

    @Test
    void shouldDeclineCreditWithDeclinedCard() {
        CardInfo declinedCard = DataHelper.getDeclinedCardInfo();
        mainPage.clickBuyCreditButton();
        paymentPage.fillForm(declinedCard);
        paymentPage.clickContinue();

        paymentPage.shouldShowErrorNotification();
        assertEquals("DECLINED", DBHelper.getLastCreditStatus());
    }
}