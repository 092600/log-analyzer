package org.example.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analyzer {
    public Map<Integer, Integer> analyzeStringFromOnSec(List<String> logs, String searchString) {
        return logs.stream().filter((log) -> log.contains(searchString))
                .map((log) -> log.substring(0,21))
                .map((log) -> log.substring(log.length()-10, log.length()-2))
                .collect(HashMap::new, (map, key) -> map.merge(Integer.parseInt(String.join("", key.split(":"))), 1, Integer::sum), HashMap::putAll);
    }


    public int countingStringFromLogOnSec(List<String> logs, String searchString) {
        return logs.parallelStream().filter((log) -> log.contains(searchString))
                .mapToInt((log) -> Integer.parseInt(log.substring(log.indexOf(": ") + 2)))
                .sum();
    }
}
