package com.code.ting.netty.rest;

import com.google.common.collect.Maps;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        return "hello, netty-rest";
    }


    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Object get() {
        Map<String, String> datas = Maps.newHashMapWithExpectedSize(10);
        datas.put("name", "netty");
        return datas;
    }

}
