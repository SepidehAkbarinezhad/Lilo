package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.matching

import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordServiceSearchTest {
    @Test fun searchMatchesBothServiceNamesRegardlessOfLanguage() {
        val github = Password(id = "github-account", serviceName = "GitHub", serviceNameFa = "گیت‌هاب")
        val instagram = Password(id = "instagram-account", serviceName = "Instagram", serviceNameFa = "اینستاگرام")
        val records = listOf(github, instagram)
        for (query in listOf("github", " GITHUB ", "گیت‌هاب")) {
            assertEquals(listOf(github), records.matching(query, false))
        }
        for (query in listOf("Instagram", "اینستاگرام", "اينستاگرام")) {
            assertEquals(listOf(instagram), records.matching(query, false))
        }
    }
}
