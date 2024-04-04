package org.example.log;

import java.util.List;

import static org.example.common.constants.Constants.*;

public enum LogType {

    NEW_AGENT(List.of(NEW_AGENT_TPS, NEW_AGENT_DBIO)), TOP(List.of(TOP_TOTAL));

    List<String> themes;

    LogType(List<String> themes) {
        this.themes = themes;
    }


}
