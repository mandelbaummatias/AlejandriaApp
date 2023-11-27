plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id ("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.matiasmandelbaum.alejandriaapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.matiasmandelbaum.alejandriaapp"
        minSdk = 26
        targetSdk = 33
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
        dataBinding = true
    }

    kapt {
        correctErrorTypes = true
    }

}

dependencies {

    implementation ("androidx.paging:paging-runtime:3.0.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation ("io.mockk:mockk:1.13.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.2.0-alpha01")

    //Activity-KTX
    implementation("androidx.activity:activity-ktx:1.7.2")

    ///Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")

    // Navigation libraries
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Retrofit
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.9.3")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-compiler:2.44.2")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Firestore
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.firebase:firebase-analytics-ktx")

    //Coil
    implementation ("io.coil-kt:coil:2.2.2")

    //Browser
    implementation ("androidx.browser:browser:1.6.0")

    //Algolia
    implementation ("com.algolia:instantsearch-android:3.+")
    implementation ("com.algolia:instantsearch-android-paging3:3.+")

    implementation ("com.algolia:algoliasearch-client-kotlin:1.8.0")
    implementation("io.ktor:ktor-client-android:2.0.1")

}

