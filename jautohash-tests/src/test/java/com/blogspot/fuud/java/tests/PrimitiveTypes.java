package com.blogspot.fuud.java.tests;


import com.blogspot.fuud.java.jautohash.InstrumentationUtils;

public class PrimitiveTypes {
    boolean aBoolean;
    byte aByte;
    char aChar;
    short aShort;
    int anInt;
    long aLong;
    float aFloat;
    double aDouble;

    public PrimitiveTypes(boolean aBoolean, byte aByte, char aChar, short aShort,
                          int anInt, long aLong, float aFloat, double aDouble) {
        this.aBoolean = aBoolean;
        this.aByte = aByte;
        this.aChar = aChar;
        this.aShort = aShort;
        this.anInt = anInt;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
    }

    @Override
    public boolean equals(Object o) {
        return InstrumentationUtils.equals(this, o);
    }

    @Override
    public int hashCode() {
        return InstrumentationUtils.hashCode(this);
    }
}
