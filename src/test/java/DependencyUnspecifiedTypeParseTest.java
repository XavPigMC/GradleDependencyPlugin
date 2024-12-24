import org.jankos.Dependency;
import org.jankos.DependencyParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyUnspecifiedTypeParseTest {

  @Test
  public void testParseDependenciesFromUnspecifiedJsonFile() {
    DependencyParser parser = new DependencyParser();
    String filePath = "src/test/resources/UnspecifiedTypeJson.txt";

    List<Dependency> dependencies = parser.parseDependencies(new File(filePath));

    assertThat(dependencies).isNotNull().hasSize(2);

    assertThat(dependencies.get(0).getGroup()).isEqualTo("org.springframework");
    assertThat(dependencies.get(0).getName()).isEqualTo("spring-core");
    assertThat(dependencies.get(0).getVersion()).isEqualTo("5.3.10");

    assertThat(dependencies.get(1).getGroup()).isEqualTo("com.google.guava");
    assertThat(dependencies.get(1).getName()).isEqualTo("guava");
    assertThat(dependencies.get(1).getVersion()).isEqualTo("30.1-jre");
  }

  @Test
  public void testParseDependenciesFromUnspecifiedPropertiesFile() {
    DependencyParser parser = new DependencyParser();
    String filePath = "src/test/resources/TestDependencies.properties";

    List<Dependency> dependencies = parser.parseDependencies(new File(filePath));

    assertThat(dependencies).isNotNull().hasSize(2);

    /*
    dependency1=org.apache.commons:commons-lang3:3.12.0
    dependency2=com.google.guava:guava:32.0.1-jre
     */

    Dependency testDependency1 = new Dependency();
    testDependency1.setGroup("org.apache.commons");
    testDependency1.setName("commons-lang3");
    testDependency1.setVersion("3.12.0");

    Dependency testDependency2 = new Dependency();
    testDependency2.setGroup("com.google.guava");
    testDependency2.setName("guava");
    testDependency2.setVersion("32.0.1-jre");

    assertThat(dependencies).contains(testDependency1);
    assertThat(dependencies).contains(testDependency2);
  }
}
