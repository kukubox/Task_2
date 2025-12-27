package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EditUserTest {
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
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
    }

    // 1. Изменение данных пользователя с авторизацией
    @Test
    @DisplayName("Успешное изменение данных пользователя с авторизацией")
    public void testSuccessfulUserEdit() {
        var editedUser = User.editAccountUser();
        ValidatableResponse editResponse = client.editUser(editedUser);
        int statusCodeEditUser = editResponse.extract().statusCode();
        assertEquals(200, statusCodeEditUser);
    }

    // 2. Изменение данных пользователя без авторизации
    @Test
    @DisplayName("Попытка изменения данных пользователя без авторизации")
    public void testEditWithoutAuthorization() {
        var editedUser = User.editAccountUser();
        ValidatableResponse editResponse = client.editUserWithoutToken(editedUser);

        // Проверяем статус код 401
        int statusCode = editResponse.extract().statusCode();
        assertEquals(401, statusCode);

        // Проверяем сообщение об ошибке
        String errorMessage = editResponse.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }

    @After
    @Step("Очистка после теста: удаление пользователя")
    public void deleteUser() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}