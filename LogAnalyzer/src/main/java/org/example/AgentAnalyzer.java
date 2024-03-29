package org.example;

import com.google.gson.Gson;
import org.example.analyzer.Analyzer;
import org.example.common.type.CommonLog;
import org.example.common.type.TopContext;
import org.example.common.type.apiServer.RequestProcessingTimeEntity;
import org.example.common.type.cpu.TopAnalyzeData;
import org.example.common.util.CommonUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.common.type.cpu.CpuUtil.*;


public class AgentAnalyzer {

    private static final Gson gson = new Gson();
    private static final Analyzer analyzer = new Analyzer();


    public static void main(String[] args) throws IOException {
//        CommonUtil.createAgentLogDirectories(1, "agt");
//        analyzeThreadsCpuUsageInTopLogAndCreateCsvFile();
        analyzeThreadsCpuUsageInTopLogAndCreateCsvFile2();
//        analyzeAgentTopLogAndCreateCsvFile();
//        analyzeAgentTopLogAndCreateCsvFile2();
//        analyzeLegacyAgentLogFileAndCreateCsvFile();
        analyzeNewAgentLogFileAndCreateCsvFile();
//        analyzeAgentLogFileAndCreateCsvFile();
//        analyzeMessageApiServerLogFileAndCreateCsvFile();
    }


