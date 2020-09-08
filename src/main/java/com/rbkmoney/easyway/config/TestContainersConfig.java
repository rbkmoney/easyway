package com.rbkmoney.easyway.config;

import com.rbkmoney.easyway.TestContainers;
import com.rbkmoney.easyway.healthcheck.NetworkModeHostWaitStrategy;
import org.rnorth.visibleassertions.VisibleAssertions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.time.Duration;

public class TestContainersConfig {

    public static void configureAndInit(TestContainers testContainers) {
        if (!testContainers.isLocalDockerContainersEnabled()) {
            testContainers.getPostgresqlTestContainer().ifPresent(
                    container -> {
                        container
                                .withNetworkAliases("postgresql")
                                .withExposedPorts(5432)
                                .withStartupTimeout(Duration.ofMinutes(1));

                        startContainer("postgresql", container);
                    }
            );
            testContainers.getCephTestContainer().ifPresent(
                    container -> {
                        container
                                .withNetworkAliases("ceph")
                                .withExposedPorts(5000, 8080)
                                .withEnv("RGW_NAME", testContainers.getParameters().getCephRgwName())
                                .withEnv("NETWORK_AUTO_DETECT", testContainers.getParameters().getCephNetworkAutoDetect())
                                .withEnv("CEPH_DAEMON", testContainers.getParameters().getCephDaemon())
                                .withEnv("CEPH_DEMO_UID", testContainers.getParameters().getCephDemoUid())
                                .withEnv("CEPH_DEMO_ACCESS_KEY", testContainers.getParameters().getCephAccessKey())
                                .withEnv("CEPH_DEMO_SECRET_KEY", testContainers.getParameters().getCephSecretKey())
                                .withEnv("CEPH_DEMO_BUCKET", testContainers.getParameters().getCephBucketName())
                                .waitingFor(getWaitStrategy("/api/v0.1/health", 200, 5000, Duration.ofMinutes(1)));

                        startContainer("ceph", container);
                    }
            );
            testContainers.getFileStorageTestContainer().ifPresent(
                    container -> {
                        container
                                .withNetworkAliases("file-storage")
                                // это не сработает при тестах на mac os. но этот контейнер нужен только при интеграционных тестах с файловым хранилищем
                                .withNetworkMode("host")
                                .withEnv("storage.endpoint", "localhost:" + testContainers.getCephTestContainer().get().getMappedPort(8080))
                                .withEnv("storage.signingRegion", testContainers.getParameters().getCephSigningRegion())
                                .withEnv("storage.accessKey", testContainers.getParameters().getCephAccessKey())
                                .withEnv("storage.secretKey", testContainers.getParameters().getCephSecretKey())
                                .withEnv("storage.clientProtocol", testContainers.getParameters().getCephProtocol())
                                .withEnv("storage.client.maxErrorRetry", testContainers.getParameters().getCephMaxErrorRetry())
                                .withEnv("storage.bucketName", testContainers.getParameters().getCephBucketName())
                                .withEnv("server.port", String.valueOf(testContainers.getParameters().getFileStoragePort()))
                                .waitingFor(getNetworkModeHostWaitStrategy("/actuator/health", 200, testContainers.getParameters().getFileStoragePort(), Duration.ofMinutes(1)));

                        startContainer("file-storage", container);
                    }
            );
            testContainers.getKafkaTestContainer().ifPresent(
                    container -> {
                        container
                                .withEmbeddedZookeeper()
                                .withNetworkAliases("kafka")
                                .withExposedPorts(9093)
                                .withStartupTimeout(Duration.ofMinutes(1));

                        startContainer("kafka", container);
                    }
            );
        } else {
            // docker-compose -f docker-compose-dev.yml up -d
        }
    }

    private static WaitStrategy getWaitStrategy(String path, Integer statusCode, Integer port, Duration duration) {
        return new HttpWaitStrategy()
                .forPath(path)
                .forPort(port)
                .forStatusCode(statusCode)
                .withStartupTimeout(duration);
    }

    private static WaitStrategy getNetworkModeHostWaitStrategy(String path, Integer statusCode, Integer port, Duration duration) {
        return new NetworkModeHostWaitStrategy()
                .withPath(path)
                .withPort(port)
                .withStatusCode(statusCode)
                .withTimeout(duration);
    }

    private static void startContainer(String name, GenericContainer container) {
        VisibleAssertions.pass("STARTING TESTCONTAINER: [" + name + "] ...");
        container.start();
        VisibleAssertions.pass("TESTCONTAINER: [" + name + "] SUCCESSFULLY STARTED");
        VisibleAssertions.pass(container.toString());
    }
}
