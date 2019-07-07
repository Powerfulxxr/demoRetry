package com.example.demo;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KingService {


    @TechlogRetryer(retryThrowable = Exception.class, maxAttempt = 3)
    public BigDecimal getBd(){
        try {
            System.out.println("哈哈哈");
            int a =1/0;
            return BigDecimal.TEN.add(BigDecimal.ONE);
        } catch (Exception e) {
            throw  new RuntimeException("嘻嘻");
        }
    }
}
