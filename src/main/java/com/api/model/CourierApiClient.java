package com.api.model;

import com.models.Courier;
import com.models.CourierLoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import io.qameta.allure.Step;

import static com.models.Endpoints.*;
import static io.restassured.RestAssured.given;

public class CourierApiClient {

    static {
        RestAssured.baseURI = BASE_URL;
    }
    @Step("Создание курьера: {courier}")
    public Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(COURIER_CREATE);
    }
    @Step("Удаление курьера по ID: {courierId}")
    public Response deleteCourier(Integer courierId) {
        return given()
                .pathParam("id", courierId)
                .delete(COURIER_DELETE);
    }
    @Step("Авторизация курьера: {request}")
    public Response loginCourier(CourierLoginRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(COURIER_LOGSIS);
    }
    @Step("Получение списка заказов")
    public Response getOrdersList() {
        System.out.println("[API] GET /api/v1/orders");
        return given()
                .get("/api/v1/orders");  // Полный путь к API
    }


}