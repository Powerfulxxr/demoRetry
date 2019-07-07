package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("king")
public class KingController {

    @Autowired
    private KingService kingService;

    @RequestMapping("getStr")
    public String getStr(){
        return "king:"+kingService.getBd();
    }
}
