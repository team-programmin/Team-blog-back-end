package com.blog.eu.exepitons.launch;

public class EmailjaEstaEmUso extends RuntimeException{
    public EmailjaEstaEmUso() {
        super("Email já está em uso");
    }
}
