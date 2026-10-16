package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.PasswordClipboard
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.UIKit.UIPasteboard
import platform.UIKit.UIPasteboardOptionExpirationDate
import platform.UIKit.UIPasteboardOptionLocalOnly

class IosPasswordClipboard : PasswordClipboard {
    override fun copyPassword(value: String) {
        UIPasteboard.generalPasteboard.setItems(listOf(mapOf("public.utf8-plain-text" to value)),
            options = mapOf(UIPasteboardOptionLocalOnly to true,
                UIPasteboardOptionExpirationDate to NSDate.dateWithTimeIntervalSinceNow(60.0)))
    }
}
