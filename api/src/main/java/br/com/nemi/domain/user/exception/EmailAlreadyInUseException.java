package br.com.nemi.domain.user.exception;

import br.com.nemi.util.exception.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
