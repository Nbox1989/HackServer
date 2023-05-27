package com.aihuishou.hackserver.core.entity;

public class ReflectionTimes {

    public ReflectionTimes(int v) {
        this.value = v;
    }

    public static ReflectionTimes getInstance()  {
        return new ReflectionTimes(1);
    }


    int value = 15;


    public ReflectionTimes newTest(int x) {
        return new ReflectionTimes(value * x);    }



}
