package com.sepideh.lilo.password.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.password.data.IosVaultCrypto
import com.sepideh.lilo.password.data.passwordDirectory
import com.sepideh.lilo.password.data.local.PasswordDatabase
import com.sepideh.lilo.password.data.local.PasswordDbConstructor
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.clipboard.PasswordClipboard
import com.sepideh.lilo.password.data.IosPasswordClipboard
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

actual fun passwordPlatformModule() = module {
    single<PasswordClipboard> { IosPasswordClipboard() }
    single<VaultCrypto> { IosVaultCrypto() }
    single { Room.databaseBuilder<PasswordDatabase>(name = passwordDirectory() + "/passwords.db", factory = { PasswordDbConstructor.initialize() })
        .addCallback(com.sepideh.lilo.password.data.local.PasswordDefaultsCallback).setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.Default).build() }
}
