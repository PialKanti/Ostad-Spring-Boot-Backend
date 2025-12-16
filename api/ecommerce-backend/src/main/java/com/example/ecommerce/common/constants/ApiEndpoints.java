package com.example.ecommerce.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.sql.results.graph.basic.CoercingResultAssembler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpoints {
    private static final String API_VERSION = "/api/v1";
    private static final String BASE_ADMIN = "/admin";

    public static class ProductAdmin {
        public static final String BASE_PRODUCT_ADMIN = API_VERSION + BASE_ADMIN;
    }
}
