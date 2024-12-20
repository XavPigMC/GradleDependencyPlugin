package org.jankos;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DependencyParserPlugin implements Plugin<Project> {

  @Override
  public void apply(Project target) {
    String dependenciesFilepath = "dependencies.txt";
    List<Dependency> dependencyList;
    Logger logger = target.getLogger();
    File dependenciesFile = new File(target.getRootDir(), dependenciesFilepath);

    if (!dependenciesFile.exists()) {
      logger.warn(
          "No dependencies.txt file found in the root directory. Skipping dependency parsing and including.");
      return;
    }
    String fileContent;
    try {
      fileContent = IOUtils.toString(new FileReader(dependenciesFile)).trim();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (fileContent.startsWith("{") || fileContent.startsWith("[")) {
      try {
        dependencyList = new DependencyParser().parseJsonFile(dependenciesFilepath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      try {
        dependencyList = new DependencyParser().parsePropertiesFile(dependenciesFilepath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    for (Dependency dependency : dependencyList) {
      target
          .getConfigurations()
          .getByName("implementation")
          .getDependencies()
          .add(target.getDependencies().create(dependency.DependencyNotation()));
    }
  }
}
