package org.example;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String logFilePath = "C:/Users/Surface Pro 9/Documents/access.log";
        File logFile = new File(logFilePath);

        if (!logFile.exists()) {
            System.err.println("Файл не найден: " + logFilePath);
            return;
        }
        if (!logFile.isFile()) {
            System.err.println("Указан путь к папке, не является путём к файлу: " + logFilePath);
            return;
        }

        Statistics stats = new Statistics();

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    LogEntry entry = new LogEntry(line);
                    stats.addEntry(entry);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Некорректная строка: " + line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Ошибка проверки пути: " + ex.getMessage());
        }

        System.out.println("Средний трафик за час: " + stats.getTrafficRate() + " байт");
        System.out.println("Список всех существующих страниц: " + stats.getExistPages());
        System.out.println("Список всех несуществующих страниц: " + stats.getNotExistPages());
        System.out.println("Статистика операционных систем пользователей сайта: " + stats.getOsStatistics());
        System.out.println("Статистика браузеров пользователей сайта: " + stats.getBrowserStatistics());
    }

    public static class LogEntry {
        public enum HttpMethod {
            GET, POST, PUT, DELETE, UNKNOWN
        }

        private final String ipAddr;
        private final LocalDateTime time;
        private final HttpMethod method;
        private final String path;
        private final int responseCode;
        private final int responseSize;
        private final String referer;
        private final UserAgent userAgent;

        public LogEntry(String logLine) {
            String logPattern = "(\\S+) - - \\[(.+?)\\] \"(\\S+) (.+?) HTTP/\\d\\.\\d\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*?)\"";
            Pattern pattern = Pattern.compile(logPattern);
            Matcher matcher = pattern.matcher(logLine);

            if (matcher.find()) {
                this.ipAddr = matcher.group(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
                this.time = LocalDateTime.parse(matcher.group(2), formatter);
                this.method = parseHttpMethod(matcher.group(3));
                this.path = matcher.group(4);
                this.responseCode = Integer.parseInt(matcher.group(5));
                this.responseSize = Integer.parseInt(matcher.group(6));
                this.referer = matcher.group(7);
                this.userAgent = new UserAgent(matcher.group(8));
            } else {
                throw new IllegalArgumentException("Некорректный формат строки лога");
            }
        }

        public LocalDateTime getTime() {
            return time;
        }

        public int getResponseSize() {
            return responseSize;
        }

        public String getPath() {
            return path;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public UserAgent getUserAgent() {
            return userAgent;
        }

        private HttpMethod parseHttpMethod(String methodString) {
            try {
                return HttpMethod.valueOf(methodString.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return HttpMethod.UNKNOWN;
            }
        }

        @Override
        public String toString() {
            return "LogEntry{" +
                    "ipAddr='" + ipAddr + '\'' +
                    ", time=" + time +
                    ", method=" + method +
                    ", path='" + path + '\'' +
                    ", responseCode=" + responseCode +
                    ", responseSize=" + responseSize +
                    ", referer='" + referer + '\'' +
                    ", userAgent=" + userAgent +
                    '}';
        }
    }

    public static class UserAgent {
        private final String browser;
        private final String os;

        public UserAgent(String userAgentString) {
            if (userAgentString.contains("Firefox")) {
                browser = "Firefox";
            } else if (userAgentString.contains("Chrome")) {
                browser = "Chrome";
            } else if (userAgentString.contains("Safari")) {
                browser = "Safari";
            } else {
                browser = "Unknown";
            }

            if (userAgentString.contains("Windows")) {
                os = "Windows";
            } else if (userAgentString.contains("Mac")) {
                os = "MacOS";
            } else if (userAgentString.contains("Linux")) {
                os = "Linux";
            } else {
                os = "Unknown";
            }
        }

        public String getBrowser() {
            return browser;
        }

        public String getOs() {
            return os;
        }

        @Override
        public String toString() {
            return "UserAgent{" +
                    "browser='" + browser + '\'' +
                    ", os='" + os + '\'' +
                    '}';
        }
    }

    public static class Statistics {
        private long totalTraffic = 0;
        private LocalDateTime minTime = null;
        private LocalDateTime maxTime = null;
        private final HashSet<String> existPages = new HashSet<>();
        private final HashSet<String> notexistPages = new HashSet<>();
        private final HashMap<String, Integer> osCount = new HashMap<>();
        private final HashMap<String, Integer> browserCount = new HashMap<>();

        public void addEntry(LogEntry entry) {
            if (entry.getResponseSize() < 0) {
                System.err.println("Отрицательный размер ответа: " + entry.getResponseSize());
                return;
            }

            totalTraffic += entry.getResponseSize();

            if (minTime == null || entry.getTime().isBefore(minTime)) {
                minTime = entry.getTime();
            }
            if (maxTime == null || entry.getTime().isAfter(maxTime)) {
                maxTime = entry.getTime();
            }

            if (entry.getResponseCode() == 200) {
                existPages.add(entry.getPath());
            }
            else if (entry.getResponseCode() == 404) {
                notexistPages.add(entry.getPath());
            }

            String os = entry.getUserAgent().getOs();
            osCount.put(os, osCount.getOrDefault(os, 0) + 1);

            String browser = entry.getUserAgent().getBrowser();
            browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1);
        }

        public double getTrafficRate() {
            if (minTime == null || maxTime == null || totalTraffic <= 0) {
                return 0;
            }

            long hours = Duration.between(minTime, maxTime).toHours();
            return hours <= 0 ? totalTraffic : (double) totalTraffic / hours;
        }

        public HashSet<String> getExistPages() {
            return new HashSet<>(existPages);
        }

        public HashSet<String> getNotExistPages() {
            return new HashSet<>(notexistPages);
        }

        public HashMap<String, Double> getOsStatistics() {
            int total = osCount.values().stream().mapToInt(Integer::intValue).sum();
            HashMap<String, Double> osStats = new HashMap<>();
            for (String os : osCount.keySet()) {
                osStats.put(os, (double) osCount.get(os) / total);
            }
            return osStats;
        }

        public HashMap<String, Double> getBrowserStatistics() {
            int total = browserCount.values().stream().mapToInt(Integer::intValue).sum();
            HashMap<String, Double> browserStats = new HashMap<>();
            for (String browser : browserCount.keySet()) {
                browserStats.put(browser, (double) browserCount.get(browser) / total);
            }
            return browserStats;
        }
    }
}
