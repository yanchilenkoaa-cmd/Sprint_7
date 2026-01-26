import io.qameta.allure.Step;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    @BeforeClass
    public static void setup() {
        reset();
    }

    @Step("Создание нового заказа")
    @Test
    public void testOrderCreationWithBlackColor() {
        given()
                .body("{\n" +
                        "   \"firstName\": \"Naruto\",\n" +
                        "   \"lastName\": \"Uchiha\",\n" +
                        "   \"address\": \"Konoha, 142 apt.\",\n" +
                        "   \"metroStation\": 4,\n" +
                        "   \"phone\": \"+7 800 355 35 35\",\n" +
                        "   \"rentTime\": 5,\n" +
                        "   \"deliveryDate\": \"2020-06-06\",\n" +
                        "   \"comment\": \"Saske, come back to Konoha\",\n" +
                        "   \"color\": [\n" +
                        "       \"BLACK\"\n" +
                        "   ]\n" +
                        "}")
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа с двумя цветами")
    @Test
    public void testOrderCreationWithBothColors() {
        given()
                .body("{\n" +
                        "   \"firstName\": \"Naruto\",\n" +
                        "   \"lastName\": \"Uchiha\",\n" +
                        "   \"address\": \"Konoha, 142 apt.\",\n" +
                        "   \"metroStation\": 4,\n" +
                        "   \"phone\": \"+7 800 355 35 35\",\n" +
                        "   \"rentTime\": 5,\n" +
                        "   \"deliveryDate\": \"2020-06-06\",\n" +
                        "   \"comment\": \"Saske, come back to Konoha\",\n" +
                        "   \"color\": [\n" +
                        "       \"BLACK\", \"GREY\"\n" +
                        "   ]\n" +
                        "}")
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа без указания цвета")
    @Test
    public void testOrderCreationWithoutColor() {
        given()
                .body("{\n" +
                        "   \"firstName\": \"Naruto\",\n" +
                        "   \"lastName\": \"Uchiha\",\n" +
                        "   \"address\": \"Konoha, 142 apt.\",\n" +
                        "   \"metroStation\": 4,\n" +
                        "   \"phone\": \"+7 800 355 35 35\",\n" +
                        "   \"rentTime\": 5,\n" +
                        "   \"deliveryDate\": \"2020-06-06\",\n" +
                        "   \"comment\": \"Saske, come back to Konoha\"\n" +
                        "}")
                .post(BASE_URL + "/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }
}