package com.sepideh.lilo.password.domain.model

data class PasswordService(val id: String, val name: String, val isDefault: Boolean = false, val nameFa: String = name)
