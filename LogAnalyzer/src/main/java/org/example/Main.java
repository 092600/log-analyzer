package org.example;

import org.example.common.util.CommonUtil;
import org.example.logType.LogType;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.logType.LogType.*;

public class Main {
    public static void main(String[] args) {
        LogType logType;
        do {
            logType = analyzerEntryPoint();
            if (logType.equals(UNKNOWN)) {
                System.out.println("\n>>> 다시 입력해주세요.");
                continue;
            } else if (logType.equals(EXIT)) {
                System.out.println(">> 종료합니다.");
            } else {
                System.out.printf("\"%s\" 디렉토리를 조회합니다.\n", logType.rootPath);
//
                List<File> directories = CommonUtil.getDirectories(logType.rootPath);
                File logDirecotry = directories.stream().filter(directory -> directory.getName().equals("logs")).findFirst().orElseThrow(IllegalStateException::new);
                List<File> logs = List.of(logDirecotry.listFiles());
                logType.analyze(logType, logs);
            }
        } while (!logType.equals(EXIT));

    }

    public static LogType analyzerEntryPoint() {
        Scanner scanner = new Scanner(System.in);
        LogType[] logTypes = values();

        System.out.printf("어떤 프로세스 로그를 분석하시겠습니까 ?\n %s : ", IntStream.rangeClosed(0, logTypes.length - 1).mapToObj(idx -> {
            LogType logType = logTypes[idx];

            if (idx + 1 == logTypes.length) {
                return String.join(".", String.valueOf(logType.typeNum), logType.name());
            } else {

                return String.join(".", String.valueOf(logType.typeNum), logType.name()+", ");
            }
        }).collect(Collectors.joining()));

        int typeNum = scanner.nextInt();

        for (LogType logType : LogType.values()) {
            if (logType.typeNum == typeNum) {
                return logType;
            }
        }

        return UNKNOWN;
    }


}

