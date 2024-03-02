package org.example.common.util;

import org.example.common.constants.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CommonUtil {
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
        File rootDir = new File("/Users/simjeonghun/차세대 로그/에이전트 로그");
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

            List<Integer> keys = new ArrayList<>(fetchCountMap.keySet());
            keys.sort(Comparator.naturalOrder());

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

    public static String getAnalyzedDataCsvFileName(String fileName) {
        return String.join("/", Constants.ROOT_PATH, "csv", String.format("%s.csv", fileName));
    }

}
