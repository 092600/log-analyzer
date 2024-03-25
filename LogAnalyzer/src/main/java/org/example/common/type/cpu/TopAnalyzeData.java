package org.example.common.type.cpu;


public class TopAnalyzeData {
    private String time;
    private String cpu;


    public String getTime() {
        return time;
    }

    public String getCpu() {
        return cpu;
    }

    public TopAnalyzeData setTime(String time) {
        this.time = time;
        return this;
    }

    public TopAnalyzeData setCpu(String cpu) {
        this.cpu = cpu;
        return this;
    }


    public TopAnalyzeData(String time, String cpu) {
        this.time = time;
        this.cpu = cpu;
    }
}
