package org.example;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        String path = "C:/Users/Surface Pro 9/Documents/access.log";

        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException("Файл не найден: " + path);
            }
            if (!file.isFile()) {
                throw new IOException("Указан путь к папке, не является путём к файлу: " + path);
            }

            int totalLines = 0;
            int longestLineLength = 0;
            int shortestLineLength = Integer.MAX_VALUE;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    int length = line.length();

                    if (length > 1024) {
                        throw new LineTooLongException("В файле встретилась строка длиннее 1024 символов - " + length);
                    }

                    longestLineLength = Math.max(longestLineLength, length);
                    shortestLineLength = Math.min(shortestLineLength, length);
                }

                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Длина самой длинной строки в файле: " + longestLineLength);
                System.out.println("Длина самой короткой строки в файле: " + shortestLineLength);
            }

        } catch (LineTooLongException ex) {
            System.err.println("Ошибка количества символов: " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.err.println("Ошибка проверки существования файла: ");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("Ошибка проверки пути: ");
            ex.printStackTrace();
        }
    }
}

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}
