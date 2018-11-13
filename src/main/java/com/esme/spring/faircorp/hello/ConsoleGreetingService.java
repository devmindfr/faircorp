package com.esme.spring.faircorp.hello;

/**
 * @author devmind
 */
public class ConsoleGreetingService implements GreetingService {
    @Override
    public void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
