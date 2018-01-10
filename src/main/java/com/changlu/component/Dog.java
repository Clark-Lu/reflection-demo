package com.changlu.component;

/**
 * Created by changlu on 1/9/18.
 */
public class Dog {

    private int age;

    private String name;

    public void move(String where){
        System.out.println("I am a dog.I am moving to " + where);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
