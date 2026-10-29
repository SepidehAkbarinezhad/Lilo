# Lilo · لیلو

Lilo is a Kotlin Multiplatform personal organizer for Android and iOS. It brings tasks, notes, and passwords into a simple, consistent app with local storage and support for English and Persian.

## Features

- **Home:** Recent-item previews, summaries, and quick access to everyday actions.
- **Tasks:** Groups, priorities, completion tracking, search and filters, recurring reminders, and image attachments.
- **Notes:** Create and edit grouped notes with images, search, and sorting.
- **Passwords:** Organize credentials by service, with encrypted password values and device authentication for access.
- **Settings:** Light, dark, and system themes; English/LTR and Persian/RTL layouts; reminder preferences.

## Architecture

Lilo follows Clean Architecture principles with feature-based organization and separation of domain, data, and presentation responsibilities. MVI-style unidirectional state flow keeps UI state predictable. UI and business logic are shared across platforms, with native integrations for device capabilities.

## Used Technologies

- **Kotlin Multiplatform:** Shared application logic for Android and iOS.
- **Compose Multiplatform & Material 3:** Declarative UI and consistent styling.
- **Coroutines & Flow:** Asynchronous work and reactive state.
- **Koin:** Dependency injection.
- **Room Multiplatform & SQLite:** Local structured storage.
- **DataStore:** Persistent user preferences.
- **Navigation Compose:** Screen navigation.
- **Kotlin Serialization & kotlinx-datetime:** Serialization and date/time handling.
- **Firebase:** Analytics, crash reporting integration, and Android messaging.

## Screenshots

<table>
  <tr>
    <td align="center"><img src="screenShots/home-dashboard.jpg" alt="Home dashboard with task, note, and password summaries" width="220"><br>Home</td>
    <td align="center"><img src="screenShots/task-list.jpg" alt="Task list with groups, priorities, and reminders" width="220"><br>Tasks</td>
    <td align="center"><img src="screenShots/task-detail.jpg" alt="Task editor with priority, group, reminder, and image controls" width="220"><br>Task details</td>
  </tr>
  <tr>
    <td align="center"><img src="screenShots/notes-list.jpg" alt="Grouped notes with search and a swipe-to-delete action" width="220"><br>Notes</td>
    <td align="center"><img src="screenShots/note-detail.jpg" alt="Note editor showing a shopping list and group selection" width="220"><br>Note details</td>
    <td align="center"><img src="screenShots/note-create.jpg" alt="New note form with title, content, group, and image controls" width="220"><br>Create a note</td>
  </tr>
  <tr>
    <td align="center"><img src="screenShots/password-list.jpg" alt="Password list with service names, masked passwords, and reveal and copy controls" width="220"><br>Passwords</td>
    <td align="center"><img src="screenShots/password-services.jpg" alt="Service selection sheet in the password creation form" width="220"><br>Password services</td>
    <td align="center"><img src="screenShots/settings-light.jpg" alt="Settings in light mode with appearance, language, and reminder preferences" width="220"><br>Settings</td>
  </tr>
</table>
