package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import java.time.Duration;

/**
 * Page Object для главной страницы приложения "Путешествие дня".
 * Содержит кнопки выбора способа оплаты.
 */
public class MainPage {

    private final SelenideElement pageHeader = $("h2.heading");

    // Кнопка "Купить" (обычная оплата по дебетовой карте)
    private final SelenideElement buyButton = $$("button").findBy(com.codeborne.selenide.Condition.text("Купить"));

    // Кнопка "Купить в кредит" (оплата в кредит)
    private final SelenideElement buyCreditButton = $$("button").findBy(com.codeborne.selenide.Condition.text("Купить в кредит"));

    public void verifyPageIsOpen() {
        pageHeader.shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.text("Путешествие дня"));
    }


    public PaymentPage clickBuyButton() {
        buyButton.click();
        return new PaymentPage();
    }


    public PaymentPage clickBuyCreditButton() {
        buyCreditButton.click();
        return new PaymentPage();
    }
}