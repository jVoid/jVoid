package io.jvoid.metadata.model;

import lombok.Data;

/**
 * A Java test metadata stored by JVoid. The test is what in general is
 * referred as a feature (i.e., a single @Test method in JUnit).
 *
 */
@Data
public class JTest implements JEntity<Long> {

    public static final String RUN_STATUS_RUN = "RUN";
    public static final String RUN_STATUS_RUNNING = "RUNNING";
    public static final String RUN_STATUS_SKIPPED = "SKIPPED";
    public static final String RUN_STATUS_FAILED = "FAILED";

    private Long id;
    private Long executionId;
    private String identifier;
    private String runStatus = RUN_STATUS_RUNNING;
    
}
