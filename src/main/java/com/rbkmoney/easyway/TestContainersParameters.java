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
    private String cephSigningRegion = "RU";
    private String cephAccessKey = "test";
    private String cephSecretKey = "test";
    private String cephProtocol = "HTTP";
    private String cephMaxErrorRetry = "10";
    private String cephBucketName = "test";
    private String cephDemoUid = "ceph-test";
    private String cephNetworkAutoDetect = "4";
    private String cephRgwName = "localhost";
    private String fileStorageImageTag = "8ac5a7533058f207fe539592a10c6c8bb7960bd5";
    private int fileStoragePort = 42826;
    private String kafkaImageTag = "5.0.1";
    private int kafkaPort = 9092;

}
