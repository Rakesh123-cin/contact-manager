package com.rakesh.Contact.Manager.Validator;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxFileSizeValidator.class)
@Documented
public @interface MaxFileSize {

    String message() default "File size must not exceed 2 MB";

    long value(); // size in bytes

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}