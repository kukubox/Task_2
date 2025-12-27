package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private String accessToken;

    // 1. Логин под существующим пользователем
    @Test
    @DisplayName("Успешный логин существующего пользователя")
    public void testSuccessfulUserLogin() {
        // Создание пользователя
        var user = User.accountForCreateUser();
        ValidatableResponse createResponse = client.createUser(user);
        check.isCreated(createResponse);

        // Логин пользователя
        var creds = Credentials.loginUser(user);
        ValidatableResponse loginResponse = client.loginUser(creds);
        accessToken = check.loginSuccess(loginResponse);
        int statusCodeLogin = loginResponse.extract().statusCode();

        // Проверяем, что ожидаемый Статус Код равен фактическому
        assertEquals(200, statusCodeLogin);
    }

    // 2. Логин с неверным логином и паролем
    @Test
    @DisplayName("Логин с неверными учетными данными")
    public void testLoginWithNonExistingUser() {
        // Создаем учетные данные для несуществующего пользователя
        Credentials invalidCreds = new Credentials("InvalidEmpire" + System.currentTimeMillis(), "11111111");

        // Пытаемся выполнить логин с несуществующим пользователем
        ValidatableResponse loginResponse = client.loginUser(invalidCreds);

        // Проверяем, что статус код ошибки - 401
        int statusCode = loginResponse.extract().statusCode();
        assertEquals(401, statusCode);

        // Проверяем сообщение об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals("email or password are incorrect", errorMessage);
    }

    @After
    @Step("Очистка после теста: удаление пользователя")
    public void deleteUser() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}