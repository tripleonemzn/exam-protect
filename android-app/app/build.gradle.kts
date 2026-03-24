plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("com.google.dagger.hilt.android")
  id("com.google.devtools.ksp") // for Room
}

android {
  namespace = "com.examprotect.app"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.examprotect.app"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "0.1.0"

    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.12"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs += listOf(
      "-Xjvm-default=all",
      "-opt-in=kotlin.RequiresOptIn"
    )
  }

  val backendBaseUrl = System.getenv("BACKEND_BASE_URL") ?: ""
  buildTypes {
    debug {
      isMinifyEnabled = false
      buildConfigField("String", "BACKEND_BASE_URL", "\"$backendBaseUrl\"")
      buildConfigField("boolean", "USE_LOCAL_POLICY", "true")
      buildConfigField("boolean", "ENABLE_HTTP_LOGGING", "true")
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      buildConfigField("String", "BACKEND_BASE_URL", "\"$backendBaseUrl\"")
      buildConfigField("boolean", "USE_LOCAL_POLICY", "false")
      buildConfigField("boolean", "ENABLE_HTTP_LOGGING", "false")
    }
  }
}

dependencies {
  val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
  implementation(composeBom)
  androidTestImplementation(composeBom)

  implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23")
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3:1.3.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.navigation:navigation-compose:2.8.3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
  implementation("androidx.datastore:datastore-preferences:1.1.1")
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")

  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
  testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
  implementation("androidx.webkit:webkit:1.9.0")

  implementation("com.google.dagger:hilt-android:2.51.1")
  kapt("com.google.dagger:hilt-android-compiler:2.51.1")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}
