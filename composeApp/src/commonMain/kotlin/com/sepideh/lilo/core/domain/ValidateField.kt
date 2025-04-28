package com.sepideh.lilo.core.domain


import com.sepideh.lilo.core.domain.model.ValidationStatus
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.error_empty_field_label
import lilo.composeapp.generated.resources.least_length_limitation
import lilo.composeapp.generated.resources.more_length_limitation
import lilo.composeapp.generated.resources.repeat_password_is_not_correct

object ValidateField
{
    fun validate(
        validationStatus: ValidationStatus,
        minLength: Int = 1,
        maxLength: Int? = null,
        isCharacter: Boolean = false,
        password: String = "",
        passwordLength: Int = 5,
        repeatPassword: String = "",
        isPassword: Boolean = false,
        isRepeatPassword: Boolean = false,
    ): ValidationStatus
    {
        println("validate: $validationStatus")
        with(validationStatus) {
            if (minLength > 0 && value.isBlank())
            {
                return validationStatus.copy(
                    isSuccessful = false,
                    messageId = Res.string.error_empty_field_label
                )
            }
            if (value.length < minLength)
            {
                return validationStatus.copy(
                    isSuccessful = false,
                    messageId = Res.string.least_length_limitation,
                )
            }
            if (maxLength!=null && value.length > maxLength)
            {
                return validationStatus.copy(
                    isSuccessful = false,
                    messageId = Res.string.more_length_limitation,
                )
            }
            if (isRepeatPassword && password != repeatPassword)
            {
                return validationStatus.copy(
                    isSuccessful = false,
                    messageId = Res.string.repeat_password_is_not_correct
                )
            }
            return validationStatus.copy(isSuccessful = true)
        }
    }
}