package org.example.common.util;

import org.example.common.constants.Constants;
import org.example.common.type.TopContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import static org.example.common.constants.Constants.*;

public class CommonUtil {

    public static Scanner scanner = new Scanner(System.in);

    public static List<File> getAgentLogDirectories() {
        File rootDir = new File(NEW_AGENT_ROOT_PATH);
        return Arrays.stream(Objects.requireNonNull(rootDir.listFiles()))
                .filter(File::isFile)
                .filter((file) -> !CommonUtil.exclusiveExtension(file.getName()).startsWith("."))
                .collect(Collectors.toList());
    }

    public static List<File> getDirectories(String parentDirectoryPath) {
        File rootDir = new File(parentDirectoryPath);
        try {
            Objects.requireNonNull(rootDir);
            File[] directories = Objects.requireNonNull(rootDir.listFiles());

            return Arrays.stream(directories)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            System.out.printf("\"%s\" 가 존재하지 않습니다.%n", parentDirectoryPath);
        }

        return null;
    }

    public static String getInputText() {
        return scanner.next();
    }
    public static List<File> getAgentThreadsUsageTopLogDirectories() {
        File rootDir = new File(TOP_ROOT_PATH);
        return Arrays.stream(Objects.requireNonNull(rootDir.listFiles()))
                            .collect(Collectors.toList());
    }

//    public static List<File> getMessageApiServerLogDirectories() {
//        File rootDir = new File("/Users/simjeonghun/차세대 로그/MessageApiServerLogs");
//        return Arrays.stream(rootDir.listFiles()).filter((file) -> file.exists() && file.isDirectory() && file.getName().contains("messageApiServer")).collect(Collectors.toList());
//    }


    public static List<String> readLinesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static Set<String> mergeSet(Set<String>... sets) {
        return Arrays.stream(sets)
                .flatMap(Collection::stream) // 각 Set을 Stream으로 변환한 후 flatMap으로 합칩니다.
                .collect(Collectors.toCollection(() -> new ConcurrentSkipListSet<String>(Comparator.naturalOrder()))); // ConcurrentSkipListSet으로 수집합니다.
    }


    public static void generateCsv(String filePath, String fileName, Map<String, Long> fetchCountMap, Map<String, Long> sendCountMap, Map<String, Long> responseCountMap) throws IOException {
        Files.createDirectories(Path.of(filePath));

        filePath = String.join("/", filePath, fileName+".csv");
        Set<String> times = CommonUtil.mergeSet(fetchCountMap.keySet(), sendCountMap.keySet(), responseCountMap.keySet());

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Time,Fetch Count,Send Count,Response Count\n");  // Write CSV header

            // Write CSV data
            for (String time : times) {
                writer.append(String.format("%s,%s,%s,%s\n",
                        time,
                        fetchCountMap.getOrDefault(time, 0L),
                        sendCountMap.getOrDefault(time, 0L),
                        responseCountMap.getOrDefault(time, 0L)));
            }

            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void generateCsv(String filePath, String fileName, Map<String, Double> saveIOTimeMap, Map<String, Double> updateIOTimeMap) throws IOException {
        Files.createDirectories(Path.of(filePath));

        filePath = String.join("/", filePath, fileName);
        System.out.println("filePath :  " + filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Time, saveIOAvgTime, updateIoAvgTime\n");

            Set<String> times = CommonUtil.mergeSet(saveIOTimeMap.keySet(), updateIOTimeMap.keySet());

//             Write CSV data
            for (String time : times) {
                writer.append(String.format("%s,%.3f,%.3f\n",
                        time,
                        saveIOTimeMap.getOrDefault(time, 0D),
                        updateIOTimeMap.getOrDefault(time, 0D)));
            }

            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAnalyzedDataCsvFileName(String fileName, String filePath) {
        return String.join("/", Constants.ROOT_PATH, "csv", String.format("%s.csv", fileName));
    }



    public static int countingStringFromLogOnSec(List<String> logs, String searchString) {
        return logs.parallelStream().filter((log) -> log.contains(searchString))
                .mapToInt((log) -> Integer.parseInt(log.substring(log.indexOf(": ") + 2)))
                .sum();
    }


    public static Map<Integer, Integer> analyzeStringFromOnSec(List<String> logs, String searchString) {
        logs.stream().filter((log) -> log.contains(searchString))
                .map((log) -> log.substring(0,21))
                .map((log) -> log.substring(log.length()-10, log.length()-2))
                .forEach(System.out::println);

        return logs.stream().filter((log) -> log.contains(searchString))
                .map((log) -> log.substring(0,21))
                .map((log) -> log.substring(log.length()-10, log.length()-2))
                .collect(HashMap::new, (map, key) -> map.merge(Integer.parseInt(String.join("", key.split(":"))), 1, Integer::sum), HashMap::putAll);
    }









    public static String exclusiveExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static void  writeTopContextCSV(Collection<TopContext> topContexts, String threadName, String directoryName) {
        directoryName = directoryName.substring(0, directoryName.length() - 4);
        try {
            String filePath = TOP_ROOT_PATH;

            Path path = Path.of(String.join("/", filePath, directoryName));
            System.out.println("path : "+ path);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }

            String threadCpuFilePath = String.join("/", filePath, directoryName, threadName + ".csv");
            if (!Files.exists(path)) {
                path = Path.of(threadCpuFilePath);
                Files.createFile(path);
            }

            System.out.println("threadCpuFilePath : "+ threadCpuFilePath);
            try (FileWriter writer = new FileWriter(threadCpuFilePath)) {
                // CSV 헤더 작성
                writer.append("Time,totalCpuUsage,CpuUsage\n");

                // resultMap 반복
                for (TopContext topContext : topContexts) {

//                        double cpuUsage =  Float.valueOf(Objects.requireNonNullElse(topContext.getCpuUsage(), "0,0")) / Float.valueOf(Objects.requireNonNullElse(topContext.getTotalCpuUsage(), "0.0"));
                        //                    Float.valueOf(topContext.getTotalCpuUsage());
                        //                    Float.valueOf(topContext.getCpuUsage());
//                        String cpuUsageStr = String.valueOf(cpuUsage);
                    writer.append(String.join(",", topContext.getTime(), topContext.getTotalCpuUsage(), topContext.getCpuUsage()));
                    writer.append("\n");
                }

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
