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


## Screenshots

<table>
  <tr>
    <td align="center">
      <img src="output/screenshots/01-home-screenshot.png" alt="Home" width="220">
      <br>Home
    </td>
    <td align="center">
      <img src="output/screenshots/02-tasks-screenshot.png" alt="Tasks" width="220">
      <br>Tasks
    </td>
    <td align="center">
      <img src="output/screenshots/03-task-form-screenshot.png" alt="Task Form" width="220">
      <br>Task Form
    </td>
    <td align="center">
      <img src="output/screenshots/04-notes-screenshot.png" alt="Notes" width="220">
      <br>Notes
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="output/screenshots/05-note-form-screenshot.png" alt="Note Form" width="220">
      <br>Note Form
    </td>
    <td align="center">
      <img src="output/screenshots/06-passwords-screenshot.png" alt="Passwords" width="220">
      <br>Passwords
    </td>
    <td align="center">
      <img src="output/screenshots/07-passwords-form-screenshot.png" alt="Password Form" width="220">
      <br>Password Form
    </td>
    <td align="center">
      <img src="output/screenshots/08-settings-screenshot.png" alt="Settings" width="220">
      <br>Settings
    </td>
  </tr>
</table>

