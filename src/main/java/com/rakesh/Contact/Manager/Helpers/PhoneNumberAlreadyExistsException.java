package com.rakesh.Contact.Manager.Helpers;

public class PhoneNumberAlreadyExistsException extends RuntimeException{

    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }

}
