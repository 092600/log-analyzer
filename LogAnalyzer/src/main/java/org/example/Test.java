package org.example;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        String[] names = new String[]{"a1", "a2", "a3"};
        Arrays.stream(names).map((name) -> name.toUpperCase()).forEach(System.out::println);

        Arrays.stream(names).flatMap((name) -> Arrays.stream(name.split(""))).forEach(System.out::println);
    }
}
