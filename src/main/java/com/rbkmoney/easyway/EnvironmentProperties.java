package com.rbkmoney.easyway;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentProperties {

    private final Map<String, String> properties = new HashMap<>();

    public void put(String key, String value) {
        properties.put(key, value);
    }

    public String[] getAll() {
        return properties.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
    }
}
