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
                    c -> {
                        properties.add("storage.endpoint=" + c.getContainerIpAddress() + ":" + c.getMappedPort(80));
                        fillCephProperties(testContainers, properties);
                    }
            );
            testContainers.getFileStorageTestContainer().ifPresent(
                    c -> properties.add("filestorage.url=http://" + c.getContainerIpAddress() + ":" + testContainers.getParameters().getFileStoragePort() + "/file_storage")
            );
            testContainers.getKafkaTestContainer().ifPresent(
                    c -> {
                        properties.add("kafka.bootstrap.servers=" + c.getBootstrapServers());
                        fillKafkaProperties(properties);
                    }
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
                properties.add("storage.endpoint=localhost:" + testContainers.getParameters().getCephPort());
                fillCephProperties(testContainers, properties);
            }
            if (testContainers.isFileStorageTestContainerEnabled()) {
                properties.add("filestorage.url=http://localhost:" + testContainers.getParameters().getFileStoragePort() + "/file_storage");
            }
            if (testContainers.isKafkaTestContainerEnabled()) {
                properties.add("kafka.bootstrap.servers=localhost:" + testContainers.getParameters().getKafkaPort());
                fillKafkaProperties(properties);
            }
        }
    }

    private static void fillCephProperties(TestContainers testContainers, List<String> properties) {
        properties.add("storage.signingRegion=" + testContainers.getParameters().getCephSigningRegion());
        properties.add("storage.accessKey=" + testContainers.getParameters().getCephAccessKey());
        properties.add("storage.secretKey=" + testContainers.getParameters().getCephSecretKey());
        properties.add("storage.clientProtocol=" + testContainers.getParameters().getCephProtocol());
        properties.add("storage.clientMaxErrorRetry=" + testContainers.getParameters().getCephMaxErrorRetry());
        properties.add("storage.bucketName=" + testContainers.getParameters().getCephBucketName());
    }

    private static void fillKafkaProperties(List<String> properties) {
        properties.add("spring.kafka.processing.payout.topic=mg-payout-100-2");
        properties.add("spring.kafka.processing.payout.enabled=false");
        properties.add("spring.kafka.processing.payment.topic=mg-invoice-100-2");
        properties.add("spring.kafka.processing.payment.enabled=false");
        properties.add("spring.kafka.properties.security.protocol=PLAINTEXT");
    }
}
