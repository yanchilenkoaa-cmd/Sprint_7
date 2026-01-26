import io.qameta.allure.Step;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    @BeforeClass
    public static void createCourierForTests() {
        given()
                .body("{\n" +
                        "   \"login\": \"testUser\",\n" +
                        "   \"password\": \"passwd\",\n" +
                        "   \"firstName\": \"John\"\n" +
                        "}")
                .post(BASE_URL + "/courier");
    }

    @AfterClass
    public static void deleteCreatedCourier() {
        given()
                .when().delete(BASE_URL + "/courier/{id}", 1);
    }

    @Step("Авторизация курьера")
    @Test
    public void testSuccessfulLogin() {
        given()
                .body("{\n" +
                        "   \"login\": \"testUser\",\n" +
                        "   \"password\": \"passwd\"\n" +
                        "}")
                .post(BASE_URL + "/courier/login")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Неверные данные для авторизации")
    @Test
    public void testInvalidCredentials() {
        given()
                .body("{\n" +
                        "   \"login\": \"wrongUser\",\n" +
                        "   \"password\": \"invalidPass\"\n" +
                        "}")
                .post(BASE_URL + "/courier/login")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Step("Отсутствие необходимых полей")
    @Test
    public void testMissingFieldInRequest() {
        given()
                .body("{\n" +
                        "   \"login\": \"testUser\"\n" +
                        "}")
                .post(BASE_URL + "/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }
}