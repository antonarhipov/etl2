package org.example.etl2.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ProcessedFileRenamer {

    private static final Logger log = LoggerFactory.getLogger(ProcessedFileRenamer.class);
    private static final String PROCESSED_SUFFIX = ".processed";

    public void renameProcessedFiles(Resource[] resources) {
        if (resources == null || resources.length == 0) {
            log.info("No files to rename");
            return;
        }

        for (Resource resource : resources) {
            try {
                File file = resource.getFile();
                if (file.exists()) {
                    File renamedFile = new File(file.getAbsolutePath() + PROCESSED_SUFFIX);
                    if (file.renameTo(renamedFile)) {
                        log.info("Renamed file: {} -> {}", file.getName(), renamedFile.getName());
                    } else {
                        log.warn("Failed to rename file: {}", file.getName());
                    }
                } else {
                    log.warn("File does not exist, cannot rename: {}", resource.getFilename());
                }
            } catch (IOException e) {
                log.error("Error accessing file for renaming: {}", resource.getFilename(), e);
            }
        }
    }
}
