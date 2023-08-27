plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("maven-publish")


}

android {
    namespace = "com.example.mylibrary"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        viewBinding  = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
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

    val multidex_version = "2.0.1"
    implementation("androidx.multidex:multidex:$multidex_version")
    implementation ("com.google.code.gson:gson:2.8.9")
}
publishing {
    publications {
        create<MavenPublication>("ReleaseAar") {
            groupId = "com.example.mylibrary"
            artifactId = "magicbid"
            version = "1.2"
            afterEvaluate {
                artifact(tasks.getByName("bundleReleaseAar"))
            }
            /* create<MavenPublication>("maven") {

            groupId = "com.example.mylibrary"
            artifactId = "magicbid"
            version = "1.1"
            artifact("$buildDir/outputs/aar/${project.name}-release.aar") // this is the solution I came up with


            from(components["java"])
        */
        }
    }
}