    public static void analyzeThreadsCpuUsageInTopLogAndCreateCsvFile() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentThreadsUsageTopLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        System.out.println("agentDirectories : "+ agentDirectories);
        for (File logDirectory : agentDirectories) {
//            System.out.println(logDirectory.getName());
//            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            System.out.println(Arrays.toString(logFiles));
            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".") && logFile.getName().endsWith(".log")) {
                    System.out.println(String.format("->>>>> %s, %s", logFile.getName(), logFile.getPath()));
                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

                    System.out.println(logs);

                    try {
                        List<TopContext> contexts = new ArrayList<>();
                        String[] topBlock = String.join("\n", logs).split("\n\n");
                        for (int i = 3; i < topBlock.length - 1; i+=2) {
                            String totalBlock = topBlock[i-1];
                            System.out.printf("%s\n\n", totalBlock);

                            String[] topTotalHeaderBlock = totalBlock.split("\n");
                            String[] dateTimeRow = topTotalHeaderBlock[1].split("\\s+");
                            String[] totalCpuRow = topTotalHeaderBlock[3].split("\\s+");

                            String time = dateTimeRow[2];
                            String totalCpuUsage = totalCpuRow[1].split("%us")[0];
                            String threadInfoBlock = topBlock[i];

                            String[] topThreadRows = threadInfoBlock.split("\n");
//                        System.out.println(Arrays.toString(topThreadRows));

                            IntStream.rangeClosed(1, topThreadRows.length - 1).forEach(idx -> {
                                String[] parts = topThreadRows[idx].split("\\s+");
                                System.out.println(Arrays.toString(parts));

                                String threadName = parts.length < 12 ? IntStream.rangeClosed(12, parts.length - 1).mapToObj(innerIndex -> parts[innerIndex]).collect(Collectors.joining(" ")) : parts[11];
                                String cpuUsage = parts[8];

                                System.out.printf("%s, %s, %s, %s\n\n\n", time, totalCpuUsage, threadName, cpuUsage);

                                TopContext topContext = new TopContext(time, totalCpuUsage, cpuUsage, threadName);
                                contexts.add(topContext);
                            });
                        }

                        Map<String, List<TopContext>> resultMap = contexts.stream().collect(Collectors.groupingBy(TopContext::getThreadName));
                        contexts.stream().forEach(topContext -> {
                            String threadName = topContext.getThreadName();
                            resultMap.computeIfAbsent(threadName, key -> new ArrayList<>()).add(topContext);
                        });

                        for (List<TopContext> topContexts : resultMap.values()) {
                            CommonUtil.writeTopContextCSV(topContexts, topContexts.get(0).getThreadName(), logFile.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void analyzeThreadsCpuUsageInTopLogAndCreateCsvFile2() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentThreadsUsageTopLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        System.out.println("agentDirectories : "+ agentDirectories);
        for (File logDirectory : agentDirectories) {
//            System.out.println(logDirectory.getName());
//            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            System.out.println(Arrays.toString(logFiles));
            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".") && logFile.getName().endsWith(".log")) {
                    System.out.println(String.format("->>>>> %s, %s", logFile.getName(), logFile.getPath()));
                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

//                    System.out.println(logs);

                    try {
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
//                        System.out.println(Arrays.toString(topThreadRows));

                            IntStream.rangeClosed(1, topThreadRows.length - 1).forEach(idx -> {
                                String[] parts = topThreadRows[idx].split("\\s+");
                                System.out.println(Arrays.toString(parts));

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

                                System.out.printf("%s, %s, %s, %s\n\n\n", time, totalCpuUsage, threadName, cpuUsage);

                                TopContext topContext = new TopContext(time, totalCpuUsage, cpuUsage, threadName);
                                contexts.add(topContext);
                            });
                        }

                        Map<String, List<TopContext>> resultMap = contexts.stream().collect(Collectors.groupingBy(TopContext::getThreadName));
                        contexts.stream().forEach(topContext -> {
                            String threadName = topContext.getThreadName();
                            resultMap.computeIfAbsent(threadName, key -> new ArrayList<>()).add(topContext);
                        });

                        for (List<TopContext> topContexts : resultMap.values()) {
                            CommonUtil.writeTopContextCSV(topContexts, topContexts.get(0).getThreadName(), logFile.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    public static void analyzeAgentTopLogAndCreateCsvFile2() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentTopLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        for (File logDirectory : agentDirectories) {
//            System.out.println(logDirectory.getName());
//            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".")) {

                    String memFileNameToCsvFile = logFile.getPath().substring(0, logFile.getPath().length() - 4) + "_memory.csv";
                    String logFileNameToCsvFile = logFile.getPath().substring(0, logFile.getPath().length() - 4) + ".csv";
                    System.out.println(String.format("->>>>> %s, %s", logFile.getName(), logFile.getPath()));

                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

                    List<String> times = logs.stream().filter(log -> log.startsWith("top -")).collect(Collectors.toList()).stream().map(log -> log.split("top - ")[1].split(" up")[0]).collect(Collectors.toList());
                    List<String> cpus = logs.stream().filter(log -> log.startsWith("%Cpu(s):  ")).collect(Collectors.toList()).stream().map(log -> log.substring(9, log.indexOf(" us"))).collect(Collectors.toList());
                    Path logFilePah = Path.of(logFileNameToCsvFile);
//                    System.out.println("logFilePah : "+logFilePah);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePah.toFile()))) {
                        writer.write("Time, cpu usage\n");

                        for (int i = 0; i < times.size(); i++) {
//                            System.out.println(String.format("%s, %s\n", times.get(i), cpus.get(i)));
                            writer.write(String.format("%s, %s\n", times.get(i), cpus.get(i)));
                        }
                    } catch (Exception e) {

                    }

                    Path path = Path.of(memFileNameToCsvFile);
//
//                    logs.stream().filter(log -> log.startsWith("top - ") || log.startsWith("KiB Mem")).forEach(System.out::println);
                    List<String> memDatas = logs.stream().filter(log -> log.startsWith("top - ") || log.startsWith("KiB Mem")).collect(Collectors.toList());

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
//                        // 헤더 작성 (선택 사항)
                        writer.write("Time, total-memory, used-memory, free memory\n");

                        for (int i = 0; i < memDatas.size(); i += 2) {
                            int startIdx = memDatas.get(i).indexOf("- ");
                            int endIdx = memDatas.get(i).indexOf("up");

                            String date = memDatas.get(i).substring(startIdx + 2, endIdx);

                            int idx1 = memDatas.get(i + 1).indexOf("KiB Mem : ");
                            int idx2 = memDatas.get(i + 1).indexOf(" total");
                            String memTotal = memDatas.get(i + 1).substring(idx1 + 10, idx2);
                            String freeMem = memDatas.get(i + 1).split("total, ")[1].split(" free")[0];
                            String usedMem = memDatas.get(i + 1).split("free, ")[1].split(" used")[0];

                            writer.write(String.format("%s, %s, %s, %s\n", date, memTotal, usedMem, freeMem));
                        }
                    }
                }
            }
        }
    }


    public static void analyzeAgentTopLogAndCreateCsvFile() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentTopLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        for (File logDirectory : agentDirectories) {
//            System.out.println(logDirectory.getName());
//            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".")) {

                    String memFileNameToCsvFile = logFile.getPath().substring(0, logFile.getPath().length() - 4) + "_memory.csv";
                    String logFileNameToCsvFile = logFile.getPath().substring(0, logFile.getPath().length() - 4) + ".csv";
                    System.out.println(String.format("->>>>> %s, %s", logFile.getName(), logFile.getPath()));

                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());
                    List<String> datas = logs.stream().filter(log -> log.startsWith("top - ") || log.startsWith("Cpu(s): "))
                            .map(log -> {
                                if (log.startsWith("top - ")) {
                                    int startIdx = log.indexOf("- ");
                                    int endIdx = log.indexOf("up");

//                                    System.out.println(String.format("startIdx : %s, endIdx : %s, %s", startIdx, endIdx, log.substring(startIdx + 2, endIdx -1)));
                                    return log.substring(startIdx + 2, endIdx);
                                } else {
                                    int startIdx = log.indexOf(": ");
                                    int endIdx = log.indexOf("us");
//
                                    return log.substring(startIdx + 2, endIdx - 1);
                                }
                            }).collect(Collectors.toList());

                    File csvFile = new File(logFileNameToCsvFile);
                    if (!csvFile.exists()) {
                        csvFile.createNewFile();
                    }

                    List<String> times = new ArrayList<>();
                    List<String> cpus = new ArrayList<>();
                    for (int i = 0; i < datas.size(); i += 2) {
                        System.out.println(String.format("%s, %s", datas.get(i), datas.get(i + 1)));
                        String time = datas.get(i);
                        String cpu = datas.get(i + 1);

                        times.add(time);
                        cpus.add(cpu);
                    }
                    Path logFilePah = Path.of(logFileNameToCsvFile);
                    System.out.println("logFilePah : " + logFilePah);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePah.toFile()))) {
                        writer.write("Time, cpu usage\n");

                        for (int i = 0; i < times.size(); i++) {
//                            System.out.println(String.format("%s, %s\n", times.get(i), cpus.get(i)));
                            writer.write(String.format("%s, %s\n", times.get(i), cpus.get(i)));
                        }
                    } catch (Exception e) {

                    }
