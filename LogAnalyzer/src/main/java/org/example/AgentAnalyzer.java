package org.example;

import com.google.gson.Gson;
import jdk.jfr.Frequency;
import org.example.analyzer.Analyzer;
import org.example.common.type.CommonLog;
import org.example.common.type.apiServer.RequestProcessingTimeEntity;
import org.example.common.util.CommonUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AgentAnalyzer {

    private static final Gson gson = new Gson();
    private static final Analyzer analyzer = new Analyzer();


    public static void main(String[] args) throws IOException {
        CommonUtil.createAgentLogDirectories(6, "agt");
        analyzeAgentLogFileAndCreateCsvFile();
//        analyzeMessageApiServerLogFileAndCreateCsvFile();
    }

    public static void analyzeAgentLogFileAndCreateCsvFile() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        for (File logDirectory : agentDirectories) {
            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".")) {
                    String logFileDateTime = logFile.getName().substring(3, logFile.getName().length() - 4);
                    System.out.println(String.format("->>>>> %s", logFile.getName()));
                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

                    int fetchCounts = analyzer.countingStringFromLogOnSec(logs, "[SMS Fetcher] result List size");

                    Map<Integer, Integer> fetchDatas = analyzer.analyzeStringFromOnSec(logs, "[SMS] Fetcher fetch Data: SRC_MSG_ID");
                    Map<Integer, Integer> sendDatas = analyzer.analyzeStringFromOnSec(logs, "Agent >>>>> MCMP, SrcMsgId");
                    Map<Integer, Integer> responseDatas = analyzer.analyzeStringFromOnSec(logs, "Agent <<<<< MCMP, SrcMsgId");

                    System.out.println(CommonUtil.getAnalyzedDataCsvFileName(logDirectory.getName() + logFileDateTime));
                    CommonUtil.generateCsv(CommonUtil.getAnalyzedDataCsvFileName(logDirectory.getName() + logFileDateTime), fetchDatas, sendDatas, responseDatas);

                    System.out.println(String.format("fetchCount : %s", fetchCounts));
                    System.out.println(String.format("fetchCountMap : %s", fetchDatas));
                    System.out.println(String.format("sendCountMap : %s", sendDatas));
                    System.out.println(String.format("reseponseCountMap : %s", responseDatas));
                }
            }
            System.out.println(String.format("========================================== %s END =============================================", logDirectory.getName()));
        }
    }


    public static void analyzeMessageApiServerLogFileAndCreateCsvFile() throws IOException {
        List<File> directoriesInRootDirectory = CommonUtil.getMessageApiServerLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        for (File directory : directoriesInRootDirectory) {
            System.out.println(String.format("========================================= %s START ============================================", directory.getName()));
            File[] logFiles = directory.listFiles();

            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".")) {
                    System.out.println(String.format("->>>>> %s", logFile.getName()));

//                    System.out.println("logFileDateTime : "+logFileDateTime);


                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());
                    Map<String, RequestProcessingTimeEntity> datas = new HashMap<String, RequestProcessingTimeEntity>();
                    logs.stream().filter((log) -> log.contains("Process Start") || log.contains("Process End"))
                            .forEach((log) -> {
                                boolean isStart = log.contains("Start");
                                CommonLog commonLog = gson.fromJson(log, CommonLog.class);
                                String requestId = commonLog.getRequestId();
                                RequestProcessingTimeEntity entity;
                                if (!datas.containsKey(requestId)) {
                                    entity = new RequestProcessingTimeEntity(requestId, commonLog.getTime());
                                } else {
                                    entity = datas.get(requestId);
                                }

                                if (isStart) {
                                    // Process Start...
                                    String startTime = commonLog.getTime();
                                    entity.setStartDate(startTime);
                                    datas.put(requestId, entity);
                                } else {
                                    // Process End...
                                    String endTime = commonLog.getTime();
                                    entity.setEndDate(endTime);
                                    datas.put(requestId, entity);
                                }

                            });



//                    try (FileWriter writer = new FileWriter("/Users/simjeonghun/차세대 로그/MessageApiServerLogs/csv/dataGroupBy.csv")) {
//                        // Write CSV header
//                        writer.append("백의 자리,카운팅\n");
//
//                        Map<String, Long> countByRange = datas.values().stream()
//                                .filter(data -> data.getStartDate() != null && data.getEndDate() != null)
//                                .mapToLong(data -> (int) data.getDelayTime())
//                                .filter(delayTime -> delayTime >= 100) // 100 미만은 필터링
//                                .mapToObj(delayTime -> ((delayTime / 100) * 100) + " - " + ((delayTime / 100) * 100 + 99))
//                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//                        // Write CSV data
//                        for (Map.Entry entity : countByRange.entrySet()) {
//                            writer.append(String.format("%s,%s\n", entity.getKey(), entity.getValue()));
//                        }
//
//                        System.out.println("CSV file has been created successfully!");
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
                    try (FileWriter writer = new FileWriter("/Users/simjeonghun/차세대 로그/MessageApiServerLogs/csv/FileServerLogAnaylzeDatas.csv")) {
                        // Write CSV header
                        writer.append("logDate,Start-End\n");

                        List<RequestProcessingTimeEntity> entities = datas.values().stream().filter(data -> data.getStartDate() != null && data.getEndDate() != null).collect(Collectors.toList());
                        // Write CSV data
                        for (RequestProcessingTimeEntity entity : entities) {
                            Duration duration = Duration.between(entity.getStartDate(), entity.getEndDate());
                            long milliseconds = duration.toMillis();

//                            System.out.println(String.format("requestId : %s, milli : %s", entity.getRequestId(), milliseconds));
//                            System.out.println(String.format("%s,%s\n", entity.getRequestId(), milliseconds));
                            writer.append(String.format("%s,%s\n", entity.getLogDate(), milliseconds));
                        }

                        System.out.println("CSV file has been created successfully!");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}