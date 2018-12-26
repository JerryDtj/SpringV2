package com.dtj.spring.formework.context;

import com.dtj.spring.formework.context.support.BeanDefinitionReader;
import com.dtj.spring.formework.core.BeanFactory;

import java.util.List;

public class ApplicatonContext implements BeanFactory {
    String[] configLocation;

    private BeanDefinitionReader reader;

    public ApplicatonContext(String... locations){
        this.configLocation = locations;
        refresh();
    }

    public void refresh(){

//        定位
        this.reader = new BeanDefinitionReader(configLocation);
//        加载
        List<String> beanDefinitions = reader.loadBeanDefinition();
//        注册
        doRegisty(beanDefinitions);
//        依赖注入

    }

    private void doRegisty(List<String> beanDefinitions) {
    }

    @Override
    public Object getBean(String name) {
        return null;
    }
}
