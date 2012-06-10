package com.blogspot.fuud.java.tests;


import com.blogspot.fuud.java.jautohash.InstrumentationUtils;

public class ForLoadTest {
    boolean aBoolean;
    byte aByte;
    char aChar;
    short aShort;
    int anInt;
    long aLong;
    float aFloat;
    double aDouble;
    Object object;
    Object nullObject;

    public ForLoadTest(boolean aBoolean, byte aByte, char aChar, short aShort,
                       int anInt, long aLong, float aFloat, double aDouble,
                       Object object, Object nullObject) {
        this.aBoolean = aBoolean;
        this.aByte = aByte;
        this.aChar = aChar;
        this.aShort = aShort;
        this.anInt = anInt;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.object = object;
        this.nullObject = nullObject;
    }

    @Override
    public boolean equals(Object o) {
        return InstrumentationUtils.equals(this, o);
    }

    @Override
    public int hashCode() {
        return InstrumentationUtils.hashCode(this);
    }

    public boolean equals_ide(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForLoadTest)) return false;

        ForLoadTest that = (ForLoadTest) o;

        if (aBoolean != that.aBoolean) return false;
        if (aByte != that.aByte) return false;
        if (aChar != that.aChar) return false;
        if (Double.compare(that.aDouble, aDouble) != 0) return false;
        if (Float.compare(that.aFloat, aFloat) != 0) return false;
        if (aLong != that.aLong) return false;
        if (aShort != that.aShort) return false;
        if (anInt != that.anInt) return false;
        if (nullObject != null ? !nullObject.equals(that.nullObject) : that.nullObject != null) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;

        return true;
    }

    public int hashCode_ide() {
        int result;
        long temp;
        result = (aBoolean ? 1 : 0);
        result = 31 * result + (int) aByte;
        result = 31 * result + (int) aChar;
        result = 31 * result + (int) aShort;
        result = 31 * result + anInt;
        result = 31 * result + (int) (aLong ^ (aLong >>> 32));
        result = 31 * result + (aFloat != +0.0f ? Float.floatToIntBits(aFloat) : 0);
        temp = aDouble != +0.0d ? Double.doubleToLongBits(aDouble) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (nullObject != null ? nullObject.hashCode() : 0);
        return result;
    }
}
