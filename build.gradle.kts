plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("com.diffplug.spotless") version "8.8.0" apply false
    id("org.jetbrains.kotlinx.kover") version "0.9.8" apply false
}

tasks.register("ktlintCheck") {
    dependsOn(":app:spotlessCheck")
}

tasks.register("koverHtmlReport") {
    dependsOn(":app:koverHtmlReportDebug")
}
