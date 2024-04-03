package org.example.logType;

import org.example.analyzeDataType.AgentDataType;
import org.example.common.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.common.constants.Constants.NEW_AGENT_ROOT_PATH;
import static org.example.common.constants.Constants.TOP_ROOT_PATH;



public enum LogType {
    NEW_AGENT_LOG(1, NEW_AGENT_ROOT_PATH),
    TOP_LOG(2, TOP_ROOT_PATH),
    UNKNOWN(100, null),
    EXIT(9999, null);

    public int typeNum;
    public String rootPath;

    LogType(int typeNum, String rootPath) {
        this.typeNum = typeNum;
        this.rootPath = rootPath;
    }

    public static LogType getByTypeNum(int typeNum) {
        for (LogType analyzeType : LogType.values()) {
            if (analyzeType.typeNum == typeNum) {
                return analyzeType;
            }
        }
        return UNKNOWN; // 해당하는 typeNum을 가진 AnalyzeType이 없는 경우
    }

    public void analyze(LogType logType, List<File> logFiles) {
        System.out.printf("%s 을 선택하셨습니다.\n", logType);

        switch (logType) {
            case NEW_AGENT_LOG:
                List<AgentDataType> selectedAgentDataTypes = List.of(AgentDataType.values());
                List<AgentDataType> agentDataTypes = List.of(AgentDataType.values());


                System.out.printf("조회하고싶은 로그를 선택해주세요 : %s", IntStream.rangeClosed(0, AgentDataType.values().length - 1)
                        .mapToObj(idx -> {
                            if (idx == agentDataTypes.size() - 1) {
                                return String.format("%s. %s : ", idx + 1, agentDataTypes.get(idx));
                            } else {
                                return String.format("%s. %s, ", idx + 1, agentDataTypes.get(idx));
                            }
                        })
                        .collect(Collectors.joining()));
                String theme = CommonUtil.getInputText();
                while (theme.equals(AgentDataType.EXIT) || selectedAgentDataTypes.size() == agentDataTypes.size()) {
//                    selectedAgentDataTypes.add(theme);
                }

                System.out.println(theme);
//                AgentDataType[] analyzeAgentDataType = new AgentDataType[agentDataTypes.length];
//                analyzeNewAgentLog(logFiles);
                break;

            case TOP_LOG:
//                analyzeTopLog(logFiles);
                break;

        }
    }

    public void analyzeNewAgentLog(List<File> logFiles) {
        for (File logFile : logFiles) {
//            try {
                String fileName = CommonUtil.exclusiveExtension(logFile.getName());
                String filePath = logFile.getPath().substring(logFile.getPath().lastIndexOf(fileName));

                System.out.println("fileName : "+ fileName + ", filePath : " + filePath);
//                System.out.printf("\"%s\" 디렉토리 안의 %s 파일 분석을 시작합니다.", logFile.getCanonicalPath(), logFile.getName());
//
//                List<String> rows = CommonUtil.readLinesFromFile(logFile.getPath());
//                int fetchCounts = CommonUtil.countingStringFromLogOnSec(rows, "result List size");
//
//                Map<Integer, Integer> fetchDatas = CommonUtil.analyzeStringFromOnSec(rows, "Fetcher fetch Data");
//                Map<Integer, Integer> sendDatas = CommonUtil.analyzeStringFromOnSec(rows, "Agent >>>>> MCMP");
//                Map<Integer, Integer> responseDatas = CommonUtil.analyzeStringFromOnSec(rows, "Agent <<<<< MCMP");
//
//                String
//                CommonUtil.generateCsv(String.join("/", NEW_AGENT_ROOT_PATH, "csv", logFile.getName().replace(".log", ".csv")), fetchDatas, sendDatas, responseDatas);
//            } catch (IOException e) {
//                System.out.println("분석 파일 생성 중 에러 발생");
//            }

        }
    }

//    public void analyzeTopLog(List<File> logFiles) {
//        try {
//            for (File logFile : logFiles) {
//                List<String> rows = CommonUtil.readLinesFromFile(logFile.getPath());
//                List<String> datas = rows.stream().filter(log -> log.startsWith("top - ") || log.startsWith("Cpu(s): "))
//                        .map(log -> {
//                            if (log.startsWith("top - ")) {
//                                int startIdx = log.indexOf("- ");
//                                int endIdx = log.indexOf("up");
//
////                                    System.out.println(String.format("startIdx : %s, endIdx : %s, %s", startIdx, endIdx, log.substring(startIdx + 2, endIdx -1)));
//                                return log.substring(startIdx + 2, endIdx);
//                            } else {
//                                int startIdx = log.indexOf(": ");
//                                int endIdx = log.indexOf("us");
////
//                                return log.substring(startIdx + 2, endIdx - 1);
//                            }
//                        }).collect(Collectors.toList());
//
//                File csvFile = new File(logFileNameToCsvFile);
//            }
//        } catch (IOException e) {
//            System.out.println("분석 파일 생성 중 에러 발생");
//        }
//    }
}

