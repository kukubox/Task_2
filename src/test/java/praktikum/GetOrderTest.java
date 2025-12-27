package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GetOrderTest {
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;

    @Before
    @Step("Подготовка теста: создание и авторизация пользователя")
    public void setUp() {
        // Создание пользователя
        var user = User.accountForCreateUser();
        ValidatableResponse createResponse = client.createUser(user);
        check.isCreated(createResponse);

        // Логин пользователя
        var creds = Credentials.loginUser(user);
        ValidatableResponse loginResponse = client.loginUser(creds);
        accessToken = check.loginSuccess(loginResponse);

        client.setAccessToken(accessToken);
        orderClient.setAccessToken(accessToken);
    }

    // 1. Получение заказов авторизованным пользователем
    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void testGetOrders() {

        // Отправляем запрос на создание заказа
        ValidatableResponse responseGetOrders = orderClient.getOrders();

        // Проверяем полученный ответ
        int statusCodeGetOrders = responseGetOrders.extract().statusCode();
        assertEquals(200, statusCodeGetOrders);
    }

    // 2. Получение заказов неавторизованным пользователем
    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void testGetOrdersWithOutToken() {

        // Отправляем запрос на создание заказа
        ValidatableResponse responseGetOrders = orderClient.getOrdersWithOutToken();

        // Проверяем полученный ответ
        int statusCodeGetOrders = responseGetOrders.extract().statusCode();
        assertEquals(401, statusCodeGetOrders);

        String errorMessage = responseGetOrders.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}