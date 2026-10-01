package com.astra.sirius;

public class SiriusTask {

    private final String type;
    private final String data;

    public SiriusTask(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String encode() {
        return "TASK:" + type + ":" + data;
    }

    @Override
    public String toString() {
        return "SiriusTask{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
