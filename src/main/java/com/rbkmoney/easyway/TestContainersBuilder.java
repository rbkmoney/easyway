package com.rbkmoney.easyway;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Supplier;

public class TestContainersBuilder {

    private TestContainersParameters parameters;

    private boolean localDockerContainersEnabled;
    private boolean postgresqlTestContainerEnabled;
    private boolean cephTestContainerEnabled;
    private boolean fileStorageTestContainerEnabled;
    private boolean kafkaTestContainerEnabled;

    private TestContainersBuilder(Supplier<TestContainersParameters> parameters, boolean localDockerContainersEnabled) {
        this.parameters = parameters.get();
        this.localDockerContainersEnabled = localDockerContainersEnabled;
    }

    public static TestContainersBuilder builderWithLocalDockerContainers(Supplier<TestContainersParameters> parameters) {
        return new TestContainersBuilder(parameters, true);
    }

    public static TestContainersBuilder builderWithTestContainers(Supplier<TestContainersParameters> parameters) {
        return new TestContainersBuilder(parameters, false);
    }

    public TestContainersBuilder addPostgresqlTestContainer() {
        postgresqlTestContainerEnabled = true;
        return this;
    }

    public TestContainersBuilder addCephTestContainer() {
        cephTestContainerEnabled = true;
        return this;
    }

    public TestContainersBuilder addFileStorageTestContainer() {
        cephTestContainerEnabled = true;
        fileStorageTestContainerEnabled = true;
        return this;
    }

    public TestContainersBuilder addKafkaTestContainer() {
        kafkaTestContainerEnabled = true;
        return this;
    }

    public TestContainers build() {
        TestContainers testContainers = new TestContainers();

        if (!localDockerContainersEnabled) {
            addTestContainers(testContainers);
        } else {
            testContainers.setPostgresqlTestContainerEnabled(postgresqlTestContainerEnabled);
            testContainers.setCephTestContainerEnabled(cephTestContainerEnabled);
            testContainers.setFileStorageTestContainerEnabled(fileStorageTestContainerEnabled);
            testContainers.setKafkaTestContainerEnabled(kafkaTestContainerEnabled);
            testContainers.setLocalDockerContainersEnabled(true);
        }

        testContainers.setParameters(parameters);

        return testContainers;
    }

    private void addTestContainers(TestContainers testContainers) {
        if (postgresqlTestContainerEnabled) {
            testContainers.setPostgresqlTestContainer(new PostgreSQLContainer<>("postgres:" + parameters.getPostgresqlImageTag()));
        }
        if (cephTestContainerEnabled) {
            testContainers.setCephTestContainer(new GenericContainer<>("dr.rbkmoney.com/ceph-demo:" + parameters.getCephImageTag()));
        }
        if (fileStorageTestContainerEnabled) {
            testContainers.setFileStorageTestContainer(new GenericContainer<>("dr.rbkmoney.com/rbkmoney/file-storage:" + parameters.getFileStorageImageTag()));
        }
        if (kafkaTestContainerEnabled) {
            testContainers.setKafkaTestContainer(new KafkaContainer(parameters.getKafkaImageTag()));
        }
        testContainers.setLocalDockerContainersEnabled(false);
    }
}
