package com.api.model;

import com.models.OrderRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderApiClient {

    static {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1";
    }

    @Step("Отправка запроса на создание заказа")
    public Response createOrder(OrderRequest order) {
        return given()
                .contentType(JSON)
                .body(order)
                .post("/orders");
    }
}
