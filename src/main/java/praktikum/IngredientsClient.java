package praktikum;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class IngredientsClient extends Client {

    public static final String INGREDIENTS = "/ingredients";

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return spec()
                .accept(ContentType.JSON)
                .get(INGREDIENTS);
    }
}