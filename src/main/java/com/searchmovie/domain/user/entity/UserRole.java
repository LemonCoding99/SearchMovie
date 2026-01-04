package com.searchmovie.domain.user.entity;

public enum UserRole {
    ADMIN,
    USER;

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