//
                    Path path = Path.of(memFileNameToCsvFile);
//
                    String all = String.join("\n", logs);
                    String[] memDatas = all.split("\n\n\n");


                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
//                        // 헤더 작성 (선택 사항)
                        writer.write("Time, total memory, used memory, free memory\n");

                        for (String data : memDatas) {
                            String[] rows = data.split("\n");

                            int startIdx = rows[0].indexOf("- ");
                            int endIdx = rows[0].indexOf("up");
//                        System.out.println(Arrays.toString(rows));
//                            System.out.println(String.format("idx1 : %s, idx2 : %s", startIdx, endIdx));
                            String date = rows[0].substring(startIdx + 2, startIdx + 10);

                            int idx1 = rows[3].indexOf("Mem:   ");
                            int idx2 = rows[3].indexOf(" total");
                            String memTotal = rows[3].substring(idx1 + 7, idx2 - 1);

                            int idx3 = rows[3].indexOf("total,  ");
                            int idx4 = rows[3].indexOf("k used");
                            String usedMem = rows[3].substring(idx3 + 8, idx4);

                            int idx5 = rows[3].indexOf("used,  ");
                            int idx6 = rows[3].indexOf("k free");
                            String freeMem = rows[3].substring(idx5 + 7, idx6);
//
                            writer.write(String.format("%s, %s, %s, %s\n", date, memTotal, usedMem, freeMem));
//                            System.out.println(String.format("time : %s, memory total : %s, usedMem : %s, freeMem : %s", date, memTotal, usedMem, freeMem));
                        }
                    }
                }
            }
        }
    }

    public static void analyzeNewAgentLogFileAndCreateCsvFile() throws IOException {
        List<File> agentDirectories = CommonUtil.getAgentLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
        for (File logDirectory : agentDirectories) {
            System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
            File[] logFiles = logDirectory.listFiles();

            for (File logFile : logFiles) {
                if (logFile.isFile() && !logFile.getName().startsWith(".")) {
                    String logFileDateTime = logFile.getName().substring(3, logFile.getName().length() - 4);
                    System.out.println(String.format("->>>>> %s", logFile.getName()));
                    List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

                    int fetchCounts = analyzer.countingStringFromLogOnSec(logs, "result List size");

                    Map<Integer, Integer> fetchDatas = analyzer.analyzeStringFromOnSec(logs, "Fetcher fetch Data");
                    Map<Integer, Integer> sendDatas = analyzer.analyzeStringFromOnSec(logs, "Agent >>>>> MCMP");
                    Map<Integer, Integer> responseDatas = analyzer.analyzeStringFromOnSec(logs, "Agent <<<<< MCMP");

                    System.out.println(CommonUtil.getAnalyzedDataCsvFileName(logDirectory.getName() + logFileDateTime));
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


//            public static void analyzeLegacyAgentLogFileAndCreateCsvFile () throws IOException {
//                List<File> agentDirectories = CommonUtil.getAgentLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
//                for (File logDirectory : agentDirectories) {
//                    System.out.println(String.format("========================================= %s START ============================================", logDirectory.getName()));
//                    File[] logFiles = logDirectory.listFiles();
//
//                    for (File logFile : logFiles) {
//                        if (logFile.isFile() && !logFile.getName().startsWith(".")) {
//                            String logFileDateTime = logFile.getName().substring(3, logFile.getName().length() - 4);
//                            System.out.println(String.format("->>>>> %s", logFile.getName()));
//                            System.out.println(logFile.getPath());
//                            List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());
////
//                            int fetchCounts = analyzer.countingStringFromLogOnSec(logs, "result List size");
//
////                    Map<Integer, Integer> fetchDatas = analyzer.analyzeStringFromOnSec(logs, "Fetcher fetch Data");
//                            Map<Integer, Integer> sendDatas = analyzer.analyzeStringFromOnSec(logs, "SENT >>>>> SUBMIT(seqNo");
//                            Map<Integer, Integer> responseDatas = analyzer.analyzeStringFromOnSec(logs, "RCVD <<<<< REPORT(msgId");
//
//                            System.out.println(CommonUtil.getAnalyzedDataCsvFileName(logDirectory.getName() + logFileDateTime));
//                            CommonUtil.generateCsv(CommonUtil.getAnalyzedDataCsvFileName(logDirectory.getName() + logFileDateTime), sendDatas, responseDatas);
//
//                            System.out.println(String.format("SENT >>>>> SUBMIT(seqNo : %s", sendDatas));
//                            System.out.println(String.format("RCVD <<<<< REPORT(msgId : %s", responseDatas));
//                        }
//                    }
//                    System.out.println(String.format("========================================== %s END =============================================", logDirectory.getName()));
//                }
//            }


//            public static void analyzeMessageApiServerLogFileAndCreateCsvFile () throws IOException {
//                List<File> directoriesInRootDirectory = CommonUtil.getMessageApiServerLogDirectories().stream().filter((directory) -> directory.listFiles().length != 0).collect(Collectors.toList());
//                for (File directory : directoriesInRootDirectory) {
//                    System.out.println(String.format("========================================= %s START ============================================", directory.getName()));
//                    File[] logFiles = directory.listFiles();
//
//                    for (File logFile : logFiles) {
//                        if (logFile.isFile() && !logFile.getName().startsWith(".")) {
//                            System.out.println(String.format("->>>>> %s", logFile.getName()));
//
////                    System.out.println("logFileDateTime : "+logFileDateTime);
//
//
//                            List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());
//                            Map<String, RequestProcessingTimeEntity> datas = new HashMap<String, RequestProcessingTimeEntity>();
//                            logs.stream().filter((log) -> log.contains("Process Start") || log.contains("Process End"))
//                                    .forEach((log) -> {
//                                        boolean isStart = log.contains("Start");
//                                        CommonLog commonLog = gson.fromJson(log, CommonLog.class);
//                                        String requestId = commonLog.getRequestId();
//                                        RequestProcessingTimeEntity entity;
//                                        if (!datas.containsKey(requestId)) {
//                                            entity = new RequestProcessingTimeEntity(requestId, commonLog.getTime());
//                                        } else {
//                                            entity = datas.get(requestId);
//                                        }
//
//                                        if (isStart) {
//                                            // Process Start...
//                                            String startTime = commonLog.getTime();
//                                            entity.setStartDate(startTime);
//                                            datas.put(requestId, entity);
//                                        } else {
//                                            // Process End...
//                                            String endTime = commonLog.getTime();
//                                            entity.setEndDate(endTime);
//                                            datas.put(requestId, entity);
//                                        }
//
//                                    });
//
//
////                    try (FileWriter writer = new FileWriter("/Users/simjeonghun/차세대 로그/MessageApiServerLogs/csv/dataGroupBy.csv")) {
////                        // Write CSV header
////                        writer.append("백의 자리,카운팅\n");
////
////                        Map<String, Long> countByRange = datas.values().stream()
////                                .filter(data -> data.getStartDate() != null && data.getEndDate() != null)
////                                .mapToLong(data -> (int) data.getDelayTime())
////                                .filter(delayTime -> delayTime >= 100) // 100 미만은 필터링
////                                .mapToObj(delayTime -> ((delayTime / 100) * 100) + " - " + ((delayTime / 100) * 100 + 99))
////                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
////                        // Write CSV data
////                        for (Map.Entry entity : countByRange.entrySet()) {
////                            writer.append(String.format("%s,%s\n", entity.getKey(), entity.getValue()));
////                        }
////
////                        System.out.println("CSV file has been created successfully!");
////
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////
//                            try (FileWriter writer = new FileWriter("/Users/simjeonghun/차세대 로그/MessageApiServerLogs/csv/FileServerLogAnaylzeDatas.csv")) {
//                                // Write CSV header
//                                writer.append("logDate,Start-End\n");
//
//                                List<RequestProcessingTimeEntity> entities = datas.values().stream().filter(data -> data.getStartDate() != null && data.getEndDate() != null).collect(Collectors.toList());
//                                // Write CSV data
//                                for (RequestProcessingTimeEntity entity : entities) {
//                                    Duration duration = Duration.between(entity.getStartDate(), entity.getEndDate());
//                                    long milliseconds = duration.toMillis();
////                            System.out.println(String.format("requestId : %s, milli : %s", entity.getRequestId(), milliseconds));
////                            System.out.println(String.format("%s,%s\n", entity.getRequestId(), milliseconds));
//                                    writer.append(String.format("%s,%s\n", entity.getLogDate(), milliseconds));
//                                }
//
//                                System.out.println("CSV file has been created successfully!");
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
}