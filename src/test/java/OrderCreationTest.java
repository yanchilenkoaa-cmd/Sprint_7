import com.api.model.OrderApiClient;
import com.models.OrderRequest;
import io.restassured.response.Response;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest {
    private List<String> colors;
    private OrderApiClient apiClient = new OrderApiClient();

    public OrderCreationTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{List.of("BLACK")},
                new Object[]{List.of("GREY")},           // ← кейс с GREY уже здесь!
                new Object[]{List.of("BLACK", "GREY")},
                new Object[]{List.of()},
                new Object[]{null}
        );
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цветов")
    @Description("Проверяем создание заказа с различными комбинациями цветов. Ожидается статус 201 и наличие поля track.")
    public void testOrderCreationWithColors() {
        OrderRequest order = new OrderRequest(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha",
                colors
        );

        Response response = apiClient.createOrder(order);

        response.then().assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }
}

