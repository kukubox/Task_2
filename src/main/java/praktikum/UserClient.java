package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

public class UserClient extends Client {
    public static final String USER = "/auth/user";
    public static final String LOGIN = "/auth/login";
    public static final String REGISTER = "/auth/register";

    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(Credentials creds) {
        return spec()
                .body(creds)
                .when()
                .post(LOGIN)
                .then().log().all();
    }

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return spec()
                .body(user)
                .when()
                .post(REGISTER)
                .then().log().all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return spec()
                .header("Authorization", accessToken)
                .when()
                .delete(USER)
                .then().log().all();
    }

    @Step("Редактирование пользователя с авторизацией")
    public ValidatableResponse editUser(User edit) {
        return spec()
                .header("Authorization", this.accessToken)
                .body(edit)
                .when()
                .patch(USER)
                .then().log().all();
    }

    @Step("Редактирование пользователя без авторизации")
    public ValidatableResponse editUserWithoutToken(User edit) {
        return spec()
                .body(edit)
                .when()
                .patch(USER)
                .then().log().all();
    }
}