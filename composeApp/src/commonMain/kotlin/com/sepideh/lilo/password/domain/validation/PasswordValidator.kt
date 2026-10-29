package com.sepideh.lilo.password.domain.validation

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.normalizePasswordWebsite
import com.sepideh.lilo.password.domain.model.secretTooLong
import com.sepideh.lilo.password.domain.model.titleTooLong

/** Coordinates input rules without changing the supplied values. */
object PasswordValidator {
    fun validate(password: Password): Set<PasswordValidationError> = buildSet {
        if (!password.hasIdentifier) add(PasswordValidationError.IdentifierRequired)
        if (password.titleTooLong) add(PasswordValidationError.TitleTooLong)
        if (password.serviceId.isNullOrBlank()) add(PasswordValidationError.ServiceRequired)
        if (!password.secret.isNotBlank()) add(PasswordValidationError.SecretRequired)
        if (password.secretTooLong) add(PasswordValidationError.SecretTooLong)
        if (normalizePasswordWebsite(password.website) == null) add(PasswordValidationError.InvalidWebsite)
    }
}
