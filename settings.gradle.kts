pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
        google()
        mavenCentral()
        jcenter()
    }
}
rootProject.name = "Yaqeen"
include(":app")
include(":cardstackview")
//include(":sample")