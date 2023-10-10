plugins {
    kotlin("multiplatform") version "1.9.0"
    id("maven-publish")
}

group = "com.bpavuk"
version = "Prerelease_1"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
//    js {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//    }

//    val hostOs = System.getProperty("os.name")
//    val isArm64 = System.getProperty("os.arch") == "aarch64"
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
//        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
//        hostOs == "Linux" && isArm64 -> linuxArm64("native")
//        hostOs == "Linux" && !isArm64 -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.io)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.coroutines.test)
            }
        }
        val jvmMain by getting
        val jvmTest by getting
//        val jsMain by getting
//        val jsTest by getting
//        val nativeMain by getting
//        val nativeTest by getting
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bpavuk/filery")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}