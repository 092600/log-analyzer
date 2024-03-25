package org.example.common.type.cpu;

import java.util.regex.Pattern;

public class CpuUtil {

    // 패턴 정의
    public static final Pattern TIME_PATTERN = Pattern.compile("top - (\\d{2}:\\d{2}:\\d{2})");
    public static final Pattern CPU_PATTERN = Pattern.compile("Cpu\\(s\\):\\s+(\\d+\\.\\d+%us)");

//    // 매칭 수행
//    public static final

}
