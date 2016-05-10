package com.jvoid.metadata.model;

import lombok.Data;

/**
 * The current test execution metadata stored by JVoid.
 *
 */
@Data
public class JExecution implements JEntity<Long> {

    private Long id;
    private Long timestamp;
    
}
