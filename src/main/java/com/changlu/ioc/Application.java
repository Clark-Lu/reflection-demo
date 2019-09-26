package com.changlu.ioc;

import com.changlu.ioc.annotation.Autowired;
import com.changlu.ioc.annotation.Component;
import com.changlu.ioc.service.Service;
import com.changlu.ioc.util.IOCUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class Application {

    @Autowired
    private  Service service;

    public static void main(String[] args) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        /**
         * 初始化bean容器
         */
        IOCUtil.initContext();
        /**
         * 从bean容器中获取bean
         */
        Application application = IOCUtil.getBeanWithClass(Application.class);
        /**
         * 调用bean属性的方法，测试bean以及bean内Autowired的属性被正确初始化
         */
        application.service.speak();
    }

}
