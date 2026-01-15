package com.rakesh.Contact.Manager.Helpers;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String msg)
    {
        super(msg);
    }
}
