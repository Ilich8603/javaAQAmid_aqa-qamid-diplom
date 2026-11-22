package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper.CardInfo;
import java.time.Duration;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object для формы оплаты (как дебетовой, так и в кредит).
 * Формы идентичны по структуре, отличаются только заголовком.
 */
public class PaymentPage {

    private final SelenideElement cardNumberInput = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthInput = $("input[placeholder='08']");
    private final SelenideElement yearInput = $("input[placeholder='22']");
    private final SelenideElement ownerInput = $(byText("Владелец"))
            .closest(".input").find("input.input__control");
    private final SelenideElement cvcInput = $("input[placeholder='999']");

    private final SelenideElement continueButton = $$("button").findBy(Condition.text("Продолжить"));

    private final SelenideElement successNotification = $(byText("Операция одобрена Банком."));
    private final SelenideElement errorNotification = $(byText("Ошибка! Банк отказал в проведении операции."));



    public void fillForm(CardInfo card) {
        cardNumberInput.setValue(card.getNumber());
        monthInput.setValue(card.getMonth());
        yearInput.setValue(card.getYear());
        ownerInput.setValue(card.getOwner());
        cvcInput.setValue(card.getCvc());
    }

    public void clickContinue() {
        continueButton.click();
    }

    public void shouldShowSuccessNotification() {
        successNotification.shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Операция одобрена Банком."));
    }

    public void shouldShowErrorNotification() {
        errorNotification
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."));
    }

    public void shouldShowErrorUnderField(SelenideElement inputField, String expectedErrorText) {
        inputField.parent().parent().find(".input__sub")
                .shouldHave(Condition.text(expectedErrorText), Duration.ofSeconds(5));
    }

    public void shouldShowCardNumberError(String text) {
        shouldShowErrorUnderField(cardNumberInput, text);
    }

    public void shouldShowMonthError(String text) {
        shouldShowErrorUnderField(monthInput, text);
    }

    public void shouldShowYearError(String text) {
        shouldShowErrorUnderField(yearInput, text);
    }

    public void shouldShowOwnerError(String text) {
        shouldShowErrorUnderField(ownerInput, text);
    }

    public void shouldShowCvcError(String text) {
        shouldShowErrorUnderField(cvcInput, text);
    }

    public void shouldHaveEmptyCardNumber() {
        cardNumberInput.shouldBe(Condition.empty);
    }
}
