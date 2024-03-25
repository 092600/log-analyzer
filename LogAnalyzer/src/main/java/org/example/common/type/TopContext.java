package org.example.common.type;

public class TopContext {
    private String time;

    public String getTotalCpuUsage() {
        return totalCpuUsage;
    }

    private String totalCpuUsage;
    private String cpuUsage;
    private String memUsage;
    private String threadName;

    public TopContext(String time,  String totalCpuUsage, String cpuUsage, String threadName) {
        this.time = time;
        this.totalCpuUsage = totalCpuUsage;
        this.cpuUsage = cpuUsage;
        this.threadName = threadName;
    }

    public TopContext(String time,  String totalCpuUsage, String cpuUsage, String memUsage, String threadName) {
        this.time = time;
        this.totalCpuUsage = totalCpuUsage;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.threadName = threadName;
    }


    public String getTime() {
        return time;
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public String getMemUsage() {
        return memUsage;
    }

    public String getThreadName() {
        return threadName;
    }

    public TopContext setTime(String time) {
        this.time = time;
        return this;
    }

    public TopContext setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
        return this;
    }

    public TopContext setMemUsage(String memUsage) {
        this.memUsage = memUsage;
        return this;
    }

    public TopContext setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    @Override
    public String toString() {
        return String.join(",", time, threadName, cpuUsage, memUsage);
    }
}
