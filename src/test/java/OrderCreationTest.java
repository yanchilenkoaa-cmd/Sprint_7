import com.models.OrderRequest;
import com.models.OrderRequestWithoutColor;
import io.qameta.allure.Step;
import org.junit.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";


    @Step("Создание нового заказа")
    @Test
    public void testOrderCreationWithBlackColor() {
        OrderRequest order = new OrderRequest(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha",
                Arrays.asList("BLACK")
        );
        given()
                .body(order)
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа с двумя цветами")
    @Test
    public void testOrderCreationWithBothColors() {
        OrderRequest order = new OrderRequest(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha",
                Arrays.asList("BLACK", "GREY")
        );
        given()
                .body(order)
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа без указания цвета")
    @Test
    public void testOrderCreationWithoutColor() {
        OrderRequestWithoutColor order = new OrderRequestWithoutColor(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha"
        );
        given()
                .body(order)
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }
}