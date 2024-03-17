package hu.progmasters.moovsmart.exception;/*
 * Copyright © Progmasters (QTC Kft.), 2018.
 * All rights reserved. No part or the whole of this Teaching Material (TM) may be reproduced, copied, distributed,
 * publicly performed, disseminated to the public, adapted or transmitted in any form or by any means, including
 * photocopying, recording, or other electronic or mechanical methods, without the prior written permission of QTC Kft.
 * This TM may only be used for the purposes of teaching exclusively by QTC Kft. and studying exclusively by QTC Kft.’s
 * students and for no other purposes by any parties other than QTC Kft.
 * This TM shall be kept confidential and shall not be made public or made available or disclosed to any unauthorized person.
 * Any dispute or claim arising out of the breach of these provisions shall be governed by and construed in accordance with the laws of Hungary.
 */

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("A validation error occurred: ", ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError();

        for (FieldError fieldError : fieldErrors) {
            validationError.addFieldError(fieldError.getField(), messageSource.getMessage(fieldError, Locale.getDefault()));
        }
        return validationError;
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        log.error("Request JSON could no be parsed: ", ex);

        ApiError body = new ApiError("JSON_PARSE_ERROR", "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiError> handleAddressNotFoundException(AddressNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Address not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeatherNotFoundException.class)
    public ResponseEntity<ApiError> handleWeatherNotFoundException(WeatherNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Weather not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ApiError> handlePropertyNotFoundException(PropertyNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Property not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyDataNotFoundException.class)
    public ResponseEntity<ApiError> handlePropertyDataNotFoundException(PropertyDataNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Property data not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "User not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UsernameAndEmailAddressNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameAndEmailAddressNotFoundException(UsernameAndEmailAddressNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Username or email not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<ApiError> handlePasswordNotValidException(PasswordNotValidException ex) {
        log.error("Not valis error: ", ex);

        ApiError body = new ApiError("NOT_VALID_ERROR", "Password is not valid error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptionImpl.class)
    public ResponseEntity<ApiError> handleAuthenticationExceptionImpl(AuthenticationExceptionImpl ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "User is denied error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAddressNotFoundException.class)
    public ResponseEntity<ApiError> handleEmailAddressNotFoundException(EmailAddressNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "Email address not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomUserEmailNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomUserEmailNotFoundException(CustomUserEmailNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "CustomUserEmail not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyImageURLNotFoundException.class)
    public ResponseEntity<ApiError> handlePropertyImageURLNotFoundException(PropertyImageURLNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "PropertyImageURL not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomUserImageURLNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomUserImageURLNotFoundException(CustomUserImageURLNotFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "CustomUserImageURL not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageDeleteFailedException.class)
    public ResponseEntity<ApiError> handleImageDeleteFailedException(ImageDeleteFailedException ex) {
        log.error("Delete failed error: ", ex);

        ApiError body = new ApiError("DELETE_FAILED_ERROR", "Deleting ImageURL failed error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourcesFoundException(NoResourceFoundException ex) {
        log.error("Not found error: ", ex);

        ApiError body = new ApiError("NOT_FOUND_ERROR", "No resource not found error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAddressExistsException.class)
    public ResponseEntity<ApiError> handleEmailAddressExistsException(EmailAddressExistsException ex) {
        log.error("Email address exists error: ", ex);

        ApiError body = new ApiError("EMAIL_ADDRESS_EXISTS_ERROR", "Email address exists error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SendingEmailException.class)
    public ResponseEntity<ApiError> handleSendingEmailException(SendingEmailException ex) {
        log.error("sending email error: ", ex);

        ApiError body = new ApiError("SENDING_EMAIL_ERROR", "Sending email error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ApiError> handleUsernameExistsException(UsernameExistsException ex) {
        log.error("Username exists error: ", ex);

        ApiError body = new ApiError("USERNAME_EXISTS_ERROR", "Username exists error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAdminExistsException.class)
    public ResponseEntity<ApiError> handleRoleAdminExistsException(RoleAdminExistsException ex) {
        log.error("ROLE_ADMIN exists error: ", ex);

        ApiError body = new ApiError("ROLE_ADMIN_EXISTS_ERROR", "ROLE_ADMIN exists error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenCannotBeUsedException.class)
    public ResponseEntity<ApiError> handleTokenCannotBeUsedException(TokenCannotBeUsedException ex) {
        log.error("The link is invalid or broken error: ", ex);

        ApiError body = new ApiError("TOKEN_CANNOT_BE_USED_ERROR", "The link is invalid or broken error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument error: ", ex);

        ApiError body = new ApiError("ILLEGAL_ARGUMENT_ERROR", "An illegal argument has been passed to the method.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageUploadFailedException.class)
    public ResponseEntity<ApiError> handleImageUploadFailedException(ImageUploadFailedException ex) {
        log.error("Image upload failed error: ", ex);

        ApiError body = new ApiError("IMAGE_UPLOAD_FAILED_ERROR", "Image upload is failed.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomUserPlayedTheGameException.class)
    public ResponseEntity<ApiError> handleCustomUserPlayedTheGameException(CustomUserPlayedTheGameException ex) {
        log.error("Customer played the game error: ", ex);

        ApiError body = new ApiError("CUSTOMER_PLAYED_THE_GAME_ERROR", "Customer played the game error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomUserHasNotRightNumberOfInputsForTheGameException.class)
    public ResponseEntity<ApiError> handleCustomUserHasNotRightNumberOfInputsForTheGameException(CustomUserHasNotRightNumberOfInputsForTheGameException ex) {
        log.error("Customer has more inputs for the game error: ", ex);

        ApiError body = new ApiError("CUSTOMER_HAS_MORE_INPUTS_FOR_THE_GAME_ERROR", "Customer has more inputs for the game error.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        log.error("An unexpected error occurred: ", t);

        ApiError body = new ApiError("UNCLASSIFIED_ERROR", "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

