package org.example.analyzer;

import org.example.common.dto.TopContext;
import org.example.common.util.CommonUtil;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Analyzer {

    private static Pattern dbioParsingPattern = Pattern.compile("\\[(\\d+ms)]");

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

    public Map<String, Double> analyzeDbIOAVGFromOnSec(List<String> logs, String s) {
        // 키(sec)와 값(ioTime)으로 구성된 Map을 생성합니다.
        Map<String, List<Long>> map = logs.parallelStream()
                .filter(log -> log.contains(s))
                .map(log -> {
                    String[] arr = log.split("\\s+");

                    String time = arr[1];
                    String sec = time.substring(0, time.lastIndexOf(".") + 2);

                    Matcher matcher = dbioParsingPattern.matcher(log);
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

    public Map<String, ConcurrentSkipListSet<TopContext>> analyzeTopLog(List<String> logs) {
        List<TopContext> contexts = new ArrayList<>();
        String[] topBlock = String.join("\n", logs).split("\n\n");
        for (int i = 3; i < topBlock.length - 1; i+=2) {
            String totalBlock = topBlock[i-1];

            String[] topTotalHeaderBlock = totalBlock.split("\n");
            String[] dateTimeRow = topTotalHeaderBlock[1].split("\\s+");
            String[] totalCpuRow = topTotalHeaderBlock[3].split("\\s+");

            String time = dateTimeRow[2];
            String totalCpuUsage = totalCpuRow[1].split("%us")[0];
            String threadInfoBlock = topBlock[i];

            String[] topThreadRows = threadInfoBlock.split("\n");

            IntStream.rangeClosed(1, topThreadRows.length - 1).forEach(idx -> {
                String[] parts = topThreadRows[idx].split("\\s+");
                String threadName, cpuUsage;
                if (parts.length <= 12) {
                    threadName = parts[11];
                    cpuUsage = parts[8];
                }
                // parts.length > 12
                else {
                    if (parts[0].isEmpty()) {
                        threadName = IntStream.rangeClosed(12, parts.length - 1).mapToObj(innerIndex -> parts[innerIndex]).collect(Collectors.joining(" "));
                        cpuUsage = parts[9];
                    } else {
                        // [27921, smsmt, 20, 0, 6340m, 552m, 12m, S, 0.0, 7.0, 0:00.00, SIGTERM, handler]
                        threadName = IntStream.rangeClosed(11, parts.length - 1).mapToObj(innerIndex -> parts[innerIndex]).collect(Collectors.joining(" "));
                        cpuUsage = parts[8];
                    }
                }

                TopContext topContext = new TopContext(time, totalCpuUsage, cpuUsage, threadName);
                contexts.add(topContext);
            });
        }

        Map<String, ConcurrentSkipListSet<TopContext>> resultMap = new HashMap<>();
        contexts.forEach(topContext -> {
            String threadName = topContext.getThreadName();
            resultMap.computeIfAbsent(threadName, key -> new ConcurrentSkipListSet<TopContext>(Comparator.comparing(TopContext::getTime))).add(topContext);
        });

        return resultMap;
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







}
