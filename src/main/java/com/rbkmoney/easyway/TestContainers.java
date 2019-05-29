package com.rbkmoney.easyway;

import com.rbkmoney.easyway.config.EnvironmentPropertiesConfig;
import com.rbkmoney.easyway.config.TestContainersConfig;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor
@Setter
public class TestContainers {

    private TestContainersParameters parameters;

    private boolean localDockerContainersEnabled;

    private boolean postgresqlTestContainerEnabled;
    private boolean cephTestContainerEnabled;
    private boolean fileStorageTestContainerEnabled;
    private boolean kafkaTestContainerEnabled;

    private PostgreSQLContainer postgresqlTestContainer;
    private GenericContainer cephTestContainer;
    private GenericContainer fileStorageTestContainer;
    private KafkaContainer kafkaTestContainer;

    public TestContainersParameters getParameters() {
        return parameters;
    }

    public boolean isPostgresqlTestContainerEnabled() {
        return postgresqlTestContainerEnabled;
    }

    public boolean isCephTestContainerEnabled() {
        return cephTestContainerEnabled;
    }

    public boolean isFileStorageTestContainerEnabled() {
        return fileStorageTestContainerEnabled;
    }

    public boolean isKafkaTestContainerEnabled() {
        return kafkaTestContainerEnabled;
    }

    public Boolean isLocalDockerContainersEnabled() {
        return localDockerContainersEnabled;
    }

    public Optional<PostgreSQLContainer> getPostgresqlTestContainer() {
        return Optional.ofNullable(postgresqlTestContainer);
    }

    public Optional<GenericContainer> getCephTestContainer() {
        return Optional.ofNullable(cephTestContainer);
    }

    public Optional<GenericContainer> getFileStorageTestContainer() {
        return Optional.ofNullable(fileStorageTestContainer);
    }

    public Optional<KafkaContainer> getKafkaTestContainer() {
        return Optional.ofNullable(kafkaTestContainer);
    }

    public String[] getEnvironmentProperties(Supplier<List<String>> commonProperties) {
        List<String> properties = new ArrayList<>();

        EnvironmentPropertiesConfig.configure(this, properties);

        properties.addAll(commonProperties.get());

        return properties.toArray(new String[0]);
    }

    public void startTestContainers() {
        TestContainersConfig.configureAndInit(this);
    }

    public void stopTestContainers() {
        if (!isLocalDockerContainersEnabled()) {
            getFileStorageTestContainer().ifPresent(GenericContainer::stop);
            getCephTestContainer().ifPresent(GenericContainer::stop);
            getPostgresqlTestContainer().ifPresent(GenericContainer::stop);
            getKafkaTestContainer().ifPresent(GenericContainer::stop);
        }
    }
}
