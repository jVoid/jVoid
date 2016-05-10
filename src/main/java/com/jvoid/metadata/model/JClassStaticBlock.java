package com.jvoid.metadata.model;

import lombok.Data;

/**
 * A Java class static block metadata stored by JVoid.
 *
 */
@Data
public class JClassStaticBlock implements JEntity<Long>, ChecksumAware {

    private Long id;
    private String identifier;
    private String checksum;
    private Long classId;
    
}
