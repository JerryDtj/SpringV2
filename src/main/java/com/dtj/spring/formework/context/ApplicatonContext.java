package com.dtj.spring.formework.context;

import com.dtj.spring.formework.annotation.Autowired;
import com.dtj.spring.formework.annotation.Controller;
import com.dtj.spring.formework.annotation.Service;
import com.dtj.spring.formework.beans.BeanDefinition;
import com.dtj.spring.formework.beans.BeanWrapper;
import com.dtj.spring.formework.context.support.BeanDefinitionReader;
import com.dtj.spring.formework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicatonContext implements BeanFactory {
    String[] configLocation;

    private BeanDefinitionReader reader;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    //用来存储所有的被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

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
//依赖注入（lazy-init = false），要是执行依赖注入
        //在这里自动调用getBean方法
        doAutowrited();

//        MyAction myAction = (MyAction)this.getBean("myAction");
//        myAction.query(null,null,"任性的Tom老师");

    }

    //开始执行自动化的依赖注入
    private void doAutowrited() {


        for(Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();

            if(!beanDefinitionEntry.getValue().isLazyInit()){
                Object obj = getBean(beanName);
//                System.out.println(obj.getClass());
            }

        }


        for(Map.Entry<String, BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){

            populateBean(beanWrapperEntry.getKey(),beanWrapperEntry.getValue().getOriginalInstance());

        }

//        System.out.println("===================");


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

    public void populateBean(String beanName,Object instance){

        Class clazz = instance.getClass();

        //不是所有牛奶都叫特仑苏
        if(!(clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class))){
            return;
        }


        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)){ continue; }

            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName = autowired.value().trim();

            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {

                //System.out.println("=======================" +instance +"," + autowiredBeanName + "," + this.beanWrapperMap.get(autowiredBeanName));
                field.set(instance,this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }



    }
}
