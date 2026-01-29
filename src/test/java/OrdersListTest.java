import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import com.api.model.CourierApiClient;

public class OrdersListTest {

    private final CourierApiClient apiClient = new CourierApiClient();


    @Step("Получение списка заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что API возвращает список заказов. Ожидается статус 200 и массив заказов (возможно пустой).")
    @Test
    public void testGetOrdersList() {
        // Отправляем запрос
        Response response = apiClient.getOrdersList();

        // Отладка: выводим статус и тело ответа
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body: " + response.body().asString());

        // Проверки
        response.then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json")  // Убеждаемся, что ответ — JSON
                .body("orders.size()", greaterThanOrEqualTo(0));  // Проверяем длину массива orders
    }
}
