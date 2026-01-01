package cn.edu.zju.cs.jobmate.dto.common;

/**
 * Entity update request DTO interface.
 * 
 * @param <T> the type of the entity to be updated
 */
public interface UpdateRequest<T> {

    /**
     * Check if this update request contains updatable fields.
     * 
     * @return false if no updates are needed, true otherwise.
     */
    boolean isUpdatable();

    /**
     * Apply the updates from this request to the given entity.
     * 
     * @param entity the entity to be updated
     */
    void apply(T entity);
}
