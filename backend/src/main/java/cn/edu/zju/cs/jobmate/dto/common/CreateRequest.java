package cn.edu.zju.cs.jobmate.dto.common;

/**
 * Entity create request DTO interface.
 * 
 * @param <T> the type of the entity to be created
 */
public interface CreateRequest<T> {

    /**
     * Converts this create request DTO to the corresponding entity.
     * 
     * @return the entity created from this request DTO
     */
    public T toModel();
}
