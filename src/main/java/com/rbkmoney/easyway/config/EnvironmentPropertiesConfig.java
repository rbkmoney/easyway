package com.rbkmoney.easyway.config;

import com.rbkmoney.easyway.EnvironmentProperties;
import com.rbkmoney.easyway.TestContainers;

public class EnvironmentPropertiesConfig {

    public static void configure(TestContainers testContainers, EnvironmentProperties properties) {
        if (!testContainers.isLocalDockerContainersEnabled()) {
            testContainers.getPostgresqlTestContainer().ifPresent(
                    c -> {
                        properties.put("spring.datasource.url", c.getJdbcUrl());
                        properties.put("spring.datasource.username", c.getUsername());
                        properties.put("spring.datasource.password", c.getPassword());
                        properties.put("flyway.url", c.getJdbcUrl());
                        properties.put("flyway.user", c.getUsername());
                        properties.put("flyway.password", c.getPassword());
                        properties.put("spring.flyway.url", c.getJdbcUrl());
                        properties.put("spring.flyway.user", c.getUsername());
                        properties.put("spring.flyway.password", c.getPassword());
                    }
            );
            testContainers.getCephTestContainer().ifPresent(
                    c -> {
                        String storageEndpoint = c.getContainerIpAddress() + ":" + c.getMappedPort(8080);
                        fillCephProperties(testContainers, properties, storageEndpoint);
                    }
            );
            testContainers.getFileStorageTestContainer().ifPresent(
                    c -> {
                        String url = "http://" + c.getContainerIpAddress() + ":" + testContainers.getParameters().getFileStoragePort() + "/file_storage";
                        properties.put("filestorage.url", url);
                    }
            );
            testContainers.getKafkaTestContainer().ifPresent(
                    c -> fillKafkaProperties(properties, c.getBootstrapServers())
            );
        } else {
            if (testContainers.isPostgresqlTestContainerEnabled()) {
                properties.put("spring.datasource.url", testContainers.getParameters().getPostgresqlJdbcUrl());
                properties.put("spring.datasource.username", testContainers.getParameters().getPostgresqlUsername());
                properties.put("spring.datasource.password", testContainers.getParameters().getPostgresqlPassword());
                properties.put("flyway.url", testContainers.getParameters().getPostgresqlJdbcUrl());
                properties.put("flyway.user", testContainers.getParameters().getPostgresqlUsername());
                properties.put("flyway.password", testContainers.getParameters().getPostgresqlPassword());
                properties.put("spring.flyway.url", testContainers.getParameters().getPostgresqlJdbcUrl());
                properties.put("spring.flyway.user", testContainers.getParameters().getPostgresqlUsername());
                properties.put("spring.flyway.password", testContainers.getParameters().getPostgresqlPassword());
            }
            if (testContainers.isCephTestContainerEnabled()) {
                fillCephProperties(testContainers, properties, "localhost:" + testContainers.getParameters().getCephPort());
            }
            if (testContainers.isFileStorageTestContainerEnabled()) {
                properties.put("filestorage.url", "http://localhost:" + testContainers.getParameters().getFileStoragePort() + "/file_storage");
            }
            if (testContainers.isKafkaTestContainerEnabled()) {
                fillKafkaProperties(properties, "localhost:" + testContainers.getParameters().getKafkaPort());
            }
        }
    }

    private static void fillCephProperties(TestContainers testContainers, EnvironmentProperties properties, String storageEndpoint) {
        properties.put("storage.endpoint", storageEndpoint);
        properties.put("storage.signingRegion", testContainers.getParameters().getCephSigningRegion());
        properties.put("storage.accessKey", testContainers.getParameters().getCephAccessKey());
        properties.put("storage.secretKey", testContainers.getParameters().getCephSecretKey());
        properties.put("storage.clientProtocol", testContainers.getParameters().getCephProtocol());
        properties.put("storage.clientMaxErrorRetry", testContainers.getParameters().getCephMaxErrorRetry());
        properties.put("storage.bucketName", testContainers.getParameters().getCephBucketName());
    }

    private static void fillKafkaProperties(EnvironmentProperties properties, String bootstrapServers) {
        properties.put("kafka.bootstrap-servers", bootstrapServers);
        properties.put("kafka.topics.invoice.id", "mg-invoice-100-2");
        properties.put("kafka.topics.invoice.enabled", "true");
        properties.put("kafka.topics.payment.id", "mg-payment-100-2");
        properties.put("kafka.topics.payment.enabled", "false");
        properties.put("kafka.topics.payout.id", "mg-payout-100-2");
        properties.put("kafka.topics.payout.enabled", "false");
        properties.put("kafka.ssl.enabled", "false");
    }
}
