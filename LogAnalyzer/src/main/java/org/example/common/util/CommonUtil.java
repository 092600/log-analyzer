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
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.common.constants.Constants.NEW_AGENT_ROOT_PATH;
import static org.example.common.constants.Constants.NEW_AGENT_ROOT_PATH2;

public class CommonUtil {

    public static Scanner scanner = new Scanner(System.in);
    public static void createAgentLogDirectories(int cnt, String... directoryName) {
        for (int i = 1; i <= cnt; i++) {
            String num = i < 10 ? String.format("0%s",i) : String.valueOf(i);

//            File file = new File(String.format(String.join("/", Constants.ROOT_PATH, directoryName, String.format("%d%s", directoryName, num))));
//            if (!file.exists()) {
//                if (file.mkdir()) {
//                    System.out.println(String.format("%s file created", num));
//                }
//            }

        }
    }

    public static List<File> getAgentLogDirectories() {
        File rootDir = new File(NEW_AGENT_ROOT_PATH2);
        return Arrays.stream(rootDir.listFiles())
                .filter((file) -> file.exists() && file.isDirectory() && file.getName().contains("agt"))
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
    public static List<File> getAgentTopLogDirectories() {
        File rootDir = new File("/Users/simjeonghun/차세대 로그/cpu 사용량");
        return Arrays.stream(rootDir.listFiles()).filter((file) -> file.exists() && file.isDirectory() && file.getName().contains("agt")).collect(Collectors.toList());
    }

    public static List<File> getAgentThreadsUsageTopLogDirectories() {
        File rootDir = new File("/Users/simjeonghun/차세대 로그/test");
        return Arrays.stream(rootDir.listFiles()).filter((file) -> file.exists() && file.isDirectory() && file.getName().contains("agt")).collect(Collectors.toList());
    }

    public static List<File> getMessageApiServerLogDirectories() {
        File rootDir = new File("/Users/simjeonghun/차세대 로그/MessageApiServerLogs");
        return Arrays.stream(rootDir.listFiles()).filter((file) -> file.exists() && file.isDirectory() && file.getName().contains("messageApiServer")).collect(Collectors.toList());
    }


    public static List<String> readLinesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static void generateCsv(String fileName, Map<Integer, Integer> fetchCountMap, Map<Integer, Integer> sendCountMap, Map<Integer, Integer> responseCountMap) {
        System.out.println("fileName : " + fileName);
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            writer.append("Time,Fetch Count,Send Count,Response Count\n");

            Set<Integer> fetchKeySet = sendCountMap.keySet();
            Set<Integer> sendKeySet = sendCountMap.keySet();
            Set<Integer> responseKeySet = sendCountMap.keySet();

            Set<Integer> mergedKeySet = new HashSet<Integer>(fetchKeySet);
            mergedKeySet.addAll(sendKeySet);
            mergedKeySet.addAll(responseKeySet);
            List<Integer> keys = mergedKeySet.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

//            List<Integer> keys = new ArrayList<>(fetchCountMap.keySet());
//            keys.sort(Comparator.naturalOrder());

            // Write CSV data
            for (Integer time : keys) {
                writer.append(String.format("%d,%d,%d,%d\n",
                        time,
                        fetchCountMap.getOrDefault(time, 0),
                        sendCountMap.getOrDefault(time, 0),
                        responseCountMap.getOrDefault(time, 0)));
            }

            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateCsv(String fileName, Map<Integer, Integer> sendCountMap, Map<Integer, Integer> responseCountMap) {
        System.out.println("fileName : " + fileName);
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            writer.append("Time,Fetch Count,Send Count,Response Count\n");

            Set<Integer> fetchKeySet = sendCountMap.keySet();
            Set<Integer> sendKeySet = sendCountMap.keySet();
            Set<Integer> responseKeySet = sendCountMap.keySet();

            Set<Integer> mergedKeySet = new HashSet<Integer>(fetchKeySet);
            mergedKeySet.addAll(sendKeySet);
            mergedKeySet.addAll(responseKeySet);
            List<Integer> keys = mergedKeySet.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

//            List<Integer> keys = new ArrayList<>(fetchCountMap.keySet());
//            keys.sort(Comparator.naturalOrder());

            // Write CSV data
            for (Integer time : keys) {
                writer.append(String.format("%d,%d,%d\n",
                        time,
//                        fetchCountMap.getOrDefault(time, 0),
                        sendCountMap.getOrDefault(time, 0),
                        responseCountMap.getOrDefault(time, 0)));
            }

            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static String getAnalyzedDataCsvFileName(String fileName) {
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










    public static void writeTopContextCSV(List<TopContext> topContexts, String threadName, String directoryName) {
        directoryName = directoryName.substring(0, directoryName.length() - 4);
        try {
            String filePath = "/Users/simjeonghun/차세대 로그/test/agt";

            Path path = Path.of(String.format("%s/%s", filePath, directoryName));
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }


            if (!Files.exists(path)) {
                path = Path.of(String.format("%s/%s/%s", filePath, directoryName, threadName + ".csv"));
                Files.createFile(path);
            }

            try (FileWriter writer = new FileWriter(String.format("%s/%s/%s", filePath, directoryName, threadName + ".csv"))) {
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
