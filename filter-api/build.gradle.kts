plugins {
   `java-library`
}

dependencies {
   api("io.opentelemetry:opentelemetry-api:0.9.1")
   api(project(":javaagent-core"))
   implementation("com.google.auto.service:auto-service:1.0-rc7")
   annotationProcessor("com.google.auto.service:auto-service:1.0-rc7")
}