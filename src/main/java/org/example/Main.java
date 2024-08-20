package org.example;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Введите текст и нажмите <Enter>");
        String text = new Scanner(System.in).nextLine();
        System.out.println("Длина текста: " + text.length());
    }
}