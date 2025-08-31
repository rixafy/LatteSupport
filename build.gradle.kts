import kotlinx.kover.tasks.KoverXmlTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.17.3"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
    id("org.jetbrains.changelog") version "2.2.0"
    id("org.jetbrains.qodana") version "0.1.13"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
    test {
        java.srcDirs("src/main/gen")
    }
}

// https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) })
}

grammarKit {
    // Version of IntelliJ patched JFlex (see the link below), Default is 1.7.0-1
    jflexRelease.set(properties("jflexRelease"))

    // Release version, tag, or short commit hash of Grammar-Kit to use (see link below). Default is 2021.1.2
    grammarKitRelease.set(properties("grammarKitRelease"))
}

// https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(provider { file(".qodana").canonicalPath })
    reportPath.set(provider { file("build/reports/inspections").canonicalPath })
    saveReport.set(true)
    showReport.set(environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false))
}

// https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
    onCheck.set(true)
}

val generateLatteParser = task("generateLatteParser", GenerateParserTask::class) {
    sourceFile.set(File("src/main/java/org/nette/latte/parser/LatteParser.bnf"))
    targetRootOutputDir.set(File("src/main/gen"))
    pathToParser.set("/org/nette/latte/parser/LatteParser.java")
    pathToPsiRoot.set("/org/nette/latte/psi")
    purgeOldFiles.set(false)
}

val generateLatteMacroContentLexer = task<GenerateLexerTask>("generateLatteMacroContentLexer") {
    sourceFile.set(File("src/main/java/org/nette/latte/lexer/grammars/LatteMacroContentLexer.flex"))
    targetOutputDir.set(File("src/main/gen/org/nette/latte/lexer"))
    purgeOldFiles.set(false)
}

val generateLatteMacroLexer = task<GenerateLexerTask>("generateLatteMacroLexer") {
    sourceFile.set(File("src/main/java/org/nette/latte/lexer/grammars/LatteMacroLexer.flex"))
    targetOutputDir.set(File("src/main/gen/org/nette/latte/lexer"))
    purgeOldFiles.set(false)
}

val generateLatteTopLexer = task<GenerateLexerTask>("generateLatteTopLexer") {
    sourceFile.set(File("src/main/java/org/nette/latte/lexer/grammars/LatteTopLexer.flex"))
    targetOutputDir.set(File("src/main/gen/org/nette/latte/lexer"))
    purgeOldFiles.set(false)
}

val generateLattePhpLexer = task<GenerateLexerTask>("generateLattePhpLexer") {
    sourceFile.set(File("src/main/java/org/nette/latte/lexer/grammars/LattePhpLexer.flex"))
    targetOutputDir.set(File("src/main/gen/org/nette/latte/lexer"))
    purgeOldFiles.set(false)
}

changelog {
    version.set(properties("pluginVersion"))
    repositoryUrl = properties("pluginRepositoryUrl")
    path.set(file("CHANGELOG.md").canonicalPath)
}

tasks {
    generateLexer.configure { enabled = false }
    generateParser.configure { enabled = false }

    // Set the JVM compatibility versions
    properties("javaVersion").get().let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    withType<KoverXmlTask> {
        dependsOn("compileJava")
    }

    withType<KotlinCompile> {
        dependsOn(
            generateLatteMacroContentLexer,
            generateLatteMacroLexer,
            generateLatteTopLexer,
            generateLattePhpLexer,
            generateLatteParser
        )
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        })

        changeNotes.set( provider {
            changelog.getAll().values.joinToString("\n") { changelog.renderItem(it, Changelog.OutputType.HTML) }
        })
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    publishPlugin {
        token.set(environment("PLUGIN_PUBLISH_TOKEN"))
    }
}
