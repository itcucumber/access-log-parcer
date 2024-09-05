package org.example;
import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int fileCounter = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Файл не существует или указан путь к папке. Попробуйте еще раз.");
                continue;
            }

            fileCounter++;
            System.out.println("Путь указан верно. Это файл номер " + fileCounter);
        }
    }
}