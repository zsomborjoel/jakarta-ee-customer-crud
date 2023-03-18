package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {

    private static class SingletonHelper {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper getInstance() {
        return ObjectMapperFactory.SingletonHelper.INSTANCE;
    }

}
