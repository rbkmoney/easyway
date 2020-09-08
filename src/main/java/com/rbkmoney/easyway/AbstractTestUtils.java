package com.rbkmoney.easyway;

import com.rbkmoney.geck.common.util.TypeUtil;
import org.jeasy.random.EasyRandom;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;

public abstract class AbstractTestUtils {

    private final LocalDateTime fromTime = LocalDateTime.now().minusHours(3);
    private final LocalDateTime toTime = LocalDateTime.now().minusHours(1);
    private final LocalDateTime inFromToPeriodTime = LocalDateTime.now().minusHours(2);

    protected static String generateDate() {
        return TypeUtil.temporalToString(LocalDateTime.now());
    }

    protected static Long generateLong() {
        return new EasyRandom().nextLong();
    }

    protected static Integer generateInt() {
        return new EasyRandom().nextInt();
    }

    protected static String generateString() {
        return new EasyRandom().nextObject(String.class);
    }

    protected static LocalDateTime generateLocalDateTime() {
        return new EasyRandom().nextObject(LocalDateTime.class);
    }

    protected static Instant generateCurrentTimePlusDay() {
        return now().plusDays(1).toInstant(getZoneOffset());
    }

    protected static ZoneOffset getZoneOffset() {
        return systemDefault().getRules().getOffset(now());
    }

    protected static String getContent(InputStream content) throws IOException {
        return IOUtils.toString(content, StandardCharsets.UTF_8);
    }

    protected LocalDateTime getFromTime() {
        return fromTime;
    }

    protected LocalDateTime getToTime() {
        return toTime;
    }

    protected LocalDateTime getInFromToPeriodTime() {
        return inFromToPeriodTime;
    }
}
