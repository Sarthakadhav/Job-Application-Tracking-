// thrown when a student applies twice for the same job
class DuplicateApplicationException extends Exception {

    public DuplicateApplicationException(String message) {
        super(message);
    }
}

// thrown when a student, job or application id does not exist
class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
