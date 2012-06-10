package com.blogspot.fuud.java.tests;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class JAutoHashTest {
    private final Foo foo1 = new Foo(1, "1", Arrays.asList("foo", "bar"));
    private final Foo foo2 = new Foo(1, "1", Arrays.asList("foo", "bar"));
    private final Foo foo3 = new Foo(2, "1", Arrays.asList("foo", "bar"));
    private final Foo foo4 = new Foo(1, "2", Arrays.asList("foo", "bar"));
    private final Foo foo5 = new Foo(1, "1", Arrays.asList("bar", "foo"));
    private final Foo foo6 = new Foo(1, "1", null);

    private final ExtendsFoo fooExt1 = new ExtendsFoo(1, "1", Arrays.asList("fooExt", "bar"), 1.0);
    private final ExtendsFoo fooExt2 = new ExtendsFoo(1, "1", Arrays.asList("fooExt", "bar"), 1.0);
    private final ExtendsFoo fooExt3 = new ExtendsFoo(2, "1", Arrays.asList("fooExt", "bar"), 1.0);
    private final ExtendsFoo fooExt4 = new ExtendsFoo(1, "2", Arrays.asList("fooExt", "bar"), 1.0);
    private final ExtendsFoo fooExt5 = new ExtendsFoo(1, "1", Arrays.asList("bar", "fooExt"), 1.0);
    private final ExtendsFoo fooExt6 = new ExtendsFoo(1, "1", null, 1.0);
    private final ExtendsFoo fooExt7 = new ExtendsFoo(1, "1", Arrays.asList("fooExt", "bar"), 2.0);

    @Test
    public void testEquals() throws Exception {
        assertEquals(foo1, foo2);
        assertEquals(foo2, foo1);
        assertEquals(foo1, foo1);

        assertFalse(foo3.equals(foo1));
        assertFalse(foo4.equals(foo1));
        assertFalse(foo5.equals(foo1));
        assertFalse(foo6.equals(foo1));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(foo1.hashCode(), foo2.hashCode());
        assertEquals(foo1.hashCode(), foo1.hashCode());

        // next probably can be wrong (non equals objects can have same hashCode).
        // But for well-distributed hash function we can check next conditions

        assertFalse(foo3.hashCode() != foo1.hashCode());
        assertFalse(foo4.hashCode() != foo1.hashCode());
        assertFalse(foo5.hashCode() != foo1.hashCode());
        assertFalse(foo6.hashCode() != foo1.hashCode());
    }

    @Test
    public void testEqualsExtends() throws Exception {
        assertEquals(fooExt1, fooExt2);
        assertEquals(fooExt2, fooExt1);
        assertEquals(fooExt1, fooExt1);

        assertFalse(fooExt3.equals(fooExt1));
        assertFalse(fooExt4.equals(fooExt1));
        assertFalse(fooExt5.equals(fooExt1));
        assertFalse(fooExt6.equals(fooExt1));
        assertFalse(fooExt7.equals(fooExt1));
    }

    @Test
    public void testHashCodeExtends() throws Exception {
        assertEquals(fooExt1.hashCode(), fooExt2.hashCode());
        assertEquals(fooExt1.hashCode(), fooExt1.hashCode());

        // next probably can be wrong (non equals objects can have same hashCode).
        // But for well-distributed hash function we can check next conditions

        assertFalse(fooExt3.hashCode() != fooExt1.hashCode());
        assertFalse(fooExt4.hashCode() != fooExt1.hashCode());
        assertFalse(fooExt5.hashCode() != fooExt1.hashCode());
        assertFalse(fooExt6.hashCode() != fooExt1.hashCode());
        assertFalse(fooExt7.hashCode() != fooExt1.hashCode());
    }
}
