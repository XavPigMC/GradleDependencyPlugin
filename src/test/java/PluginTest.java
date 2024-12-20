import org.gradle.api.artifacts.Configuration;
import org.jankos.DependencyParser;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginTest {
  @Test
  public void addDependencyTest() throws IOException {
    Project project = ProjectBuilder.builder().build();
    org.jankos.Dependency dependency =
        new DependencyParser().parseJsonFile("src/test/resources/TestDependencies.json").get(0);

    Configuration configuration = project.getConfigurations().getByName("implementation");


    configuration
            .getDependencies()
            .add(project.getDependencies().create(dependency.DependencyNotation()));

    assertThat(configuration.getDependencies().contains(project.getDependencies().create(dependency.DependencyNotation()))).isTrue();
  }
}
