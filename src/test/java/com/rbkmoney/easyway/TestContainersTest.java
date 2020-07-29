package com.rbkmoney.easyway;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class TestContainersTest {

    private final static TestContainers testContainers = TestContainersBuilder.builderWithTestContainers(getParameters())
//            .addPostgresqlTestContainer()
            .addCephTestContainer()
            .addFileStorageTestContainer()
//            .addKafkaTestContainer()
            .build();

    @Test
    public void testContainersInitTest() {
        try {
            testContainers.startTestContainers();

            List<String> containersProps = Arrays.asList(
//                    "spring.datasource.url"
//                    ,
                    "storage.endpoint"
                    ,
                    "filestorage.url"
//                    ,
//                    "kafka.bootstrap-servers"
            );

            int actualSize = Arrays.stream(testContainers.getEnvironmentProperties(getEnvironmentPropertiesConsumer()))
                    .filter(prop -> containersProps.stream().anyMatch(prop::contains))
                    .mapToInt(value -> 1)
                    .sum();

            assertEquals(containersProps.size(), actualSize);
        } finally {
            testContainers.stopTestContainers();
        }
    }

    private static Consumer<EnvironmentProperties> getEnvironmentPropertiesConsumer() {
        return environmentProperties -> {
            environmentProperties.put("kafka.topics.invoice.id", "mg-invoice-100-2");
            environmentProperties.put("kafka.topics.invoice.enabled", "true");
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
