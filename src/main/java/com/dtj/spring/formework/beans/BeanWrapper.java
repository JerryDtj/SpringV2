package com.dtj.spring.formework.beans;

public class BeanWrapper {
    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    //还会用到  观察者  模式
    //1、支持事件响应，会有一个监听
    private BeanPostProcessor postProcessor;

    private Object wrapperInstance;
    //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;

    public BeanWrapper(Object instance){
        //从这里开始，我们要把动态的代码添加进来了
        this.originalInstance = instance;
    }

    public Object getWrappedInstance(){
        return this.wrapperInstance;
    }


    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }


    public Object getOriginalInstance() {
        return originalInstance;
    }
}
