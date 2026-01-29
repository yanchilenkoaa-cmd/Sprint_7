package com.models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Courier {
    private String login;
    private String password;
    private String firstName;
}