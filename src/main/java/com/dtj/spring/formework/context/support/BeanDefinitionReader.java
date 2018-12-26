package com.dtj.spring.formework.context.support;

import com.dtj.spring.formework.beans.BeanDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BeanDefinitionReader {

    public Properties properties = new Properties();

    public BeanDefinitionReader(String... locations){
        //spring中是用reader来做定位，这里直接
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BeanDefinition registerBean(String ClassName){
        return null;
    }

    public Properties getProperties(){
        return this.properties;
    }
}
