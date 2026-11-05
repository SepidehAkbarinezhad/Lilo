import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.firebase)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    //composeApp/build.gradle.kts
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.junit)
            implementation(libs.androidx.test.runner)
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.coil.compose)
            implementation(libs.persian.date.picker)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.material)
            implementation(compose.material3)
            implementation(libs.accompanist.systemuicontroller)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.messaging)
            implementation(libs.firebase.crashlytics)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.androidx.annotation)
            implementation(libs.kotlinx.datetime)
            implementation(libs.data.store)
            implementation(libs.material3.compose)
            implementation(libs.ui.tooling.preview)
            implementation(libs.persian.date.time)
        }
    }
}

android {
    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl.outputFileName = "Lilo-${name}.apk"
        }
    }
    namespace = "com.sepideh.lilo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sepideh.lilo.kmp.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
    }
    signingConfigs {
        create("release") {
            // Supply these only in the build environment, never in tracked properties.
            storeFile = providers.environmentVariable("LILO_RELEASE_STORE_FILE").orNull?.let { path ->
                val keystore = File(path)
                require(keystore.isAbsolute && !keystore.canonicalFile.toPath().startsWith(rootDir.canonicalFile.toPath())) {
                    "LILO_RELEASE_STORE_FILE must be an absolute path outside the repository."
                }
                keystore
            }
            storePassword = providers.environmentVariable("LILO_RELEASE_STORE_PASSWORD").orNull
            keyAlias = providers.environmentVariable("LILO_RELEASE_KEY_ALIAS").orNull
            keyPassword = providers.environmentVariable("LILO_RELEASE_KEY_PASSWORD").orNull
        }
    }
    buildTypes {
        getByName("release") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// AGP omits validateSigningRelease when credentials are incomplete, so explicitly
// gate packaging. Compilation/R8 checks can still run without a private key.
val validateReleaseSigningCredentials = tasks.register("validateReleaseSigningCredentials") {
    group = "verification"
    description = "Checks external release signing inputs without displaying credentials."
    doLast {
        val required = listOf("LILO_RELEASE_STORE_FILE", "LILO_RELEASE_STORE_PASSWORD",
            "LILO_RELEASE_KEY_ALIAS", "LILO_RELEASE_KEY_PASSWORD")
        val missing = required.filter { providers.environmentVariable(it).orNull.isNullOrBlank() }
        check(missing.isEmpty()) { "Release signing requires: ${missing.joinToString()}. See docs/android-release-signing.md." }
        check(File(providers.environmentVariable("LILO_RELEASE_STORE_FILE").get()).isFile) {
            "Release keystore file does not exist. See docs/android-release-signing.md."
        }
    }
}
tasks.matching { it.name in setOf("packageRelease", "packageReleaseBundle", "assembleRelease", "bundleRelease") }
    .configureEach { dependsOn(validateReleaseSigningCredentials) }

dependencies {
    debugImplementation(compose.uiTooling)
    // Android

    add("kspAndroid", libs.androidx.room.compiler)


    // iOS

    add("kspIosSimulatorArm64", libs.androidx.room.compiler)

    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}



// IDE resource indexing can replace these files with stubs that omit resource collectors.
// Recreate the complete accessors before compilation instead of reusing those stubs.
tasks.matching { it.name == "generateResourceAccessorsForCommonMain" }.configureEach {
    outputs.upToDateWhen { false }
}
