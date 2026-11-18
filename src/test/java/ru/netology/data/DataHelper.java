package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



public final class DataHelper {
    private static final Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
    }

    public static CardInfo getApprovedCardInfo() {
        return CardInfo.builder()
                .number(getApprovedCardNumber())
                .month(getValidMonth())
                .year(getValidYear())
                .owner(getValidOwnerName())
                .cvc(getValidCvc())
                .build();
    }

    public static CardInfo getDeclinedCardInfo() {
        return CardInfo.builder()
                .number(getDeclinedCardNumber())
                .month(getValidMonth())
                .year(getValidYear())
                .owner(getValidOwnerName())
                .cvc(getValidCvc())
                .build();
    }

    public static CardInfo getZeroCvcCardInfo() {
        return CardInfo.builder()
                .number(getApprovedCardNumber())
                .month(getValidMonth())
                .year(getValidYear())
                .owner(getValidOwnerName())
                .cvc(getZeroCvc())
                .build();
    }

        //--Валидные данные--
    public static String getApprovedCardNumber() {
        return "1111 2222 3333 4444";
    }

    public static String getDeclinedCardNumber() {
        return "5555 6666 7777 8888";
    }

    public static String getValidMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getValidYear() {
        int year = LocalDate.now().getYear() + 1;
        return String.format("%02d", year % 100);
    }

    public static String getValidOwnerName() {
        return (faker.name().firstName() + " " + faker.name().lastName()).toUpperCase();
    }

    public static String getValidCvc() {
        return String.format("%03d", faker.number().numberBetween(1, 999));
    }

    public static String getZeroCvc() {
        return "000";
    }
        //--НЕвалидные НОМЕР КАРТЫ--
    public static String getZeroCardNumber() {
        return "0000 0000 0000 0000";
    }

    public static String getShortCardNumber() {
        return "1111 2222 3333 444";
    }

    public static String getLettersCardNumber() {
        return "abcd efgh ijkl mnop";
    }

    public static String getSpecialCharsCardNumber() {
        return "@@@@ #### $$$$ %%%%";
    }

    public static String getUnknownCardNumber() {
        return "1234 5678 9012 3456";
    }

        //--НЕвалидные МЕСЯЦ--

    public static String getZeroMonth() {
        return "00";
    }

    public static String getMonthAbove12() {
        return "13";
    }

    public static String getSingleNumberMonth() {
        return "5";
    }

    /**
     * FORM-19: прошлый месяц при текущем годе.
     * ВАЖНО: В январе прошлого месяца в текущем году не существует,
     * поэтому тест FORM-19 должен быть пропущен (см. Assumptions в тесте).
     * Метод возвращает декабрь для января, но тест в этом случае не выполняется.
     */
    public static String getPastMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        if (currentMonth == 1) {
            throw new IllegalStateException(
                    "Прошлого месяца в текущем году не существует (сейчас январь). " +
                            "Тест FORM-19 должен быть пропущен через Assumptions.assumeTrue()."
            );
        }
        return String.format("%02d", currentMonth - 1);
    }

        //--НЕвалидные ГОД--
    public static String getZeroYear() {
        return "00";
    }

    public static String getPastYear() {
        int year = LocalDate.now().getYear() - 1;
        return String.format("%02d", year % 100);
    }

    public static String getFarFutureYear() {
        int year = LocalDate.now().getYear() + 7;
        return String.format("%02d", year % 100);
    }

    public static String getSingleNumberYear() {
        return "5";
    }

        //--НЕвалидные ВЛАДЕЛЕЦ--
    public static String getCyrillicOwner() {
        return "ИВАН ИВАНОВ";
    }

    public static String getOwnerWithNumbers() {
        return "IVAN 123";
    }

    public static String getOwnerWithSpecialChars() {
        return "IVAN !@#";
    }

    public static String getSingleWordOwner() {
        return "IVAN";
    }

    public static String getLongOwnerName() {
        StringBuilder name = new StringBuilder();
        int attempts = 0;
        while (name.length() < 55 && attempts < 10) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            if (firstName != null && lastName != null) {
                name.append(firstName.toUpperCase())
                        .append(" ")
                        .append(lastName.toUpperCase())
                        .append(" ");
            }
            attempts++;
        }
        if (name.length() < 55) {
            throw new IllegalStateException(
                    "Не удалось сгенерировать владельца длиннее 55 символов"
            );
        }
        return name.toString().trim();
    }

        //--НЕвалидные CVC/CVV--
    public static String getOneNumberCvc() {
        return "1";
    }

    public static String getTwoNumbersCvc() {
        return "12";
    }

    public static String getFourNumbersCvc() {
        return "1234";
    }

    public static String getLettersCvc() {
        return "abc";
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        private String number;   // "1111 2222 3333 4444"
        private String month;    // "12"
        private String year;     // "24"
        private String owner;    // "IVAN IVANOV"
        private String cvc;      // "123"
    }

}
