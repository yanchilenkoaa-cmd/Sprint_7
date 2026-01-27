import com.models.Courier;
import com.models.CourierLoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class CourierLoginTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private Integer courierId;
    private String courierLogin;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;


        // Генерируем уникальный логин
        courierLogin = "login_test_" + System.currentTimeMillis();
        Courier courier = new Courier(courierLogin, "passwd", "Test");


        Response response = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");


        // Ожидаем 201 Created и {"ok": true}
        response.then()
                .assertThat()
                .statusCode(201)
                .body("ok", is(true));

        System.out.println("[SETUP] Create courier response: " + response.body().asString());
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            Response response = given()
                    .delete("/api/v1/courier/" + courierId);

            // Логируем ответ на удаление
            System.out.println("[TEARDOWN] Delete courier response: " + response.body().asString());


            response.then()
                    .assertThat()
                    .statusCode(200);
        }
    }

    @Test
    public void testSuccessfulLogin() {
        CourierLoginRequest request = new CourierLoginRequest(courierLogin, "passwd");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/courier/login");


        // Ожидаем: HTTP 200 OK, тело {"id": 12345}
        response.then()
                .assertThat()
                .statusCode(200)
                .body("id", instanceOf(Integer.class));  // id — число


        // Сохраняем id для tearDown
        courierId = response.jsonPath().getInt("id");
        System.out.println("[TEST] Successful login response (got ID): " + response.body().asString());
    }

    @Test
    public void testInvalidCredentials() {
        CourierLoginRequest invalidRequest = new CourierLoginRequest("wrongUser", "invalidPass");


        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .post("/api/v1/courier/login");


        // Ожидаем: HTTP 404 Not Found, тело {"message": "Учетная запись не найдена"}
        response.then()
                .assertThat()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));


        System.out.println("[TEST] Invalid credentials response: " + response.body().asString());
    }

    @Test
    public void testMissingLoginOrPassword() {
        // Случай 1: нет login
        CourierLoginRequest noLogin = new CourierLoginRequest();
        noLogin.setPassword("passwd");


        Response response1 = given()
                .contentType(ContentType.JSON)
                .body(noLogin)
                .post("/api/v1/courier/login");


        response1.then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));


        System.out.println("[TEST] Missing login response: " + response1.body().asString());


        // Случай 2: нет password
        CourierLoginRequest noPassword = new CourierLoginRequest();
        noPassword.setLogin("testUser");


        Response response2 = given()
                .contentType(ContentType.JSON)
                .body(noPassword)
                .post("/api/v1/courier/login");
        response2.then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
        System.out.println("[TEST] Missing password response: " + response2.body().asString());
    }
}