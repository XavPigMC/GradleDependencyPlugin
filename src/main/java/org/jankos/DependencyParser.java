package org.jankos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DependencyParser {
  public List<Dependency> parseJsonFile(String path) throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    return mapper.readValue(new File(path), new TypeReference<>() {});
  }

  public List<Dependency> parsePropertiesFile(String path) throws IOException {
    List<Dependency> dependencies = new ArrayList<>();
    Properties properties = new Properties();

    try (InputStreamReader reader =
        new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) {
      properties.load(reader);
    }


    for (Object value : properties.values()) {
      if (!(value instanceof String current)) {
        throw new IllegalArgumentException("Invalid dependency value: " + value);
      }

      String[] split = current.split(":");

      if(split.length != 3)
        throw new IllegalArgumentException("Invalid dependency value: " + Arrays.toString(split) + " of length " + split.length);

      Dependency dependency = new Dependency();
      dependency.setGroup(split[0]);
      dependency.setName(split[1]);
      dependency.setVersion(split[2]);

      dependencies.add(dependency);
    }

    return dependencies;
  }
}
