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
    String dependenciesFilePath =
        target.hasProperty("dependenciesFile")
            ? Objects.requireNonNull(target.property("dependenciesFile")).toString()
            : DEFAULT_DEPENDENCIES_FILE;

    File dependenciesFile = new File(target.getRootDir(), dependenciesFilePath);
    DependencyParser dependencyParser = new DependencyParser();

    if (!dependenciesFile.exists()) {
      target
          .getLogger()
          .warn("No dependencies file found at: {}", dependenciesFile.getAbsolutePath());
      return;
    }

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
