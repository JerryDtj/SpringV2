package com.dtj.spring.formework.context;

import com.dtj.spring.formework.core.BeanFactory;

public class ApplicatonContext implements BeanFactory {
    String[] configLocation;

    public ApplicatonContext(String... locations){
        this.configLocation = locations;
        refresh();
    }

    public void refresh(){

    }

    @Override
    public Object getBean(String name) {
        return null;
    }
}
