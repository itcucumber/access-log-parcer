package org.example;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        String path = "C:/Users/Surface Pro 9/Documents/access.log";

        int totalReq = 0;
        int googlebotReq = 0;
        int yandexbotReq = 0;

        try {
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                throw new FileNotFoundException("Файл не найден: " + path);
            }
            if (!file.isFile()) {
                throw new IOException("Указан путь к папке, не является путём к файлу: " + path);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    totalReq++;

                    if (line.length() > 1024) {
                        throw new LineTooLongException("Строка длиной более 1024 символов найдена.");
                    }

                    String userAgent = extractUserAgent(line);
                    String botType = extractBotType(userAgent);

                    if ("Googlebot".equals(botType)) {
                        googlebotReq++;
                    } else if ("YandexBot".equals(botType)) {
                        yandexbotReq++;
                    }
                }
            }

            printBotStat(totalReq, googlebotReq, yandexbotReq);

        } catch (FileNotFoundException ex) {
            System.err.println("Ошибка проверки существования файла: ");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("Ошибка проверки пути: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String extractUserAgent(String line) {
        int startIndex = line.indexOf("\"Mozilla");
        int endIndex = line.lastIndexOf("\"");
        if (startIndex != -1 && endIndex != -1) {
            return line.substring(startIndex, endIndex + 1);
        }
        return "";
    }

    private static String extractBotType(String userAgent) {
        int startBracket = userAgent.indexOf('(');
        int endBracket = userAgent.indexOf(')');
        if (startBracket != -1 && endBracket != -1) {
            String[] parts = userAgent.substring(startBracket + 1, endBracket).split(";");
            if (parts.length >= 2) {
                return parts[1].split("/")[0].trim();
            }
        }
        return "";
    }

    private static void printBotStat(int totalReq, int googlebotReq, int yandexbotReq) {
        double googlebotPercent = (double) googlebotReq / totalReq * 100;
        double yandexbotPercent = (double) yandexbotReq / totalReq * 100;

        System.out.println("Общее количество запросов от Googlebot и YandexBot: " + totalReq);
        System.out.println("Доля запросов от Googlebot: " + String.format("%.2f", googlebotPercent) + "%");
        System.out.println("Доля запросов от YandexBot: " + String.format("%.2f", yandexbotPercent) + "%");
    }
}

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}

