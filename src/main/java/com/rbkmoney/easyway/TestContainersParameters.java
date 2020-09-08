package com.rbkmoney.easyway;

import lombok.Data;

@Data
public class TestContainersParameters {

    private String postgresqlImageTag = "10.9";
    private String postgresqlJdbcUrl;
    private String postgresqlUsername = "postgres";
    private String postgresqlPassword = "postgres";
    private int cephPort = 42827;
    private String cephImageTag = "v3.0.5-stable-3.0-luminous-centos-7";
    private String cephRgwName = "localhost";
    private String cephNetworkAutoDetect = "4";
    private String cephDaemon = "demo";
    private String cephDemoUid = "ceph-test";
    private String cephAccessKey = "test";
    private String cephSecretKey = "test";
    private String cephBucketName = "TEST";
    private String cephSigningRegion = "RU";
    private String cephProtocol = "HTTP";
    private String cephMaxErrorRetry = "10";
    private String fileStorageImageTag = "25506ff3def2e9a629f2056d514e84ecd3ecb2b3";
    private int fileStoragePort = 42826;
    private String kafkaImageTag = "5.2.1";
    private int kafkaPort = 9092;

}
