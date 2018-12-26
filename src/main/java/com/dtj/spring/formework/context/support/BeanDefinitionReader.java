package com.dtj.spring.formework.context.support;

import com.dtj.spring.formework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//用对配置文件进行查找，读取、解析
public class BeanDefinitionReader {

    public Properties properties = new Properties();
    private List<String> registyBeanClasses = new ArrayList<String>();
    //在配置文件中，用来获取自动扫描的包名的key
    private final String SCAN_PACKAGE = "scanPackage";

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
        doScanner(SCAN_PACKAGE);
    }

    public BeanDefinition registerBean(String ClassName){
        return null;
    }

    public Properties getProperties(){
        return this.properties;
    }

    public List<String> loadBeanDefinition() {

        return this.registyBeanClasses;
    }

    //递归扫描所有的相关联的class，并且保存到一个List中
    private void doScanner(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.","/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName + "." +file.getName());
            }else {
                registyBeanClasses.add(packageName + "." + file.getName().replace(".class",""));
            }
        }


    }
}
