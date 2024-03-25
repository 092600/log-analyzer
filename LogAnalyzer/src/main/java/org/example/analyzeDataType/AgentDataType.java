package org.example.analyzeDataType;

public enum AgentDataType {
    SEND("Agent >>>>> MCMP"), REPORT("Agent <<<<< MCMP"), FETCH("Fetcher fetch Data"), EXIT(null);


    private String phrases;

    AgentDataType(String phrases) {
        this.phrases = phrases;
    }
}
