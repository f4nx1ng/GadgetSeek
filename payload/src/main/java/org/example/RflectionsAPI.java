package org.example;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class RflectionsAPI {
    public static Object getObjectNodeByUnsafe(Unsafe unsafe, String ClassName)throws Exception{
        Class clazz = Class.forName(ClassName);
        Object o = unsafe.allocateInstance(clazz);
        return o;
    }

    public static Field getFieldNodeByReflect(String ClassName, String fieldName)throws Exception{
        Class clazz = Class.forName(ClassName);
        Field theField = clazz.getDeclaredField(fieldName);
        theField.setAccessible(true);
        return theField;
    }
}
