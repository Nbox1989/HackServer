package com.aihuishou.hackserver.core.entity;

public class ReflectionAdd {

    public ReflectionAdd(int v) {
        this.value = v;
    }

    public static ReflectionAdd getInstance()  {
        return new ReflectionAdd(0);
    }


    int value = 15;


    public ReflectionAdd newTest(int x) {
        return new ReflectionAdd(value * x);    }



}
