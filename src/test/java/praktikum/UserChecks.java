package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertTrue;

public class UserChecks {
    @Step("Успешный логин")
    public String loginSuccess(ValidatableResponse loginResponse) {
        String accessToken = loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("accessToken");
        return accessToken;
    }

    @Step("Успешное создание пользователя")
    public void isCreated(ValidatableResponse createResponse) {
        boolean created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");
        assertTrue("Пользователь не был создан", created);
    }
}