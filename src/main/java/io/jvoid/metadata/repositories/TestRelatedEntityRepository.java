package io.jvoid.metadata.repositories;

import java.util.Map;

/**
 *
 */
public interface TestRelatedEntityRepository<T, I> extends BaseRepository<T, I> {

    Map<String, T> findByTestId(Long testId);

    Map<String, T> findByExecutionIdAndRelatedToTestId(Long executionId, Long testId);

}
