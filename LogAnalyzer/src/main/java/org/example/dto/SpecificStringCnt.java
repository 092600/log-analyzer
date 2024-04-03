package org.example.dto;

public class SpecificStringCnt {
    private String time;
    private String count;

    public SpecificStringCnt(String time, String count) {
        this.time = time;
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public String getCount() {
        return count;
    }
}
