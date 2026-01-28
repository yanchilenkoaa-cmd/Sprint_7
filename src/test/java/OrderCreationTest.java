import com.models.OrderRequest;
import io.qameta.allure.Step;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    private List<String> colors;

    public OrderCreationTest(List<String> colors) {
        this.colors = colors;
    }

    // Возвращает наборы параметров
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{List.of("BLACK")},
                new Object[]{List.of("BLACK", "GREY")},
                new Object[]{null}
        );
    }

    @Test
    @Step("Создание заказа с цветом {colors}")
    @DisplayName("Создание заказа")
    @Description("Проверяем создание заказа с разными вариантами цветов. Ожидается статус 201 и наличие поля track.")
    public void testOrderCreation() {
        OrderRequest order = new OrderRequest(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha",
                colors
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