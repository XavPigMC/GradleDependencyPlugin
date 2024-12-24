package org.jankos;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class DependencyParserPlugin implements Plugin<Project> {

  private static final String DEFAULT_DEPENDENCIES_FILE = "dependencies.txt";

  @Override
  public void apply(Project target) {
    File dependenciesFile;
    if (target.hasProperty("dependenciesFile")) {
      target.getLogger().info("Using custom dependencies file");
      String dependenciesFilePath =
          Objects.requireNonNull(target.property("dependenciesFile")).toString();
      dependenciesFile = new File(target.getRootDir(), dependenciesFilePath);
      if (!dependenciesFile.exists()) {
        throw new IllegalArgumentException(dependenciesFilePath + " does not exist");
      }
    } else {
      target.getLogger().info("Using default dependencies file");
      dependenciesFile = new File(target.getRootDir(), DEFAULT_DEPENDENCIES_FILE);

      if (!dependenciesFile.exists()) {
        target
            .getLogger()
            .warn("No dependencies file found at: {}", dependenciesFile.getAbsolutePath());
        return;
      }
    }

    DependencyParser dependencyParser = new DependencyParser();

    String fileContent;

    try {
      List<Dependency> dependencies = dependencyParser.parseDependencies(dependenciesFile);
      dependencies.forEach(dep -> target.getDependencies().add("implementation", dep.notation()));
    } catch (RuntimeException e) {
      target.getLogger().error("Error processing dependencies file", e);
      throw e;
    }
  }
}
