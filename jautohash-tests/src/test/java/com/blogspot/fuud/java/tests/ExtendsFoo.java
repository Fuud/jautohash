package com.blogspot.fuud.java.tests;


import java.util.List;

public class ExtendsFoo extends Foo {
    private final double d;

    public ExtendsFoo(int a, String b, List<String> c, double d) {
        super(a, b, c);
        this.d = d;
    }

    @Override
    public String toString() {
        return "ExtendsFoo{" +
                "d=" + d +
                "super=" + super.toString() +
                '}';
    }
}
