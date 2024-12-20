package org.jankos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DependencyParser {
    public List<Dependency> parseJsonFile(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(new File(path), new TypeReference<>() {});

    }

    public List<Dependency> parsePropertiesFile(String path) throws IOException {
        return null;
    }
}
