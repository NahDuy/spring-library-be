package com.nad.start_spring.service.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationCodeStore {
    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    public void saveCode(String email, String code) {
        codeMap.put(email, code);
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(codeMap.get(email));
    }

    public void removeCode(String email) {
        codeMap.remove(email);
    }
}
