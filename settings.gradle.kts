pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "screen-translator"

include(":app")
include(":core:datastore")
include(":core:data")
include(":core:design")
include(":core:ui")
include(":core:model")
include(":core:resource")
include(":feature:settings")
include(":feature:translate")
