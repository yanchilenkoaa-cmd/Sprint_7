package com.models;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

import static com.models.Endpoints.*;
import static io.restassured.RestAssured.given;

public class CourierApiClient {

    static {
        RestAssured.baseURI = BASE_URL;
    }

    public Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(COURIER_CREATE);
    }

    public Response deleteCourier(Integer courierId) {
        return given()
                .pathParam("id", courierId)
                .delete(COURIER_DELETE);
    }
}