import io.qameta.allure.Step;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

public class OrdersListTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    @Step("Получение списка заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что API возвращает список заказов. Ожидается статус 200 и массив заказов (возможно пустой).")
    @Test
    public void testGetOrdersList() {
        when()
                .get(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}