package exceptions;

public class EntityNotFoundException extends Exception {

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " con id '" + id + "' no encontrado.");
    }
}
