package com.blogspot.fuud.java.tests;

import com.blogspot.fuud.java.jautohash.agent.InstrumentationUtils;

import java.util.List;

public class Foo {
    private final int a;

    private final String b;

    private final List<String> c;

    public Foo(int a, String b, List<String> c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        return InstrumentationUtils.equals(this, o);
    }

    @Override
    public int hashCode() {
        return InstrumentationUtils.hashCode(this);
    }


    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Foo)) return false;

        Foo foo = (Foo) o;

        if (a != foo.a) return false;
        if (b != null ? !b.equals(foo.b) : foo.b != null) return false;
        if (c != null ? !c.equals(foo.c) : foo.c != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }*/

    @Override
    public String toString() {
        return "Foo{" +
                "a=" + a +
                ", b='" + b + '\'' +
                ", c=" + c +
                '}';
    }
}
