//package org.example;
//
////import org.example.constants.Constants;
//
//import org.example.util.CommonUtil;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//public class LogAnalyzer {
//
//    public static void main(String[] args) throws IOException {
//        List<File> agentDirectories = CommonUtil.getAgentLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
//        for (File logDirectory : agentDirectories) {
//            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
//            File[] logs = logDirectory.listFiles();
//            for (File log : logs) {
//                System.out.println(String.format("->>>>> %s", log.getName()));
//                List<String> lines = CommonUtil.readLinesFromFile(log.getPath());
//
//                // Fetch 메세지 개수
//                int fetchCounts = lines.parallelStream().filter((line) -> line.contains("[SMS Fetcher] result List size"))
//                                                        .mapToInt((line) -> Integer.parseInt(line.substring(line.indexOf(": ") + 2)))
//                                                        .sum();
//
//                Map<String, Integer> fetchCountMap = lines.stream().filter((line) -> line.contains("[SMS] Fetcher fetch Data: SRC_MSG_ID"))
//                                                                   .map((line) -> line.substring(0,21))
//                                                                   .map((dateTime) -> dateTime.substring(dateTime.length()-10, dateTime.length()-2))
//                                                                   .collect(HashMap::new, (map, key) -> map.merge(key, 1, Integer::sum), HashMap::putAll);
//
//                Map<String, Integer> sendCountMap = lines.stream().filter((line) -> line.contains("[SMS] Send Message Agent >>>>> MCMP, SrcMsgId"))
//                                                                  .map((line) -> line.substring(0,21))
//                                                                  .map((dateTime) -> dateTime.substring(dateTime.length()-10, dateTime.length()-2))
//                                                                  .collect(HashMap::new, (map, key) -> map.merge(key, 1, Integer::sum), HashMap::putAll);
//
//                Map<String, Integer> reseponseCountMap = lines.stream().filter((line) -> line.contains("Agent <<<<< MCMP, SrcMsgId"))
//                                                                       .map((line) -> line.substring(0,21))
//                                                                       .map((dateTime) -> dateTime.substring(dateTime.length()-10, dateTime.length()-2))
//                                                                       .collect(HashMap::new, (map, key) -> map.merge(key, 1, Integer::sum), HashMap::putAll);
//
//
//                System.out.println(String.format("fetchCount : %s", fetchCounts));
//                System.out.println(String.format("fetchCountMap : %s", fetchCountMap));
//                System.out.println(String.format("sendCountMap : %s", sendCountMap));
//                System.out.println(String.format("reseponseCountMap : %s", reseponseCountMap));
//            }
//            System.out.println(String.format("========================================== %s END =============================================", logDirectory.getName()));
//        }
//    }
//
//}
