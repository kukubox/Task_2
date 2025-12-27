package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class OrderTest {
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;
    private IngredientsClient ingredientsClient;

    @Before
    @Step("Подготовка теста: создание пользователя и получение ингредиентов")
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

        ingredientsClient = new IngredientsClient();
    }

    // 1. Создание заказа с авторизацией
    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    public void testCreateOrderWithTokenAndIngredients() {
        // Получаем список валидных ингредиентов
        Response ingredientsResponse = ingredientsClient.getIngredients();
        ingredientsResponse.then().statusCode(200);

        List<String> ingredients = ingredientsResponse.jsonPath().getList("data._id");
        assertFalse("Список ингредиентов не должен быть пустым", ingredients.isEmpty());

        // Берем первые 2 ингредиента для теста
        List<String> ingredientsForOrder = ingredients.subList(0, 2);

        // Создаем заказ
        Order order = new Order(ingredientsForOrder);

        // Отправляем запрос на создание заказа
        ValidatableResponse createOrderResponse = orderClient.createOrder(order);

        // Проверяем полученный ответ
        int statusCodeCreateOrder = createOrderResponse.extract().statusCode();
        assertEquals(200, statusCodeCreateOrder);

        // Проверяем, что с Токеном приходит от бэка поле 'ingredients' и оно не пустое
        List<String> responseIngredients = createOrderResponse.extract().path("order.ingredients");
        assertNotNull("Поле 'order.ingredients' должно присутствовать в ответе", responseIngredients);
        assertFalse("Список ингредиентов в заказе не должен быть пустым", responseIngredients.isEmpty());
    }

    // 2. Создание заказа без авторизации
    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWitoutToken() {
        // Получаем список валидных ингредиентов
        Response ingredientsResponse = ingredientsClient.getIngredients();
        ingredientsResponse.then().statusCode(200);

        List<String> ingredients = ingredientsResponse.jsonPath().getList("data._id");
        assertFalse("Список ингредиентов не должен быть пустым", ingredients.isEmpty());

        // Берем первые 2 ингредиента для теста
        List<String> ingredientsForOrder = ingredients.subList(0, 2);

        // Создаем заказ
        Order order = new Order(ingredientsForOrder);

        // Отправляем запрос на создание заказа
        ValidatableResponse createOrderResponse = orderClient.createOrderWithoutToken(order);

        // Проверяем полученный ответ
        int statusCodeCreateOrder = createOrderResponse.extract().statusCode();
        assertEquals(200, statusCodeCreateOrder);

        // Проверяем, что без Токена авторизации не приходит от бэка поле 'ingredients'
        List<String> responseIngredients = createOrderResponse.extract().path("order.ingredients");
        assertNull("Поле 'order.ingredients' должно отсутствовать в ответе", responseIngredients);
    }

    // 3. Создание заказа без ингредиентов
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithOutIngredients() {
        // Создание заказа с пустым списком ингредиентов
        Order emptyOrder = new Order(List.of());
        ValidatableResponse withOutIngredientsResponse = orderClient.createOrder(emptyOrder);

        // Делаем проверки:
        withOutIngredientsResponse.statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        // Создаем список с неверными хешами ингредиентов
        List<String> invalidIngredientsHash = List.of("qqq", "invalidHash2");

        // Создаем заказ
        Order invalidOrder = new Order(invalidIngredientsHash);

        ValidatableResponse invalidIngredientsHashResponse = orderClient.createOrder(invalidOrder);

        int statusCodeInvalidHash = invalidIngredientsHashResponse.extract().statusCode();
        assertEquals(500, statusCodeInvalidHash);
    }

    @After
    @Step("Очистка после теста: удаление пользователя")
    public void deleteUser() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}