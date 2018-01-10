package com.changlu.reflection;



import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by changlu on 1/9/18.
 */
public class ReflectionDemo {
    /**
     * 调用move方法
     * @param obj
     * @param where
     */
    public static void callMoveMethod(Object obj, String where){
        Class cls = obj.getClass();
        try {
//            注释掉的调用方法也可以执行
//            Method method = cls.getMethod("move", String.class);
//            method.invoke(obj, where);
            Method method = cls.getMethod("move",new Class[]{String.class});
            method.invoke(obj,new String[]{where});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在整个继承树中查找方法
     * @param cls
     * @param methodName
     * @param paramType
     * @return
     */
    public static Optional<Method> searchHierarchyForMethod(Class cls, String methodName, Class[] paramType){
        if (cls == null){
            return Optional.empty();
        }
        try {
            return Optional.of(cls.getMethod(methodName, paramType));
        } catch (NoSuchMethodException e) {
            return searchHierarchyForMethod(cls.getSuperclass(), methodName, paramType);
        }
    }

    public static List<Field> getAllField(Class cls){
        List<Field> fields = new LinkedList<>();
        while (cls != null){
            Arrays.asList(cls.getDeclaredFields()).stream().forEach(d -> fields.add(d));
            cls = cls.getSuperclass();
        }
        return fields;
    }

    public static String serializeObject(Object source) throws ParserConfigurationException {
        return serializeHelper(source,new IdentityHashMap<>());
    }

    private static String serializeHelper(Object source, Map table) throws ParserConfigurationException {
        StringBuffer s = new StringBuffer();
        String id = Integer.toString(table.size()); table.put(source, id);
        Class sourceClass = source.getClass();
        s.append(sourceClass.getSimpleName()).append(":{id:").append(id).append(",");
        getAllField(sourceClass).stream().forEach(field -> {
            field.setAccessible(true);
            try {
                s.append(field.getName()).append(":").append(field.get(source)).append(",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        s.append("}");
        return s.toString();
    }
}
