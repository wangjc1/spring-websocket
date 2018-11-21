package com.wang.springboot.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class JacksonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String bean2Json(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

//    public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
//        try {
//            return mapper.readValue(jsonStr, objClass);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static <T> T json2Bean(String jsonStr, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
