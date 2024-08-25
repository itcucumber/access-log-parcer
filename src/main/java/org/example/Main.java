package org.example;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner number = new Scanner(System.in);

        // Ввод первого числа
        System.out.print("Введите первое число: ");
        int num1 = number.nextInt();

        // Ввод второго числа
        System.out.print("Введите второе число: ");
        int num2 = number.nextInt();

        // Подсчет суммы
        int sum = num1 + num2;
        System.out.println("Сумма: " + sum);

        // Подсчет разности
        int difference = num1 - num2;
        System.out.println("Разность: " + difference);

        // Подсчет произведения
        int product = num1 * num2;
        System.out.println("Произведение: " + product);

        // Подсчет частного
        if (num2 != 0) {
            double quotient = (double) num1 / num2;
            System.out.println("Частное: " + quotient);
        } else {
            System.out.println("Частное: деление на ноль невозможно");
        }
    }
}