plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.ocrmission.vitesse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ocrmission.vitesse"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //Material design
    implementation(libs.material)

    //Ktx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    //Viewpager 2
    implementation (libs.androidx.viewpager2)

    //Lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.fragment)
    kapt(libs.hilt.android.compiler)

    //kotlinx-coroutine
    implementation(libs.kotlinx.coroutines.core)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    //Retrofit + OkHttp + Gson
    implementation (libs.retrofit)
    implementation (libs.okhttp)
    implementation (libs.moshi)
    implementation (libs.moshi.kotlin)
    implementation (libs.converter.moshi)

    //Glide
    implementation (libs.glide)
    kapt ("com.github.bumptech.glide:compiler:4.15.1")

    //Glide Transformation
    implementation(libs.glide.transformations)


    //Navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
}
kapt {
    correctErrorTypes = true
}