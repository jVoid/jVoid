package io.jvoid.metadata.model;

import lombok.Data;

/**
 * A Java class constructor metadata stored by JVoid.
 *
 */
@Data
public class JClassConstructor implements JEntity<Long>, ChecksumAware {

    private Long id;
    private String identifier;
    private String checksum;
    private Long classId;
    
}
