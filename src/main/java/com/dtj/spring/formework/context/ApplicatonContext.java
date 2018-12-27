package com.dtj.spring.formework.context;

import com.dtj.spring.formework.beans.BeanDefinition;
import com.dtj.spring.formework.context.support.BeanDefinitionReader;
import com.dtj.spring.formework.core.BeanFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicatonContext implements BeanFactory {
    String[] configLocation;

    private BeanDefinitionReader reader;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

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

    //真正的将BeanDefinitions注册到beanDefinitionMap中
    private void doRegisty(List<String> beanDefinitions) {
        //beanName有三种情况:
        //1、默认是类名首字母小写
        //2、自定义名字
        //3、接口注入
        try {
            for (String className:beanDefinitions){
                Class<?> beanClass = Class.forName(className);
                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if (beanClass.isInterface()) {
                    continue;
                }
                BeanDefinition beanDefinition = reader.registerBean(className);
                if (beanDefinition!=null){
                    this.beanDefinitionMap.put(className,beanDefinition);
                }
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i: interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }


                //到这里为止，容器初始化完毕
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Object getBean(String name) {
        return null;
    }
}
