package mephi.url_shorter.domain.interactors;

public class PermissionDeniedException extends Exception {
    public PermissionDeniedException(String errorMessage) {
        super(errorMessage);
    }
}
