package com.rbkmoney.easyway;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestContainersTest {

    private static TestContainers testContainers = TestContainersBuilder.builderWithTestContainers(getParameters())
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

            Assert.assertTrue(flag);
        } finally {
            testContainers.stopTestContainers();
        }
    }

    private static Supplier<List<String>> getCommonProperties() {
        return () -> {
            List<String> prop = new ArrayList<>();
            prop.add("kafka.processing.payment.enabled=false");
            prop.add("kafka.processing.payout.enabled=false");

            return prop;
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
