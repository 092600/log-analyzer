package org.example.common.util;

import org.example.common.dto.TopContext;

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

    public static List<File> getDirectoriesInFile(String filePath) {
        try {
            File rootDir = new File(filePath);
            File[] directories = Objects.requireNonNull(rootDir.listFiles());

            return Arrays.stream(directories)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            System.out.printf("\"%s\" 가 존재하지 않습니다.%n", filePath);
        }

        return null;
    }

    public static List<File> getFilesInDirectory(String directoryPath) {
        try {
            File rootDir = new File(directoryPath);
            File[] directories = Objects.requireNonNull(rootDir.listFiles());

            return Arrays.stream(directories)
                    .filter(File::isFile)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            System.out.printf("\"%s\" 가 존재하지 않습니다.%n", directoryPath);
        }

        return null;
    }

    public static String getInputText(String text) {
        System.out.printf("%s : ", text);
        return scanner.next();
    }
    public static List<File> getAgentThreadsUsageTopLogDirectories() {
        File rootDir = new File(TOP_ROOT_PATH);
        return Arrays.stream(Objects.requireNonNull(rootDir.listFiles()))
                            .collect(Collectors.toList());
    }


    public static List<String> readLinesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    @SafeVarargs
    public static ConcurrentSkipListSet<String> mergeToSortedSet(Set<String>... sets) {
        return Arrays.stream(sets)
                .flatMap(Collection::stream) // 각 Set을 Stream으로 변환한 후 flatMap으로 합칩니다.
                .collect(Collectors.toCollection(() -> new ConcurrentSkipListSet<String>(Comparator.naturalOrder()))); // ConcurrentSkipListSet으로 수집합니다.
    }


    public static void generateCsv(String filePath, String fileName, Map<String, Long> fetchCountMap, Map<String, Long> sendCountMap, Map<String, Long> responseCountMap) throws IOException {
        Files.createDirectories(Path.of(filePath));

        filePath = String.join("/", filePath, fileName+".csv");
        Set<String> times = CommonUtil.mergeToSortedSet(fetchCountMap.keySet(), sendCountMap.keySet(), responseCountMap.keySet());

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
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Time, saveIOAvgTime, updateIoAvgTime\n");

            Set<String> times = CommonUtil.mergeToSortedSet(saveIOTimeMap.keySet(), updateIOTimeMap.keySet());

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



    public static String exclusiveExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static void  writeTopContextCSV(Collection<TopContext> topContexts, String threadName, String directoryName) {
        directoryName = directoryName.substring(0, directoryName.length() - 4);
        try {
            String filePath = TOP_ROOT_PATH;

            Path path = Path.of(String.join("/", filePath, directoryName));
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }

            String threadCpuFilePath = String.join("/", filePath, directoryName, threadName + ".csv");
            if (!Files.exists(path)) {
                path = Path.of(threadCpuFilePath);
                Files.createFile(path);
            }

            try (FileWriter writer = new FileWriter(threadCpuFilePath)) {
                // CSV 헤더 작성
                writer.append("Time,totalCpuUsage,CpuUsage\n");

                // resultMap 반복
                for (TopContext topContext : topContexts) {
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

    public static int getNumberInput(String text) {
        System.out.printf("%s : ", text);
        return scanner.nextInt();
    }
}
