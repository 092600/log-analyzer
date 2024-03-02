package org.example.common.type;

public class CommonLog {


    private String level;
    private String time;
    private String thread;
    private String msg;

    public String getLevel() {
        return level;
    }

    public String getTime() {
        return time;
    }

    public String getThread() {
        return thread;
    }

    public String getMsg() {
        return msg;
    }

    public String getRequestId() {
        int idx1 = msg.indexOf(":");
        int idx2 = msg.indexOf("]");

        return msg.substring(idx1+2, idx2);
    }


    public CommonLog(String level, String time, String thread, String msg) {
        this.level = level;
        this.time = time;
        this.thread = thread;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CommonLog{" +
                "level='" + level + '\'' +
                ", time='" + time + '\'' +
                ", thread='" + thread + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
