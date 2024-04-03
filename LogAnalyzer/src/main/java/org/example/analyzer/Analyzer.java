package org.example.analyzer;

import org.example.dto.SpecificStringCnt;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Analyzer {

    public static Pattern ioParsingPattern = Pattern.compile("\\[(\\d+ms)]");


    public Map<String, Long> countFetchSizeBySec(List<String> logs, String searchString) {
        // -> 2024-04-01 13:29:09.581  INFO(1859646)[mover-scheduling-1] [k.s.ums.service.SmsService:454] [SMS Mover] result List size : 2
        return logs.parallelStream()
                .filter((log) -> log.contains(searchString))
                .map((log) -> {
                    String[] arr = log.split("\\s+");

                    String time = arr[1];
                    String fetchSize = arr[arr.length];

                    return new AbstractMap.SimpleEntry<>(time, fetchSize);
                })
                .collect(Collectors.groupingBy(
                        AbstractMap.SimpleEntry::getKey, // 시간 값을 기준으로 그룹화합니다.
                        Collectors.counting() // 각 그룹의 개수를 카운팅합니다.
                ));
    }

    public Map<String, Long> analyzeStringFromOnSec(List<String> logs, String searchString) {
        return logs.parallelStream()
                .filter((log) -> log.contains(searchString))
                .map((log) -> {
                    String[] arr = log.split("\\s+");
                    return arr[1];
                })
                .collect(Collectors.groupingBy(
                        time -> time.substring(0, time.length() - 2), // 시간 값을 키로 사용하여 그룹화합니다.
                        Collectors.counting() // 각 그룹의 개수를 카운팅합니다.
                ));
    }

//    public Map<Integer, Integer> analyzeDbIOAVGFromOnSec(List<String> logs, String searchString) {
//        logs.stream().filter((log) -> log.contains(searchString)).forEach(log -> {
//                    String datetimeAll = log.substring(0,21);
//                    String datetime = log.substring(0,21);
//                    datetime.substring(datetime.length()-10, datetime.length()-2);
//                    System.out.println(datetime);
//                });
////                .map((log) -> )
////                .map((log) -> log.substring(log.length()-10, log.length()-2))
//
////                .collect(HashMap::new, (map, key) -> map.merge(Integer.parseInt(String.join("", key.split(":"))), 1, Integer::sum), HashMap::putAll);
//    }






    public static Map<String, Double> analyzeDbIOAVGFromOnSec(List<String> logs, String s) {
        // 키(sec)와 값(ioTime)으로 구성된 Map을 생성합니다.
        Map<String, List<Long>> map = logs.parallelStream()
                .filter(log -> log.contains(s))
                .map(log -> {
                    String[] arr = log.split("\\s+");

                    String time = arr[1];
                    String sec = time.substring(0, time.lastIndexOf(".") + 2);

                    Matcher matcher = ioParsingPattern.matcher(log);
                    long ioTime = matcher.find() ? Long.parseLong(matcher.group(1).replaceAll("ms", "")) : 0L;

                    return new AbstractMap.SimpleEntry<>(sec, ioTime);
                })
                .collect(Collectors.groupingBy(
                        AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())
                ));

        // 각 키에 대해 값의 평균을 계산하여 새로운 Map을 생성합니다.
        Map<String, Double> averageMap = new HashMap<>();
        map.forEach((sec, ioTimes) -> {
            double average = ioTimes.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            averageMap.put(sec, average);
        });

        return averageMap;
    }
}
