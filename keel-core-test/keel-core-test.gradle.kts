plugins {
  `java-library`
}

dependencies {
  implementation(project(":keel-core"))
  implementation("org.junit.jupiter:junit-jupiter-api")
  implementation("io.strikt:strikt-core")
  implementation("io.mockk:mockk")
  api("dev.minutest:minutest")
}
