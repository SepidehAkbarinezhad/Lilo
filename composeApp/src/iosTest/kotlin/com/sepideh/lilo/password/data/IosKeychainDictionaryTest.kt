package com.sepideh.lilo.password.data

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRetain
import platform.LocalAuthentication.LAContext
import platform.Security.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalForeignApi::class)
class IosKeychainDictionaryTest {
    @Test fun keychainReturnFlagsUseNativeBooleans() {
        withDictionary(mapOf(cfObject(kSecReturnRef) to true, cfObject(kSecReturnData) to false,
            cfObject(kSecReturnAttributes) to true)) { query ->
            for ((key, expected) in listOf(kSecReturnRef to true, kSecReturnData to false, kSecReturnAttributes to true)) {
                val value = CFDictionaryGetValue(query, key)
                assertEquals(CFBooleanGetTypeID(), CFGetTypeID(value))
                assertEquals(expected, CFBooleanGetValue(value?.reinterpret()))
            }
        }
    }

    @Test fun nestedKeyAttributesAndAuthenticationContextKeepTheirNativeTypes() {
        val context = LAContext()
        try {
            withDictionary(mapOf(cfObject(kSecPrivateKeyAttrs) to mapOf(cfObject(kSecAttrIsPermanent) to true),
                cfObject(kSecUseAuthenticationContext) to context, cfObject(kSecAttrKeySizeInBits) to 2048)) { query ->
                val attributes: CFDictionaryRef = CFDictionaryGetValue(query, kSecPrivateKeyAttrs)!!.reinterpret()
                assertEquals(CFDictionaryGetTypeID(), CFGetTypeID(attributes))
                val permanent = CFDictionaryGetValue(attributes, kSecAttrIsPermanent)
                assertEquals(CFBooleanGetTypeID(), CFGetTypeID(permanent))
                assertTrue(CFBooleanGetValue(permanent?.reinterpret()))
                val nativeContext = requireNotNull(CFBridgingRetain(context))
                try { assertEquals(nativeContext, CFDictionaryGetValue(query, kSecUseAuthenticationContext)) }
                finally { CFRelease(nativeContext) }
                assertEquals(CFNumberGetTypeID(), CFGetTypeID(CFDictionaryGetValue(query, kSecAttrKeySizeInBits)))
            }
        } finally { context.invalidate() }
    }
}
