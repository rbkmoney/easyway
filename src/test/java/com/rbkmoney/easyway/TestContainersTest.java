package com.rbkmoney.easyway;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class TestContainersTest {

    private final static TestContainers testContainers = TestContainersBuilder.builderWithTestContainers(getParameters())
            .addPostgresqlTestContainer()
            .addFileStorageTestContainer()
            .addKafkaTestContainer()
            .build();

    @Test
    public void testContainersInitTest() {
        try {
            testContainers.startTestContainers();

            boolean flag = false;

            String[] properties = testContainers.getEnvironmentProperties(getCommonProperties());
            for (String property : properties) {
                if (property.contains("flyway.url")) {
                    flag = true;
                }
            }

            assertTrue(flag);
        } finally {
            testContainers.stopTestContainers();
        }
    }

    private static Supplier<List<String>> getCommonProperties() {
        return () -> {
            List<String> properties = new ArrayList<>();
            properties.add("kafka.topics.invoice.id=mg-invoice-100-2");
            properties.add("kafka.topics.invoice.enabled=false");

            return properties;
        };
    }

    private static Supplier<TestContainersParameters> getParameters() {
        return () -> {
            TestContainersParameters testContainersParameters = new TestContainersParameters();
            testContainersParameters.setPostgresqlJdbcUrl("jdbc:postgresql://localhost:5432/rbkmoney");

            return testContainersParameters;
        };
    }
}
