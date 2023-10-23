package ru.seminar.homework.hw6.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {

    public String generate() {
        return UUID.randomUUID().toString().substring(0, 4);
    }
}
