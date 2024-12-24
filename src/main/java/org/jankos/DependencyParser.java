package org.jankos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DependencyParser {

  private static final String INVALID_DEPENDENCY_VALUE = "Invalid dependency value";

  public List<Dependency> parseDependencies(File dependenciesFile) {
    try {
      String content = java.nio.file.Files.readString(dependenciesFile.toPath()).trim();

      if (content.startsWith("{") || content.startsWith("[")) {
        return parseJsonFile(dependenciesFile);
      }

      return parsePropertiesFile(dependenciesFile);

    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to parse dependencies file: " + dependenciesFile.getAbsolutePath(), e);
    }
  }

  public List<Dependency> parseJsonFile(File file) throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    return mapper.readValue(file, new TypeReference<>() {});
  }

  public List<Dependency> parsePropertiesFile(File file) throws IOException {
    Properties properties = new Properties();
    try (FileReader reader = new FileReader(file)) {
      properties.load(reader);
    }

    List<Dependency> dependencies = new ArrayList<>();
    for (String value : properties.stringPropertyNames()) {
      dependencies.add(parseDependencyString(properties.getProperty(value)));
    }

    return dependencies;
  }

  private Dependency parseDependencyString(String dependencyString) {
    String[] parts = dependencyString.split(":");
    if (parts.length != 3) {
      throw new IllegalArgumentException(INVALID_DEPENDENCY_VALUE + dependencyString);
    }

    return new Dependency(parts[0], parts[1], parts[2]);
  }
}
