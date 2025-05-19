package com.example.currency5.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserBulkDTO {
    private List<UserDTO> users;

    @Data
    public static class UserDTO {
        private String username;
    }
}