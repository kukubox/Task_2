package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

public class OrderClient extends Client {
    public static final String ORDER = "/orders";

    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrder (Order order) {
        return spec()
                .header("Authorization", this.accessToken)
                .body(order)
                .when()
                .post(ORDER)
                .then().log().all();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutToken (Order order) {
        return spec()
                .body(order)
                .when()
                .post(ORDER)
                .then().log().all();
    }

    @Step("Получение заказов конкретного пользователя с авторизацией")
    public ValidatableResponse getOrders() {
        return spec()
                .header("Authorization", this.accessToken)
                .when()
                .get(ORDER)
                .then().log().all();
    }

    @Step("Получение заказов без авторизации")
    public ValidatableResponse getOrdersWithOutToken() {
        return spec()
                .get(ORDER)
                .then().log().all();
    }
}