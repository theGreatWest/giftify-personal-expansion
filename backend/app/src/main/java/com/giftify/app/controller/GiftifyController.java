package com.giftify.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiftifyController {

    @Value("${custom.site.backUrl}")
    private String backUrl;

    @Value("${custom.site.frontUrl}")
    private String frontUrl;

    @GetMapping("/")
    public String root() {
        System.out.println("backUrl = " + backUrl);
        System.out.println("frontUrl = " + frontUrl);

        return "메인 페이지 입니다";
    }
}
