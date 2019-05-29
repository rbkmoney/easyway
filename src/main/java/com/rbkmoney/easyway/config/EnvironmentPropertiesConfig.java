package com.rbkmoney.easyway.config;

import com.rbkmoney.easyway.TestContainers;

import java.util.List;

public class EnvironmentPropertiesConfig {

    public static void configure(TestContainers testContainers, List<String> properties) {
        if (!testContainers.isLocalDockerContainersEnabled()) {
            testContainers.getPostgresqlTestContainer().ifPresent(
                    c -> {
                        properties.add("spring.datasource.url=" + c.getJdbcUrl());
                        properties.add("spring.datasource.username=" + c.getUsername());
                        properties.add("spring.datasource.password=" + c.getPassword());
                        properties.add("flyway.url=" + c.getJdbcUrl());
                        properties.add("flyway.user=" + c.getUsername());
                        properties.add("flyway.password=" + c.getPassword());
                    }
            );
            testContainers.getCephTestContainer().ifPresent(
                    c -> fillCephProperties(testContainers, properties, c.getContainerIpAddress() + ":" + c.getMappedPort(80))
            );
            testContainers.getFileStorageTestContainer().ifPresent(
                    c -> properties.add("filestorage.url=http://" + c.getContainerIpAddress() + ":" + testContainers.getParameters().getFileStoragePort() + "/file_storage")
            );
            testContainers.getKafkaTestContainer().ifPresent(
                    c -> fillKafkaProperties(properties, c.getBootstrapServers())
            );
        } else {
            if (testContainers.isPostgresqlTestContainerEnabled()) {
                properties.add("spring.datasource.url=" + testContainers.getParameters().getPostgresqlJdbcUrl());
                properties.add("spring.datasource.username=" + testContainers.getParameters().getPostgresqlUsername());
                properties.add("spring.datasource.password=" + testContainers.getParameters().getPostgresqlPassword());
                properties.add("flyway.url=" + testContainers.getParameters().getPostgresqlJdbcUrl());
                properties.add("flyway.user=" + testContainers.getParameters().getPostgresqlUsername());
                properties.add("flyway.password=" + testContainers.getParameters().getPostgresqlPassword());
            }
            if (testContainers.isCephTestContainerEnabled()) {
                fillCephProperties(testContainers, properties, "localhost:" + testContainers.getParameters().getCephPort());
            }
            if (testContainers.isFileStorageTestContainerEnabled()) {
                properties.add("filestorage.url=http://localhost:" + testContainers.getParameters().getFileStoragePort() + "/file_storage");
            }
            if (testContainers.isKafkaTestContainerEnabled()) {
                fillKafkaProperties(properties, "localhost:" + testContainers.getParameters().getKafkaPort());
            }
        }
    }

    private static void fillCephProperties(TestContainers testContainers, List<String> properties, String storageEndpoint) {
        properties.add("storage.endpoint=" + storageEndpoint);
        properties.add("storage.signingRegion=" + testContainers.getParameters().getCephSigningRegion());
        properties.add("storage.accessKey=" + testContainers.getParameters().getCephAccessKey());
        properties.add("storage.secretKey=" + testContainers.getParameters().getCephSecretKey());
        properties.add("storage.clientProtocol=" + testContainers.getParameters().getCephProtocol());
        properties.add("storage.clientMaxErrorRetry=" + testContainers.getParameters().getCephMaxErrorRetry());
        properties.add("storage.bucketName=" + testContainers.getParameters().getCephBucketName());
    }

    private static void fillKafkaProperties(List<String> properties, String bootstrapServers) {
        properties.add("kafka.bootstrap-servers=" + bootstrapServers);
        properties.add("kafka.topics.invoice.id=mg-invoice-100-2");
        properties.add("kafka.topics.invoice.enabled=true");
        properties.add("kafka.topics.payment.id=mg-payment-100-2");
        properties.add("kafka.topics.payment.enabled=false");
        properties.add("kafka.topics.payout.id=mg-payout-100-2");
        properties.add("kafka.topics.payout.enabled=false");
        properties.add("kafka.ssl.enabled=false");
    }
}
