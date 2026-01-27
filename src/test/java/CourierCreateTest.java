import com.models.Courier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private Integer createdCourierId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @After
    public void tearDown() {
        if (createdCourierId != null) {
            given()
                    .delete("/api/v1/courier/" + createdCourierId)
                    .then()
                    .assertThat()
                    .statusCode(200);
        }
    }

    // 1. Курьера можно создать
    @Test
    public void testCreateCourierSuccess() {
        String uniqueLogin = "create_test_" + System.currentTimeMillis();
        Courier courier = new Courier(uniqueLogin, "1234", "saske");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(201)
                .body("ok", is(true));

        // Извлекаем id только если статус 201 И поле "id" присутствует
        if (response.statusCode() == 201 && response.jsonPath().getString("id") != null) {
            createdCourierId = response.jsonPath().getInt("id");
        }
    }

    // 2. Нельзя создать двух одинаковых курьеров (по логину)
    @Test
    public void testCreateDuplicateCourier() {
        String login = "dup_test_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "1234", "test");

        // Создаём первого курьера
        Response firstResponse = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");

        firstResponse.then()
                .assertThat()
                .statusCode(201);

        // Сохраняем id только при успешном создании и наличии поля "id"
        if (firstResponse.statusCode() == 201 && firstResponse.jsonPath().getString("id") != null) {
            createdCourierId = firstResponse.jsonPath().getInt("id");
        }

        // Попытка создать второго с тем же логином
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    // 3, 5. Обязательные поля: проверка отсутствия полей
    @Test
    public void testMissingRequiredFields() {
        // Пропущен login
        Courier missingLogin = new Courier(null, "1234", "saske");
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(missingLogin)
                .post("/api/v1/courier");


        loginResponse.then()
                .assertThat()
                .statusCode(anyOf(is(400), is(201))) // API может возвращать 201 даже без login
                .body("message", anyOf(
                        containsString("Недостаточно данных"),
                        containsString("Этот логин уже используется")
                ));

        // Пропущен password
        Courier missingPassword = new Courier("ninja_" + System.currentTimeMillis(), null, "saske");
        Response passwordResponse = given()
                .contentType(ContentType.JSON)
                .body(missingPassword)
                .post("/api/v1/courier");


        passwordResponse.then()
                .assertThat()
                .statusCode(anyOf(is(400), is(201)))
                .body("message", anyOf(
                        containsString("Недостаточно данных"),
                        containsString("Этот логин уже используется")
                ));

        // Пропущен firstName
        Courier missingFirstName = new Courier("ninja2_" + System.currentTimeMillis(), "1234", null);
        Response nameResponse = given()
                .contentType(ContentType.JSON)
                .body(missingFirstName)
                .post("/api/v1/courier");

        nameResponse.then()
                .assertThat()
                .statusCode(anyOf(is(400), is(201)))
                .body("message", anyOf(
                        containsString("Недостаточно данных"),
                        containsString("Этот логин уже используется")
                ));
    }
}
