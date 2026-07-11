plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.dokka)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "multispot"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
        }
    }
}

android {
    namespace = "io.github.yutarosuzuki_jp.multispot"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

publishing {
    publications.withType<MavenPublication> {
        val pubName = name
        val javadocTask = tasks.register("${pubName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(pubName)
            from(tasks.named("dokkaHtml"))
        }
        artifact(javadocTask.get())

        pom {
            name.set("Multispot")
            description.set("A lightweight and flexible coachmark library for Kotlin Multiplatform and Compose Multiplatform.")
            url.set("https://github.com/YutaroSuzuki-JP/multispot")
            
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("yutarosuzuki-jp")
                    name.set("Yutaro Suzuki")
                    email.set("i.buzzbuzzinc@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/YutaroSuzuki-JP/multispot.git")
                developerConnection.set("scm:git:ssh://github.com/YutaroSuzuki-JP/multispot.git")
                url.set("https://github.com/YutaroSuzuki-JP/multispot")
            }
        }
    }
    
    repositories {
        maven {
            name = "layout"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
