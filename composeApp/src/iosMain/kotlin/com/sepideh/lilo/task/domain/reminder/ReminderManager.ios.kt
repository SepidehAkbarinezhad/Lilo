package com.sepideh.lilo.task.domain.reminder

import platform.Foundation.NSCalendar.Companion.currentCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.timeIntervalSince1970


actual fun setReminderTime(dayMillis: Long?, hour: Int?, minute: Int?) : Long? {
    println("setReminderTime()")
    return dayMillis?.let {
        if (hour != null && minute != null) {
            // Convert dayMillis (milliseconds) to seconds
            val date = NSDate( (dayMillis / 1000).toDouble()) // Convert to seconds

            // Create an NSCalendar instance to work with date components
            val calendar = currentCalendar()

            // Create NSDateComponents with the desired hour and minute
            val components = NSDateComponents()
            components.hour = hour.toLong()
            components.minute = minute.toLong()
            components.second = 0 // Reset seconds
            components.nanosecond = 0 // Reset milliseconds

            // Add the components to the existing date
            val newDate = calendar.dateByAddingComponents(components, toDate = date, options = 0u)

            // Return the adjusted time in milliseconds (convert from seconds to milliseconds)
            return (newDate?.timeIntervalSince1970?.times(1000))?.toLong() // Convert seconds to milliseconds
        } else null
    }
}