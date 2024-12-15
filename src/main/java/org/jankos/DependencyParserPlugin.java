package org.jankos;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;

public class DependencyParserPlugin implements Plugin<Project> {


    @Override
    public void apply(Project target) {
        String dependenciesFilepath = "dependencies.txt";

        Logger logger = target.getLogger();
        File dependenciesFile = new File(target.getRootDir(), dependenciesFilepath);

        if(!dependenciesFile.exists()){
            logger.warn("No dependencies.txt file found in the root directory. Skipping dependency parsing and including.");
            return;
        }


    }
}
