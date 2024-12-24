import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.fail;

public class PluginTest {

  public static final String BUILD_FILE_CONTENT =
      """
          buildscript {
            repositories {
              maven {
                url uri("${System.getProperty('user.home')}/.m2/repository")  // Local Maven repository
              }
            }
            dependencies {
              classpath 'org.jankos:dependency-parser-plugin:1.0'  // Make sure this matches your plugin version
            }
          }

          plugins {
            id 'java'
          }

          repositories {
            mavenCentral()
            maven {
              url uri("${System.getProperty('user.home')}/.m2/repository")  // Local Maven repository
            }
          }

          apply plugin: 'org.jankos.dependency-parser-plugin'
          """;
  private File testProjectDir;

  @BeforeEach
  public void setUp() throws IOException {
    testProjectDir = Files.createTempDirectory("gradle-test").toFile();
    //noinspection ResultOfMethodCallIgnored
    new File(testProjectDir, "settings.gradle").createNewFile(); // required for gradle
  }

  @Test
  public void testPluginAddsDependenciesFromJsonFile() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    File dependenciesFile = new File(testProjectDir, "dependencies.txt");
    String jsonContent =
        """
            [
              { "group": "org.springframework.boot", "name": "spring-boot-starter-web", "version": "3.1.4" },
              { "group": "com.fasterxml.jackson.core", "name": "jackson-databind", "version": "2.15.2" }
            ]
            """;
    Files.write(dependenciesFile.toPath(), jsonContent.getBytes());

    var result =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("dependencies")
            .withPluginClasspath()
            .build();

    assertThat(Objects.requireNonNull(result.task(":dependencies")).getOutcome())
        .isEqualTo(SUCCESS);
    assertThat(result.getOutput())
        .contains("org.springframework.boot:spring-boot-starter-web:3.1.4")
        .contains("com.fasterxml.jackson.core:jackson-databind:2.15.2");
  }

  @Test
  public void testPluginHandlesMissingDependenciesFile() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    var result =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("dependencies")
            .withPluginClasspath()
            .build();

    assertThat(Objects.requireNonNull(result.task(":dependencies")).getOutcome())
        .isEqualTo(SUCCESS);
    assertThat(result.getOutput()).contains("No dependencies file found");
  }

  @Test
  public void testPluginAddsDependenciesFromPropertiesFile() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    File dependenciesFile = new File(testProjectDir, "dependencies.txt");
    String propertiesContent =
        """
            dependency1=org.apache.commons:commons-lang3:3.12.0
            dependency2=com.google.guava:guava:32.0.1-jre
            """;
    Files.write(dependenciesFile.toPath(), propertiesContent.getBytes());

    var result =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("dependencies")
            .withPluginClasspath()
            .build();

    assertThat(Objects.requireNonNull(result.task(":dependencies")).getOutcome())
        .isEqualTo(SUCCESS);
    assertThat(result.getOutput())
        .contains("org.apache.commons:commons-lang3:3.12.0")
        .contains("com.google.guava:guava:32.0.1-jre");
  }

  @Test
  public void testPluginDoesNotFailWhenNoDefaultDependenciesFileFound() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    var result =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("build")
            .withPluginClasspath()
            .build();

    assertThat(Objects.requireNonNull(result.task(":build")).getOutcome()).isEqualTo(SUCCESS);
  }

  @Test
  public void testPluginFailsWhenNoCustomDependenciesFileFound() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    String buildPropertiesContent =
        """
      dependenciesFile=custom-dependencies.txt
    """;
    File buildPropertiesFile = new File(testProjectDir, "gradle.properties");
    Files.write(buildPropertiesFile.toPath(), buildPropertiesContent.getBytes());

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("build")
          .withPluginClasspath()
          .build();

      fail("Expected build to fail due to missing custom dependencies file");
    } catch (Exception e) {
      String expectedErrorMessage = "custom-dependencies.txt does not exist";
      assertThat(e.getMessage()).contains(expectedErrorMessage);
    }
  }

  @Test
  public void testPluginAddsDependenciesFromCustomDependenciesFile() throws IOException {
    File buildFile = new File(testProjectDir, "build.gradle");
    Files.write(buildFile.toPath(), BUILD_FILE_CONTENT.getBytes());

    String buildPropertiesContent =
        """
  dependenciesFile=custom-dependencies.txt
""";
    File buildPropertiesFile = new File(testProjectDir, "gradle.properties");
    Files.write(buildPropertiesFile.toPath(), buildPropertiesContent.getBytes());

    File dependenciesFile = new File(testProjectDir, "custom-dependencies.txt");
    String propertiesContent =
        """
                dependency1=org.apache.commons:commons-lang3:3.12.0
                dependency2=com.google.guava:guava:32.0.1-jre
                """;
    Files.write(dependenciesFile.toPath(), propertiesContent.getBytes());

    var result =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("dependencies")
            .withPluginClasspath()
            .build();

    assertThat(Objects.requireNonNull(result.task(":dependencies")).getOutcome())
        .isEqualTo(SUCCESS);
    assertThat(result.getOutput())
        .contains("org.apache.commons:commons-lang3:3.12.0")
        .contains("com.google.guava:guava:32.0.1-jre");
  }
}
