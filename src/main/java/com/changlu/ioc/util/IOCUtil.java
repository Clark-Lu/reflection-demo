package com.changlu.ioc.util;

import com.changlu.ioc.annotation.Autowired;
import com.changlu.ioc.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class IOCUtil {

    /**
     * 模拟ApplicationContext，存放bean实例
     */
    private static Map<Class,Object> context = new HashMap<>();

    /**
     * 通过类获取该类的实例bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBeanWithClass(Class<T> clazz){
        if (context.containsKey(clazz)) {
            return (T) context.get(clazz);
        }else {
            return null;
        }
    }

    public static void initContext() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        createBean();
        setAutowired();
    }

    private static void setAutowired() throws IllegalAccessException {
        for (Class clazz : context.keySet()){
            Field[] fields = clazz.getDeclaredFields();
            /**
             * 通过反射设置标注Autowired注解的属性
             */
            for (Field field : fields){
                if (field.getAnnotation(Autowired.class) != null){
                    field.setAccessible(true);
                    field.set(context.get(clazz),context.get(field.getType()));
                }
            }
        }
    }

    private static void createBean() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        List<Class> allClasses = findAllClasses();
        for (Class clazz : allClasses){
            /**
             * 通过反射创建标注Component注解的类的实例，并放到context中
             */
            if (clazz.getAnnotation(Component.class) != null){
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object o = constructor.newInstance();
                context.put(clazz,o);
            }
        }
    }


    /**
     * 获取和main函数所在类同目录及子目录下的所有类
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static final List<Class> findAllClasses() throws IOException, ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        Class mainClass = deduceMainClass();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packageName = mainClass.getPackage().getName();
        Enumeration<URL> resources = classLoader.getResources(packageName.replace(".","/"));
        while (resources.hasMoreElements()){
            URL url = resources.nextElement();
            if ("file".equals(url.getProtocol())){
                String filePath = URLDecoder.decode(url.getPath(),"UTF-8");
                addClasses(packageName,filePath,list);
            }
        }
        return list;
    }

    private static final void addClasses(String packageName,String filePath,List<Class> list) throws ClassNotFoundException {
        File dir = new File(filePath);
        File[] files = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));
        for (File file : files){
            if (file.isDirectory()){
                addClasses(packageName + "." + file.getName(),file.getAbsolutePath(),list);
            }else {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> aClass = Class.forName(className);
                list.add(aClass);
            }
        }
    }

    public static final Class deduceMainClass(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace){
            if ("main".equals(element.getMethodName())){
                try {
                    return Class.forName(element.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
