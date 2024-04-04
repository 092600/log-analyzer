package org.example;

import com.google.gson.Gson;
import org.example.analyzer.Analyzer;
import org.example.common.dto.TopContext;
import org.example.common.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.common.constants.Constants.NEW_AGENT_ROOT_PATH;


public class AgentAnalyzer {

    private static final Gson gson = new Gson();
    private static final Analyzer analyzer = new Analyzer();


    public static void main(String[] args) throws IOException {
        createCpuUsageCsvFileFromTopFiles();
        createTPSCsvFileFromNewAgentLogFiles();
        analyzeDBIONewAgentLogFileAndCreateCsvFile();
    }



    public static void createCpuUsageCsvFileFromTopFiles() throws IOException {
        List<File> agentLogFiles = CommonUtil.getAgentThreadsUsageTopLogDirectories().stream().filter(File::isFile).collect(Collectors.toList());

        for (File logFile : agentLogFiles) {
            if (logFile.isFile() && !logFile.getName().startsWith(".") && logFile.getName().endsWith(".log")) {
                List<String> logs = CommonUtil.readLinesFromFile(logFile.getPath());

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

                            TopContext topContext = new TopContext(time, totalCpuUsage, cpuUsage, threadName);
                            contexts.add(topContext);
                        });
                    }

                    Map<String, ConcurrentSkipListSet<TopContext>> resultMap = new HashMap<>();
                    contexts.forEach(topContext -> {
                        String threadName = topContext.getThreadName();
                        resultMap.computeIfAbsent(threadName, key -> new ConcurrentSkipListSet<TopContext>(Comparator.comparing(TopContext::getTime))).add(topContext);
                    });

                    resultMap.keySet().forEach((threadName) -> {
                        ConcurrentSkipListSet<TopContext> topContexts = resultMap.get(threadName);
                        TopContext first = topContexts.pollFirst();

                        CommonUtil.writeTopContextCSV(topContexts, Objects.requireNonNull(first).getThreadName(), logFile.getName());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void createTPSCsvFileFromNewAgentLogFiles() throws IOException {
        // Files :: AGT.log, AGT1.log, AGT2.log, AGT3.log...
        List<File> agentLogFiles = CommonUtil.getAgentLogDirectories().stream()
                                                                        .filter((file) -> !file.getName().startsWith("."))
                                                                        .collect(Collectors.toList());

        IntStream.rangeClosed(0, agentLogFiles.size()).forEach((idx) -> {
            File agentLogFile = agentLogFiles.get(idx);

            try {
                System.out.printf("========================================= %s START ============================================", agentLogFile.getName());
                List<String> logs = CommonUtil.readLinesFromFile(agentLogFile.getCanonicalPath());

                Map<String, Long> fetchDatas = analyzer.analyzeStringFromOnSec(logs, "Fetcher] result List size");
                Map<String, Long> sendDatas = analyzer.analyzeStringFromOnSec(logs, "Agent >>>>> MCMP");
                Map<String, Long> responseDatas = analyzer.analyzeStringFromOnSec(logs, "Agent <<<<< MCMP");

                String fileName = CommonUtil.exclusiveExtension(agentLogFile.getName());
                String filePath = String.join("/", NEW_AGENT_ROOT_PATH, fileName);
//
                CommonUtil.generateCsv(filePath, fileName, fetchDatas, sendDatas, responseDatas);
                System.out.printf("========================================== %s END =============================================\n", agentLogFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });
    }


    public static String junghunbabo()    {
        //From Jung ho
        return "SimJungHunBaBo";

    }

    public static void analyzeDBIONewAgentLogFileAndCreateCsvFile() throws IOException {
        // Files :: AGT.log, AGT1.log, AGT2.log, AGT3.log...
        List<File> agentLogFiles = CommonUtil.getAgentLogDirectories().stream()
                                                                        .filter((file) -> !file.getName().startsWith("."))
                                                                        .collect(Collectors.toList());

        agentLogFiles.parallelStream().forEach((agentLogFile) -> {
            try {
                System.out.printf("->>>>> %s\n", agentLogFile.getName());

                String filePath = agentLogFile.getCanonicalPath().substring(0, agentLogFile.getCanonicalPath().lastIndexOf("/")) + "/" + CommonUtil.exclusiveExtension(agentLogFile.getName());
                String newFileName = CommonUtil.exclusiveExtension(agentLogFile.getName())+"_DBIO.csv";

                List<String> logs = CommonUtil.readLinesFromFile(agentLogFile.getPath());

                Map<String, Double> saveDBIO = Analyzer.analyzeDbIOAVGFromOnSec(logs, "[SMS] Queue Save Update Finished in");
                Map<String, Double> updateDBIO = Analyzer.analyzeDbIOAVGFromOnSec(logs, "Submit Success Update Finished in");

                CommonUtil.generateCsv(filePath, newFileName, saveDBIO, updateDBIO);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}