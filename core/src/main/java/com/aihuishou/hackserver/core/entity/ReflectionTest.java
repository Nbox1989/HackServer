package com.aihuishou.hackserver.core.entity;

public class ReflectionTest {

    public ReflectionTest(int v) {
        this.value = v;
    }

    public static ReflectionTest getInstance()  {
        return new ReflectionTest(0);
    }


    int value = 15;


    public ReflectionTest calculate(int x, ReflectionTest y) {
        return new ReflectionTest(value + x + y.value);
    }

}
