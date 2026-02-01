package utils.query;

public interface FieldExtractor<T> {
    String getField(T entity, String fieldName);
}
