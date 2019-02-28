package com.changlu;

import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;

/**
 * created by LuChang
 * 2019/2/28 17:21
 */
public class MapperTest {
    
    interface Mapper{
        default void sayHello(){
            System.out.println("Hello,I am a default method of Mapper");
        }
    }


    class TestInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("hello world");
            System.out.println(proxy.getClass().getInterfaces()[0].getName());
            if (isDefaultMethod(method)){
                return invokeDefaultMethod(proxy,method,args);
            }else {
                return method.invoke(this, args);
            }
        }

        private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
                throws Throwable {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            final Class<?> declaringClass = method.getDeclaringClass();
            return constructor
                    .newInstance(declaringClass,
                            MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                    | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                    .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
        }

        private boolean isDefaultMethod(Method method) {
            return (method.getModifiers()
                    & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
                    && method.getDeclaringClass().isInterface();
        }
    }


    @Test
    public void testGenerateProxy() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] constructors = Proxy.getProxyClass(Mapper.class.getClassLoader(), Mapper.class).getConstructors();
        for (Constructor method : constructors){
            System.out.println(method.isAccessible());
            method.setAccessible(true);
            System.out.println(method);
            Object o = method.newInstance(new TestInvocationHandler());
            for (Class clazz : o.getClass().getInterfaces()){
                System.out.println(clazz.getName());
            }
            Mapper mapper = (Mapper) o;
            //拦截此类型的方法转到InvocationHandler的invoke方法
            System.out.println(mapper.toString());
            mapper.sayHello();
        }
    }
    
}
