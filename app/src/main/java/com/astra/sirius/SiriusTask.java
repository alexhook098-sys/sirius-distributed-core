package com.astra.sirius;

public class SiriusTask {

    private final String type;
    private final String data;

    // Минимально необходимая свободная RAM
    private final long requiredRamMb;

    public SiriusTask(
            String type,
            String data
    ) {

        this.type = type;
        this.data = data;

        // Определяем требования задачи
        if (type.equals("HEAVY")) {

            requiredRamMb = 700;

        } else if (type.equals("MEDIUM")) {

            requiredRamMb = 400;

        } else {

            requiredRamMb = 100;
        }
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public long getRequiredRamMb() {
        return requiredRamMb;
    }

    public boolean isHeavy() {
        return requiredRamMb >= 700;
    }

    public String encode() {
        return "TASK:" + type + ":" + data;
    }

    @Override
    public String toString() {

        return "SiriusTask{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", requiredRamMb=" +
                requiredRamMb +
                '}';
    }
}
