import com.models.Courier;
import com.api.model.CourierApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.models.CourierTestData.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    private static CourierApiClient apiClient;
    private Integer createdCourierId;
    private Response lastCreateResponse;

    @BeforeClass
    public static void setup() {
        apiClient = new CourierApiClient();
    }

    @After
    public void tearDown() {
        // 1. Извлекаем id, если был успешный create
        if (lastCreateResponse != null &&
                lastCreateResponse.statusCode() == SC_CREATED &&
                lastCreateResponse.jsonPath().getString("id") != null) {

            createdCourierId = lastCreateResponse.jsonPath().getInt("id");
            System.out.println("Extracted courier ID for cleanup: " + createdCourierId);
        }

        // 2. Удаляем курьера, если id извлечён
        if (createdCourierId != null) {
            Response response = apiClient.deleteCourier(createdCourierId);
            response.then()
                    .assertThat()
                    .statusCode(SC_OK);
            System.out.println("Courier with ID " + createdCourierId + " successfully deleted.");
        }

        // 3. Очищаем lastCreateResponse для следующего теста
        lastCreateResponse = null;
    }
    @Test
    @DisplayName("Создание курьера: успешный сценарий")
    @Description("Проверяем, что курьер создаётся с корректными данными и возвращается статус 201")
    public void testCreateCourierSuccess() {
        String uniqueLogin = generateUniqueLogin(LOGIN_PREFIX_CREATE);
        Courier courier = new Courier(uniqueLogin, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);

        Response response = apiClient.createCourier(courier);
        lastCreateResponse = response; // Сохраняем ответ для @After
        response.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", is(true));

        if (response.statusCode() == SC_CREATED && response.jsonPath().getString("id") != null) {
            createdCourierId = response.jsonPath().getInt("id");
        }
    }

    @Test
    @DisplayName("Создание дублирующего курьера")
    @Description("Проверяем, что нельзя создать курьера с уже существующим логином (статус 409)")
    public void testCreateDuplicateCourier() {
        String login = generateUniqueLogin(LOGIN_PREFIX_DUPLICATE);
        Courier courier = new Courier(login, DEFAULT_PASSWORD, "test");

        Response firstResponse = apiClient.createCourier(courier);
        lastCreateResponse = firstResponse; // Сохраняем для @After

        firstResponse.then().assertThat().statusCode(SC_CREATED);

        Response secondResponse = apiClient.createCourier(courier);
        secondResponse.then()
                .assertThat()
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера без поля login")
    @Description("Проверяем ответ при отсутствии поля login (ожидаем 400 или 201 с сообщением об ошибке)")
    public void testMissingLogin() {
        Courier missingLogin = new Courier(null, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = apiClient.createCourier(missingLogin);
        lastCreateResponse = response; // Сохраняем (хотя тут не понадобится)

        response.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Проверяем ответ при отсутствии поля password (ожидаем 400 или 201 с сообщением об ошибке)")
    public void testMissingPassword() {
        Courier missingPassword = new Courier(generateUniqueLogin(LOGIN_PREFIX_MISSING), null, DEFAULT_FIRST_NAME);
        Response response = apiClient.createCourier(missingPassword);
        lastCreateResponse = response; // Сохраняем

        response.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @DisplayName("Создание курьера без поля firstName")
    @Description("Проверяем ответ при отсутствии поля firstName (ожидаем 400 или 201 с сообщением об ошибке)")
    public void testMissingFirstName() {
        Courier missingFirstName = new Courier(generateUniqueLogin(LOGIN_PREFIX_MISSING), DEFAULT_PASSWORD, null);
        Response response = apiClient.createCourier(missingFirstName);
        lastCreateResponse = response; // Сохраняем
        response.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                        .body("message", containsString("Недостаточно данных"));
    }
    private String generateUniqueLogin(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
