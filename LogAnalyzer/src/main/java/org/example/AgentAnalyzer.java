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

                Map<String, ConcurrentSkipListSet<TopContext>> resultMap = analyzer.analyzeTopLog(logs);
                resultMap.keySet().forEach((threadName) -> {
                    ConcurrentSkipListSet<TopContext> topContexts = resultMap.get(threadName);
                    TopContext first = topContexts.pollFirst();

                    CommonUtil.writeTopContextCSV(topContexts, Objects.requireNonNull(first).getThreadName(), logFile.getName());
                });

            }
        }
    }


    public static void createTPSCsvFileFromNewAgentLogFiles() throws IOException {
        // Files :: AGT.log, AGT1.log, AGT2.log, AGT3.log...
        List<File> agentLogFiles = CommonUtil.getAgentLogDirectories().stream()
                                                                        .filter((file) -> !file.getName().startsWith("."))
                                                                        .collect(Collectors.toList());

        IntStream.rangeClosed(0, agentLogFiles.size() - 1).forEach((idx) -> {
            File agentLogFile = agentLogFiles.get(idx);

            try {
                List<String> logs = CommonUtil.readLinesFromFile(agentLogFile.getCanonicalPath());

                Map<String, Long> fetchDatas = analyzer.analyzeStringFromOnSec(logs, "Fetcher] result List size");
                Map<String, Long> sendDatas = analyzer.analyzeStringFromOnSec(logs, "Agent >>>>> MCMP");
                Map<String, Long> responseDatas = analyzer.analyzeStringFromOnSec(logs, "Agent <<<<< MCMP");

                String fileName = CommonUtil.exclusiveExtension(agentLogFile.getName());
                String filePath = String.join("/", NEW_AGENT_ROOT_PATH, fileName);
//
                CommonUtil.generateCsv(filePath, fileName, fetchDatas, sendDatas, responseDatas);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });
    }


    public static void analyzeDBIONewAgentLogFileAndCreateCsvFile() throws IOException {
        // Files :: AGT.log, AGT1.log, AGT2.log, AGT3.log...
        List<File> agentLogFiles = CommonUtil.getAgentLogDirectories().stream()
                                                                        .filter((file) -> !file.getName().startsWith("."))
                                                                        .collect(Collectors.toList());

        agentLogFiles.parallelStream().forEach((agentLogFile) -> {
            try {
                String filePath = agentLogFile.getCanonicalPath().substring(0, agentLogFile.getCanonicalPath().lastIndexOf("/")) + "/" + CommonUtil.exclusiveExtension(agentLogFile.getName());
                String newFileName = CommonUtil.exclusiveExtension(agentLogFile.getName())+"_DBIO.csv";

                List<String> logs = CommonUtil.readLinesFromFile(agentLogFile.getPath());

                Map<String, Double> saveDBIO = analyzer.analyzeDbIOAVGFromOnSec(logs, "[SMS] Queue Save Update Finished in");
                Map<String, Double> updateDBIO = analyzer.analyzeDbIOAVGFromOnSec(logs, "Submit Success Update Finished in");

                CommonUtil.generateCsv(filePath, newFileName, saveDBIO, updateDBIO);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}