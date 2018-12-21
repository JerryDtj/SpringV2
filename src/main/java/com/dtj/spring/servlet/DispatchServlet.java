package com.dtj.spring.servlet;

import com.dtj.spring.annotation.Autowried;
import com.dtj.spring.annotation.Controller;
import com.dtj.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {
    private Properties properties = new Properties();

    private Map<String,Object> beanDefinitionMap = new ConcurrentHashMap<String, Object>();

    private List<String> beanDefinitionNames = new ArrayList<String>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("开始初spring容器的初始化");
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载
        doLoad(properties.getProperty("scanPackage"));
        //注册
        doRegist();
        //自动依赖注入
        doAutoWired();
    }

    private void doAutoWired() {
        for (Map.Entry<String,Object> entry:beanDefinitionMap.entrySet()){
            Field[] fields =entry.getValue().getClass().getDeclaredFields();
            for (Field f:fields){
                if (!f.isAnnotationPresent(Autowried.class))
                    continue;
                Autowried autowried = f.getAnnotation(Autowried.class);
                String beanName = autowried.value().trim();
                if ("".equals(beanName)){
                    beanName = f.getType().getName();
                }

                f.setAccessible(true);

                if (entry.getValue().getClass().isAnnotationPresent(Service.class)){
                    beanName +="Impl";
                }

                try {
                    f.set(entry.getValue(),beanDefinitionMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void doRegist() {
        for (String s:beanDefinitionNames){
            try {
                Class<?> clazz = Class.forName(s);
                if (clazz.isAnnotationPresent(Service.class)||clazz.isAnnotationPresent(Controller.class)){
                    String beanName = clazz.getSimpleName();
                    System.out.println(beanName);
                    beanDefinitionMap.put(toLowerFirstCase(beanName),clazz.newInstance());
                }else if (clazz.isAnnotationPresent(Controller.class)){

                }else {
                    continue;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        System.out.println(beanDefinitionMap.size());
    }

    private void doLoad(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File file = new File(url.getFile());
        for (File f:file.listFiles()){
            if (f.isDirectory()){
                doLoad(packageName+"."+f.getName());
            }else {
                beanDefinitionNames.add(packageName+"."+f.getName().replace(".class",""));
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--------开始调用doPost---------");

    }

    private void doLoadConfig(String locations) {
        //spring中是用reader来做定位，这里直接
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(locations.replace("classpath:",""));
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

    private String toLowerFirstCase(String s){
        char[] chars = s.toCharArray();
        chars[0] += 32;

        return chars.toString();
    }
}
