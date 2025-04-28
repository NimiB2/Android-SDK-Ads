package dev.nimrod.adsdk_lib.util;

public enum EventEnum {
    VIEW("view"),
    CLICK("click"),
    SKIP("skip"),
    EXIT("exit");

    private final String value;

    EventEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
