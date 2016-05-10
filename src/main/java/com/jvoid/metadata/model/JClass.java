package com.jvoid.metadata.model;

import lombok.Data;

/**
 * A Java class metadata stored by JVoid.
 *
 */
@Data
public class JClass implements JEntity<Long>, ChecksumAware {

    private Long id;
    private Long executionId;
    private String identifier;
    private String checksum;
    private String superClassIdentifier;
    
}
