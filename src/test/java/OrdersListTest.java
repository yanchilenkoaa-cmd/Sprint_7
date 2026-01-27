import io.qameta.allure.Step;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrdersListTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    @Step("Получение списка заказов")
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