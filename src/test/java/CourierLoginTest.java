import com.api.model.CourierApiClient;
import com.models.Courier;
import com.models.CourierLoginRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;


public class CourierLoginTest {
    private CourierApiClient apiClient;
    private Integer courierId;
    private String courierLogin;


    @Before
    public void setUp() {
        apiClient = new CourierApiClient();

        // Генерируем уникальный логин
        courierLogin = "login_test_" + System.currentTimeMillis();
        Courier courier = new Courier(courierLogin, "passwd", "Test");

        Response response = apiClient.createCourier(courier);

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
            Response response = apiClient.deleteCourier(courierId);

            // Логируем ответ на удаление
            System.out.println("[TEARDOWN] Delete courier response: " + response.body().asString());

            response.then()
                    .assertThat()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Успешный вход курьера в систему")
    @Description("Проверяем, что курьер может авторизоваться с корректными логином и паролем. Ожидается статус 200 и наличие поля id в ответе.")
    public void testSuccessfulLogin() {
        CourierLoginRequest request = new CourierLoginRequest(courierLogin, "passwd");

        Response response = apiClient.loginCourier(request);


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
    @DisplayName("Вход с неверным логином")
    @Description("Проверяем ответ API при попытке входа с несуществующим логином. Ожидается статус 404 и сообщение об ошибке.")
    public void testInvalidLogin() {
        CourierLoginRequest invalidRequest = new CourierLoginRequest("wrongUser", "passwd");

        Response response = apiClient.loginCourier(invalidRequest);

        // Ожидаем: HTTP 404 Not Found, тело {"message": "Учетная запись не найдена"}
        response.then()
                .assertThat()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));


        System.out.println("[TEST] Invalid login response: " + response.body().asString());
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверяем ответ API при попытке входа с неверным паролем. Ожидается статус 404 и сообщение об ошибке.")
    public void testInvalidPassword() {
        CourierLoginRequest invalidRequest = new CourierLoginRequest(courierLogin, "invalidPass");

        Response response = apiClient.loginCourier(invalidRequest);

        // Ожидаем: HTTP 404 Not Found, тело {"message": "Учетная запись не найдена"}
        response.then()
                .assertThat()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));

        System.out.println("[TEST] Invalid password response: " + response.body().asString());
    }

    @Test
    @DisplayName("Вход без указания login")
    @Description("Проверяем, что API возвращает 400 и сообщение при отсутствии поля login.")
    public void testMissingLogin() {
        CourierLoginRequest noLogin = new CourierLoginRequest();
        noLogin.setPassword("passwd");

        Response response = apiClient.loginCourier(noLogin);

        response.then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));

        System.out.println("[TEST] Missing login response: " + response.body().asString());
    }

    @Test
    @DisplayName("Вход без указания password")
    @Description("Проверяем, что API возвращает 400 и сообщение при отсутствии поля password.")
    public void testMissingPassword() {
        CourierLoginRequest noPassword = new CourierLoginRequest();
        noPassword.setLogin("testUser");

        Response response = apiClient.loginCourier(noPassword);

        response.then()
                .assertThat()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));

        System.out.println("[TEST] Missing password response: " + response.body().asString());
    }
}