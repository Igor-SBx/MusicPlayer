//import org.gradle.internal.impldep.org.junit.Test
import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.example.musicplayer"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags.add("-std=c++11")
            }
        }
        ndk{
//            abiFilters += listOf("armeabi-v7a", "x86", "x86_64", "arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
//            version = "3.22.1"
        }

    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.media)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.core:core-ktx:1.12.0") //
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation ("org.robolectric:robolectric:4.11.1") //


    // Dependência do Mockito para mocks em testes unitários
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.0.0")

    // Dependência do Mockito para Android
    androidTestImplementation ("org.mockito:mockito-android:4.0.0")
}

tasks.withType(Test::class.java).configureEach {
    useJUnitPlatform()
}

