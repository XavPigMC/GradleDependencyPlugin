import org.jankos.Dependency;
import org.jankos.DependencyParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyJsonParseTest {
  @Test
  public void testParseDependencies() throws IOException {
    String json =
        """
                    [
                      { "group": "org.springframework.boot", "name": "spring-boot-starter-web", "version": "3.1.4" },
                      { "group": "com.fasterxml.jackson.core", "name": "jackson-databind", "version": "2.15.2" },
                      { "group": "org.hibernate", "name": "hibernate-core", "version": "6.3.1" }
                    ]
                    """;
    DependencyParser parser = new DependencyParser();

    Path tempFile = Files.createTempFile("dependencies", ".json");
    Files.write(tempFile, json.getBytes());

    List<Dependency> dependencies =
        parser.parseJsonFile(new File(tempFile.toAbsolutePath().toString()));

    assertThat(dependencies).isNotNull().hasSize(3);

    Dependency first = dependencies.get(0);
    assertThat(first.getGroup()).isEqualTo("org.springframework.boot");
    assertThat(first.getName()).isEqualTo("spring-boot-starter-web");
    assertThat(first.getVersion()).isEqualTo("3.1.4");

    Dependency second = dependencies.get(1);
    assertThat(second.getGroup()).isEqualTo("com.fasterxml.jackson.core");
    assertThat(second.getName()).isEqualTo("jackson-databind");
    assertThat(second.getVersion()).isEqualTo("2.15.2");

    Dependency third = dependencies.get(2);
    assertThat(third.getGroup()).isEqualTo("org.hibernate");
    assertThat(third.getName()).isEqualTo("hibernate-core");
    assertThat(third.getVersion()).isEqualTo("6.3.1");
  }

  @Test
  public void testParseDependenciesFromFile() throws IOException {
    DependencyParser parser = new DependencyParser();
    String filePath = "src/test/resources/TestDependencies.json";

    List<Dependency> dependencies = parser.parseJsonFile(new File(filePath));

    assertThat(dependencies).isNotNull().hasSize(2);

    assertThat(dependencies.get(0).getGroup()).isEqualTo("org.springframework");
    assertThat(dependencies.get(0).getName()).isEqualTo("spring-core");
    assertThat(dependencies.get(0).getVersion()).isEqualTo("5.3.10");

    assertThat(dependencies.get(1).getGroup()).isEqualTo("com.google.guava");
    assertThat(dependencies.get(1).getName()).isEqualTo("guava");
    assertThat(dependencies.get(1).getVersion()).isEqualTo("30.1-jre");
  }
}
