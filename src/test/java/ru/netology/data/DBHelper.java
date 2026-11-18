package ru.netology.data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBHelper {

    private static final QueryRunner runner = new QueryRunner();

    private DBHelper() {
    }

    /**
     * Возвращает новое подключение к БД.
     */
    private static Connection getConnection() throws SQLException {
        String url = System.getProperty("db.url", "jdbc:mysql://localhost:3306/app");
        String user = System.getProperty("db.user", "app");
        String password = System.getProperty("db.password", "pass");
        return DriverManager.getConnection(url, user, password);
    }

    //    ОЧИСТКА БД ПЕРЕД ТЕСТАМИ

    /**
     * Полностью очищает таблицы приложения.
     * <p>
     * Вызывается в {@code @BeforeEach} перед каждым тестом, чтобы:
     * <ul>
     *   <li>предыдущие тесты не влияли на текущий;</li>
     *   <li>{@code ORDER BY created DESC LIMIT 1} возвращал именно запись
     *       текущего теста.</li>
     * </ul>
     * <p>
     * Порядок удаления важен из-за внешних ключей: сначала {@code order_entity},
     * потом {@code payment_entity} и {@code credit_request_entity}.
     */
    public static void cleanDatabase() {
        try (Connection conn = getConnection()) {
            runner.execute(conn, "DELETE FROM order_entity;");
            runner.execute(conn, "DELETE FROM payment_entity;");
            runner.execute(conn, "DELETE FROM credit_request_entity;");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить БД", e);
        }
    }

    //     СТАТУСЫ ПОСЛЕДНИХ ОПЕРАЦИЙ

    /**
     * Возвращает статус последней операции оплаты по дебетовой карте.
     * Ожидаемые значения: {@code APPROVED} / {@code DECLINED}.
     * <p>
     * Используется в TC-01, TC-02.1 (ожидаем APPROVED) и TC-03 (ожидаем DECLINED).
     */
    public static String getLastPaymentStatus() {
        String sql = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить статус последнего платежа", e);
        }
    }

    /**
     * Возвращает статус последней заявки на кредит.
     * Ожидаемые значения: {@code APPROVED} / {@code DECLINED}.
     * <p>
     * Используется в TC-02 (ожидаем APPROVED) и TC-04 (ожидаем DECLINED).
     */
    public static String getLastCreditStatus() {
        String sql = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить статус последней заявки на кредит", e);
        }
    }

    /**
     * Возвращает сумму последнего платежа (в копейках).
     * Стоимость тура по ТЗ — 45 000 руб., значит в БД должно быть {@code 4500000}.
     */
    public static long getLastPaymentAmount() {
        String sql = "SELECT amount FROM payment_entity ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            // 💡 Используем Number вместо Long
            Number amount = runner.query(conn, sql, new ScalarHandler<>());
            return amount == null ? 0L : amount.longValue();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить сумму последнего платежа", e);
        }
    }


    //       ПОДСЧЁТ ЗАПИСЕЙ (для негативных тестов)

    /**
     * Возвращает количество записей в таблице {@code payment_entity}.
     * <p>
     * В тестах валидации полей (TC-05 … TC-32) ожидается {@code 0}:
     * форма не должна была уйти в банк и оставить след в БД.
     */
    public static long getPaymentsCount() {
        String sql = "SELECT COUNT(*) FROM payment_entity;";
        try (Connection conn = getConnection()) {
            Number count = runner.query(conn, sql, new ScalarHandler<>());
            return count == null ? 0L : count.longValue();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить количество платежей", e);
        }
    }

    /**
     * Возвращает количество записей в таблице {@code credit_request_entity}.
     */
    public static long getCreditsCount() {
        String sql = "SELECT COUNT(*) FROM credit_request_entity;";
        try (Connection conn = getConnection()) {
            Number count = runner.query(conn, sql, new ScalarHandler<>());
            return count == null ? 0L : count.longValue();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить количество заявок на кредит", e);
        }
    }

    /**
     * Возвращает количество записей в таблице {@code order_entity}.
     */
    public static long getOrdersCount() {
        String sql = "SELECT COUNT(*) FROM order_entity;";
        try (Connection conn = getConnection()) {
            Number count = runner.query(conn, sql, new ScalarHandler<>());
            return count == null ? 0L : count.longValue();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить количество заказов", e);
        }
    }

    //      ПРОВЕРКА СВЯЗЕЙ order_entity ↔ payment/credit

    /**
     * Проверяет, что последний заказ в {@code order_entity} ссылается
     * на последний платёж из {@code payment_entity} (совпадение {@code payment_id}).
     * <p>
     * Нужно для проверки корректности связей в БД (одно из требований диплома:
     * «запросы в базу, проверяющие корректность внесения информации приложением»).
     *
     * @return {@code true}, если связь корректна
     */
    public static boolean isLastOrderLinkedToLastPayment() {
        String orderSql = "SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1;";
        String paymentSql = "SELECT payment_id FROM payment_entity ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            String orderPaymentId = runner.query(conn, orderSql, new ScalarHandler<>());
            String paymentPaymentId = runner.query(conn, paymentSql, new ScalarHandler<>());
            return orderPaymentId != null && orderPaymentId.equals(paymentPaymentId);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось проверить связь order_entity ↔ payment_entity", e);
        }
    }

    /**
     * Аналогично {@link #isLastOrderLinkedToLastPayment()}, но для кредита.
     */
    public static boolean isLastOrderLinkedToLastCredit() {
        String orderSql = "SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1;";
        String creditSql = "SELECT credit_id FROM credit_request_entity ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            String orderCreditId = runner.query(conn, orderSql, new ScalarHandler<>());
            String creditCreditId = runner.query(conn, creditSql, new ScalarHandler<>());
            return orderCreditId != null && orderCreditId.equals(creditCreditId);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось проверить связь order_entity ↔ credit_request_entity", e);
        }
    }
}