pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RideUnify"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:accessibility")
include(":core:navigation")
include(":platform:uber")
include(":platform:cabify")
include(":feature:home")
include(":feature:settings")
include(":feature:trips")
