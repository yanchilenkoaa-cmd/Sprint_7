import io.qameta.allure.Step;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

public class CourierCreateTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";
    private static List<Integer> createdCourierIds = new ArrayList<>();

    // Сохраняем созданные ID курьеров для последующего удаления
    @AfterClass
    public static void cleanUp() {
        for (Integer id : createdCourierIds) {
            delete(BASE_URL + "/courier/" + id)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(anyOf(is(200), is(404))); // принимаем оба возможных результата
        }
    }

    @BeforeClass
    public static void setup() {
        reset(); // сброс состояния REST Assured
    }

    /**
     * Тестируем успешное создание курьера
     */
    @Step("Создание нового курьера")
    @Test
    public void testCreateCourierSuccess() {
        int response = given()
                .body("{ \"login\": \"ninja\", \"password\": \"1234\", \"firstName\": \"saske\" }")
                .post(BASE_URL + "/courier")
                .then()
                .assertThat()
                .statusCode(201)
                .extract().path("id");

        createdCourierIds.add(response); // сохраняем созданный ID
    }

    /**
     * Проверка попытки повторного создания курьера с тем же именем
     */
    @Step("Проверка дублирования имени пользователя")
    @Test
    public void testDuplicateLogin() {
        given()
                .body("{ \"login\": \"ninja\", \"password\": \"1234\", \"firstName\": \"anotherUser\" }")
                .post(BASE_URL + "/courier")
                .then()
                .assertThat()
                .statusCode(409) // изменили ожидаемый статус
                .body("message", containsString("Этот логин уже используется"));
    }

    /**
     * Проверка отправки неполных данных
     */
    @Step("Проверка отсутствия обязательных полей")
    @Test
    public void testMissingFields() {
        given()
                .body("{ \"login\": \"ninja\" }")
                .post(BASE_URL + "/courier")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }
}