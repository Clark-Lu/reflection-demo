package com.changlu;

import com.changlu.component.Car;
import com.changlu.component.Dog;
import com.changlu.reflection.ReflectionDemo;
import com.oracle.javafx.jmx.json.JSONFactory;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * Created by changlu on 1/9/18.
 */
public class ReflectionDemoTest {
    @Test
    public void testCallMoveMethod(){
        Car car = new Car();
        Dog dog = new Dog();
        ReflectionDemo.callMoveMethod(car, "city");
        ReflectionDemo.callMoveMethod(dog, "forest");
    }

    @Test
    public void testSearchHierarchyForMethod(){
        Dog dog = new Dog();
        Optional<Method> method = ReflectionDemo.searchHierarchyForMethod(dog.getClass(), "hashCode", null);
        if (method.isPresent()){
            try {
                System.out.println(method.get().invoke(dog, null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetAllFields(){
        Dog dog = new Dog();
        dog.setName("Dodo");
        dog.setAge(10);
        ReflectionDemo.getAllField(dog.getClass()).stream().forEach(
                field -> {
                    if (!Modifier.isPublic(field.getModifiers())){
                        field.setAccessible(true);
                    }
                    try {
                        System.out.println(field.get(dog));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void testSerializeObject(){
        Dog dog = new Dog();
        dog.setAge(10);
        dog.setName("Dodo");
        try {
            System.out.println(ReflectionDemo.serializeObject(dog));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
