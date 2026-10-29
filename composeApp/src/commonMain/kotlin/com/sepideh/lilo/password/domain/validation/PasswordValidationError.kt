package com.sepideh.lilo.password.domain.validation

enum class PasswordValidationError {
    IdentifierRequired,
    TitleTooLong,
    ServiceRequired,
    SecretRequired,
    SecretTooLong,
    InvalidWebsite,
}

class PasswordValidationException(val errors: Set<PasswordValidationError>) :
    IllegalArgumentException("Password validation failed")
