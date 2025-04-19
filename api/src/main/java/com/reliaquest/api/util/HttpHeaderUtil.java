package com.reliaquest.api.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHeaderUtil {

    public static HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
