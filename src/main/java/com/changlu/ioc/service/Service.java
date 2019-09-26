package com.changlu.ioc.service;

import com.changlu.ioc.annotation.Component;

@Component
public class Service {

    public void speak(){
        System.out.println("This is service");
    }

}
