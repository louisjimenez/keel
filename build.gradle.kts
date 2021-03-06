import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val korkVersion = "5.2.6"

buildscript {
  repositories {
    jcenter()
    gradlePluginPortal()
    maven(url = "https://spinnaker.bintray.com/gradle")
  }
  dependencies {
    classpath("com.netflix.spinnaker.gradle:spinnaker-dev-plugin:6.2.0")
  }
}

plugins {
  id("nebula.kotlin") version "1.3.31" apply false
  id("org.jetbrains.kotlin.plugin.allopen") version "1.3.31" apply false
}

allprojects {
  apply(plugin = "spinnaker.base-project")

  group = "com.netflix.spinnaker.keel"
}

subprojects {
  apply(plugin = "nebula.kotlin")

  repositories {
    jcenter()
  }

  dependencies {
    "implementation"(platform("com.netflix.spinnaker.kork:kork-bom:$korkVersion"))

    "implementation"("org.slf4j:slf4j-api")

    "testImplementation"("org.junit.platform:junit-platform-runner")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api")
    "testImplementation"("io.mockk:mockk")

    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      languageVersion = "1.3"
      jvmTarget = "1.8"
      freeCompilerArgs += "-progressive"
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform {
      includeEngines("junit-jupiter")
    }
    testLogging {
      exceptionFormat = FULL
    }
  }
}

defaultTasks(":keel-api:run")
