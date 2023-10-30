import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI("https://zebratech.jfrog.io/artifactory/EMDK-Android/")
        }
    }
}

rootProject.name = "ZebraCustomLibrary"
include(":app")
include(":ZebraModule")
