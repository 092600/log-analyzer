package org.example.common.type.apiServer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestProcessingTimeEntity {
    private String requestId;



    private LocalDateTime logDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;


    public RequestProcessingTimeEntity(String requestId) {
        this.requestId = requestId;
    }

    public RequestProcessingTimeEntity(String requestId, String logDate) {
        this.requestId = requestId;
        this.logDate = parseStringToLocalDateTime(logDate);
    }



    public RequestProcessingTimeEntity(String requestId, LocalDateTime startDate, LocalDateTime endDate) {
        this.requestId = requestId;
        this.startDate = startDate;
        this.endDate = endDate;
    }



    public LocalDateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = parseStringToLocalDateTime(logDate);
    }

    public LocalDateTime parseStringToLocalDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = parseStringToLocalDateTime(startDate);
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = parseStringToLocalDateTime(endDate);
    }

    public long getDelayTime() {
        Duration duration = Duration.between(startDate, endDate);
        return duration.toMillis();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestProcessingTimeEntity that = (RequestProcessingTimeEntity) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }


    public static void main(String[] args) {
        RequestProcessingTimeEntity e1 = new RequestProcessingTimeEntity("1");
        RequestProcessingTimeEntity e2 = new RequestProcessingTimeEntity("1");

        System.out.println("e1 == e2 : "+ (e1 == e2));
        System.out.println("e1.equals(e2) : "+ (e1.equals(e2)));
        System.out.println("e1.hashCode() : " + e1.hashCode());
        System.out.println("e2.hashCode() : " + e2.hashCode());

        Map<String, RequestProcessingTimeEntity> datas = new HashMap<String, RequestProcessingTimeEntity>();
        datas.put("1", e1);
        datas.put("1", e2);

        System.out.println(datas.keySet());
    }

    @Override
    public String toString() {
        return "RequestProcessingTimeEntity{" +
                "requestId='" + requestId + '\'' +
                ", logDate=" + logDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
