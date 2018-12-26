package com.dtj.springMvc.service.impl;


import com.dtj.springMvc.service.DemoService;
import com.dtj.spring.formework.annotation.Service;

@Service
public class DemoServiceImpl implements DemoService {

    public String get(String name) {
        return "My name is " + name;
    }

}
