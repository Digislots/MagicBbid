
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.test"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.test"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding  = true

    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(mapOf("path" to ":magicbid")))
  //  implementation(files("./libs/magicbid-debug.aar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")


    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")


    implementation ("com.google.android.gms:play-services-ads:22.2.0")

    implementation ("com.google.android.ump:user-messaging-platform:2.1.0")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-process:2.3.1")
     implementation ("androidx.core:core-ktx:1.5.0")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation ("com.inmobi.omsdk:inmobi-omsdk:1.3.17.1")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
     implementation ("androidx.browser:browser:1.4.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.viewpager:viewpager:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.gms:play-services-appset:16.0.2")//optional dependency for better targeting
    implementation ("com.google.android.gms:play-services-tasks:18.0.2") //optional dependency for better targeting
    implementation ("androidx.multidex:multidex:2.0.1")

    implementation ("com.inmobi.monetization:inmobi-ads:10.1.3")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")


}