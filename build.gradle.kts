plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

application.mainClass = "AlexeyPG.bots.M3.Main"

group = "AlexeyPG.bots.M3"
version = "1.0"

repositories {
    mavenCentral()
    maven {url = uri("https://jitpack.io")}
}



dependencies {
    implementation("javax.servlet:javax.servlet-api:3.1.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:6.7.0")
    implementation("org.apache.tomcat:tomcat-catalina:11.0.27")
    implementation("org.jsoup:jsoup:1.23.2")
    implementation("com.google.code.gson:gson:2.15.0")


    //implementation("com.github.Mokulu:discord-oauth2-api:1.0.4") used same lib locally with just updated dependencies:
    implementation(files("libs/discord-oauth2-api.jar"))
}


tasks.test {
    useJUnitPlatform()
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true

    // Set this to the version of java you want to use,
    // the minimum required for JDA is 1.8
    sourceCompatibility = "17"
}