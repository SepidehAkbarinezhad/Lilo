package com.sepideh.lilo.password.domain

const val PASSWORD_TITLE_MAX_LENGTH = 60
const val PASSWORD_SECRET_MAX_LENGTH = 256

val Password.titleTooLong: Boolean get() = title.trim().length > PASSWORD_TITLE_MAX_LENGTH
val Password.secretTooLong: Boolean get() = secret.length > PASSWORD_SECRET_MAX_LENGTH

fun requirePasswordFieldLengths(password: Password) {
    require(!password.titleTooLong) { "Password title is too long" }
    require(!password.secretTooLong) { "Password is too long" }
}
