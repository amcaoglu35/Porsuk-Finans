import config.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val detektPluginId = libs.findPlugin("detekt").get().get().pluginId

        pluginManager.apply(detektPluginId)

        dependencies {
            add("detektPlugins", libs.findLibrary("compose.rules.detekt").get())
            add("detektPlugins", libs.findLibrary("detekt.formatting").get())
        }

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            parallel = true

            basePath = rootProject.layout.projectDirectory.asFile.absolutePath
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))

            val moduleConfig = project.file("detekt.yml")
            if (moduleConfig.exists()) {
                config.from(moduleConfig)
            }

            baseline = project.file("detekt-baseline.xml")
        }

        tasks.withType<Detekt>().configureEach {
            jvmTarget = "17"

            reports {
                xml.required.set(true)
                html.required.set(true)
                sarif.required.set(true)
                md.required.set(false)
            }

            if (project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
                val javaExtension = extensions.findByType(JavaPluginExtension::class.java)
                javaExtension?.let {
                    classpath.setFrom(it.sourceSets.getByName("main").compileClasspath)
                }
            }
        }

        tasks.withType<DetektCreateBaselineTask>().configureEach {
            jvmTarget = "17"
        }
    }
}