package com.rbkmoney.easyway.healthcheck;

import org.rnorth.ducttape.TimeoutException;
import org.rnorth.ducttape.ratelimits.RateLimiter;
import org.rnorth.ducttape.ratelimits.RateLimiterBuilder;
import org.rnorth.visibleassertions.VisibleAssertions;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.rnorth.ducttape.unreliables.Unreliables.retryUntilSuccess;

public class NetworkModeHostWaitStrategy implements WaitStrategy {

    private static final RateLimiter LIMITER = getRateLimiter();

    private String path = "/";
    private Integer statusCode = 200;
    private Integer port = 8022;
    private Duration startupTimeout = Duration.ofSeconds(60);

    public NetworkModeHostWaitStrategy withPath(String path) {
        this.path = path;
        return this;
    }

    public NetworkModeHostWaitStrategy withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public NetworkModeHostWaitStrategy withPort(Integer port) {
        this.port = port;
        return this;
    }

    public NetworkModeHostWaitStrategy withTimeout(Duration startupTimeout) {
        this.startupTimeout = startupTimeout;
        return this;
    }

    @Override
    public void waitUntilReady(WaitStrategyTarget waitStrategyTarget) {
        String uri = URI.create("http://" + waitStrategyTarget.getContainerIpAddress() + ":" + port + path).toString();

        VisibleAssertions.pass(
                String.format(
                        "#### %s: Waiting starting up for %s seconds for URL: %s",
                        waitStrategyTarget.getContainerInfo().getName(),
                        startupTimeout.getSeconds(),
                        uri
                )
        );

        try {
            retryUntilSuccess(
                    (int) startupTimeout.getSeconds(),
                    TimeUnit.SECONDS,
                    () -> {
                        LIMITER.doWhenReady(
                                () -> {
                                    try {
                                        HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
                                        connection.setRequestMethod("GET");
                                        connection.connect();

                                        VisibleAssertions.pass(String.format("URL get response code %s ", connection.getResponseCode()));

                                        if (connection.getResponseCode() != statusCode) {
                                            throw new RuntimeException(String.format("HTTP response code was: %s", connection.getResponseCode()));
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                        return true;
                    }
            );
        } catch (TimeoutException e) {
            throw new ContainerLaunchException(String.format("Timed out waiting for URL to be accessible (%s should return HTTP %s)", uri, statusCode));
        }
    }

    @Override
    public WaitStrategy withStartupTimeout(Duration startupTimeout) {
        this.startupTimeout = startupTimeout;
        return this;
    }

    private static RateLimiter getRateLimiter() {
        return RateLimiterBuilder
                .newBuilder()
                .withRate(1, TimeUnit.SECONDS)
                .withConstantThroughput()
                .build();
    }
}