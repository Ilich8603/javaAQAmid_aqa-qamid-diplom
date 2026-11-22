package ru.netology.tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper.CardInfo;
import ru.netology.data.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.pages.MainPage;
import ru.netology.pages.PaymentPage;

import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {

    private PaymentPage paymentPage;

    @BeforeEach
    void setUp() {
        DBHelper.cleanDatabase();
        open("http://localhost:8080");

        MainPage mainPage = new MainPage();
        mainPage.verifyPageIsOpen();
        paymentPage = mainPage.clickBuyButton();
    }

    private void submitForm(CardInfo card) {
        paymentPage.fillForm(card);
        paymentPage.clickContinue();
    }

    //            ПУСТЫЕ ПОЛЯ

    @Test
    void shouldShowErrorWhenAllFieldsEmpty() {
        CardInfo emptyCard = CardInfo.builder()
                .number("").month("").year("").owner("").cvc("").build();
        submitForm(emptyCard);

        // Проверяем, что ошибки вывелись и в БД пусто
        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount(), "В БД не должно быть записей");
    }

    @Test
    void shouldShowErrorWhenCardNumberEmpty() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder().number("").build();
        submitForm(card);
        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenMonthEmpty() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder().month("").build();
        submitForm(card);
        paymentPage.shouldShowMonthError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenYearEmpty() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder().year("").build();
        submitForm(card);
        paymentPage.shouldShowYearError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenOwnerEmpty() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder().owner("").build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Поле обязательно для заполнения");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenCvcEmpty() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder().cvc("").build();
        submitForm(card);
        paymentPage.shouldShowCvcError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    //            ВАЛИДАЦИЯ НОМЕРА КАРТЫ

    @Test
    void shouldShowErrorWhenCardNumberIsZero() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .number(DataHelper.getZeroCardNumber()).build();
        submitForm(card);
        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenCardNumberIsShort() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .number(DataHelper.getShortCardNumber()).build();
        submitForm(card);
        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldNotAcceptLettersInCardNumber() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .number(DataHelper.getLettersCardNumber())
                .build();

        paymentPage.fillForm(card);
        paymentPage.shouldHaveEmptyCardNumber();
        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldNotAcceptSpecialCharsInCardNumber() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .number(DataHelper.getSpecialCharsCardNumber())
                .build();

        paymentPage.fillForm(card);
        paymentPage.shouldHaveEmptyCardNumber();
        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    //            ВАЛИДАЦИЯ МЕСЯЦА

    @Test
    void shouldShowErrorWhenMonthIsZero() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .month(DataHelper.getZeroMonth()).build();
        submitForm(card);
        paymentPage.shouldShowMonthError("Неверно указан срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenMonthIsAbove12() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .month(DataHelper.getMonthAbove12()).build();
        submitForm(card);
        paymentPage.shouldShowMonthError("Неверно указан срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenMonthIsSingleNumber() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .month(DataHelper.getSingleNumberMonth()).build();
        submitForm(card);
        paymentPage.shouldShowMonthError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenPastMonth() {
        // Пропускаем тест, если сейчас январь (нет прошлого месяца в текущем году)
        Assumptions.assumeTrue(LocalDate.now().getMonthValue() > 1,
                "FORM-20 не применим в январе");

        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .month(DataHelper.getPastMonth())
                .year(DataHelper.getCurrentYear())
                .build();
        submitForm(card);
        paymentPage.shouldShowMonthError("Неверно указан срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    //            ВАЛИДАЦИЯ ГОДА

    @Test
    void shouldShowErrorWhenYearIsZero() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .year(DataHelper.getZeroYear()).build();
        submitForm(card);
        paymentPage.shouldShowYearError("Истёк срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenYearIsPast() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .year(DataHelper.getPastYear()).build();
        submitForm(card);
        paymentPage.shouldShowYearError("Истёк срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenYearIsFarFuture() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .year(DataHelper.getFarFutureYear()).build();
        submitForm(card);
        paymentPage.shouldShowYearError("Неверно указан срок действия карты");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenYearIsSingleNumber() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .year(DataHelper.getSingleNumberYear()).build();
        submitForm(card);
        paymentPage.shouldShowYearError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    //            ВАЛИДАЦИЯ ВЛАДЕЛЬЦА

    @Test
    void shouldShowErrorWhenOwnerIsCyrillic() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getCyrillicOwner()).build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenOwnerHasNumbers() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getOwnerWithNumbers()).build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenOwnerHasSpecialChars() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getOwnerWithSpecialChars()).build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenLowercaseOwner() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getLowercaseOwner()).build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenSingleWordOwner() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getSingleWordOwner()).build();
        submitForm(card);
        paymentPage.shouldShowOwnerError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldLimitInputWhenOwnerIsTooLong() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .owner(DataHelper.getLongOwnerName()).build();
        submitForm(card);
        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastPaymentStatus());
    }

    //            ВАЛИДАЦИЯ CVC

    @Test
    void shouldShowErrorWhenCvcIsOneNumber() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .cvc(DataHelper.getOneNumberCvc()).build();
        submitForm(card);
        paymentPage.shouldShowCvcError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenCvcIsTwoNumbers() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .cvc(DataHelper.getTwoNumbersCvc()).build();
        submitForm(card);
        paymentPage.shouldShowCvcError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }

    @Test
    void shouldShowErrorWhenCvcIsFourNumbers() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .cvc(DataHelper.getFourNumbersCvc()).build();
        submitForm(card);
        paymentPage.shouldShowSuccessNotification();
        assertEquals("APPROVED", DBHelper.getLastPaymentStatus());
    }

    @Test
    void shouldShowErrorWhenCvcHasLetters() {
        CardInfo card = DataHelper.getApprovedCardInfo().toBuilder()
                .cvc(DataHelper.getLettersCvc()).build();
        submitForm(card);
        paymentPage.shouldShowCvcError("Неверный формат");
        assertEquals(0, DBHelper.getPaymentsCount());
    }
}
