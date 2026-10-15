rootProject.name = "Lilo"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()          // <- no mavenContent
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()          // <- no mavenContent
        mavenCentral()
    }
}


include(":composeApp")
include(":iosApp")
