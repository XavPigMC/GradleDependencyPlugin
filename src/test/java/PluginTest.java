import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.jankos.DependencyParser;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginTest {

  //I was just wondering whether this even works.   it does.    yay.
  @Test
  public void addDependencyTest() throws IOException {
    Project project = ProjectBuilder.builder().build();

    project.getPluginManager().apply(JavaPlugin.class);

    org.jankos.Dependency dependency =
        new DependencyParser().parseJsonFile("src/test/resources/TestDependencies.json").getFirst();

    Configuration configuration = project.getConfigurations().getByName("implementation");


    configuration
            .getDependencies()
            .add(project.getDependencies().create(dependency.notation()));

    assertThat(configuration.getDependencies().contains(project.getDependencies().create(dependency.notation()))).isTrue();
  }
}
