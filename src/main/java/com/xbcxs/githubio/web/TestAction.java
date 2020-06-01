package com.xbcxs.githubio.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAction {

    @RequestMapping("test")
    public void test(){
        System.out.println("test");
    }
}
