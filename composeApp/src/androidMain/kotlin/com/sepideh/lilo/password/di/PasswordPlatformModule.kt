package com.sepideh.lilo.password.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.password.data.AndroidVaultCrypto
import com.sepideh.lilo.password.data.passwordDirectory
import com.sepideh.lilo.password.data.local.PasswordDatabase
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.clipboard.PasswordClipboard
import com.sepideh.lilo.password.data.AndroidPasswordClipboard
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun passwordPlatformModule() = module {
    single<PasswordClipboard> { AndroidPasswordClipboard(androidContext()) }
    single<VaultCrypto> { AndroidVaultCrypto(androidContext()) }
    single { Room.databaseBuilder<PasswordDatabase>(androidContext(), passwordDirectory(androidContext()).resolve("passwords.db").absolutePath)
        .addCallback(com.sepideh.lilo.password.data.local.PasswordDefaultsCallback).setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build() }
}
