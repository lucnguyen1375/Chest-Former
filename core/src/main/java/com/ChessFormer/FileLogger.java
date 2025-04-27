package com.ChessFormer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileLogger {
    private final FileHandle logFile;
    private final Logger logger;
    private static final String LOG_FILE_NAME = "chess-former.log";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void clearExistedLogger() {
        // Clear existing log file
        if (logFile.exists()) {
            logFile.writeString("", false);
        }

        // Write header
        writeToFile("=== Log started ===");
    }

    public FileLogger(String className) {
        this.logFile = Gdx.files.local(LOG_FILE_NAME);
        this.logger = new Logger(className, Logger.DEBUG);
    }

    public void info(String message) {
        logger.info(message);
        writeToFile("[INFO] " + logger.getClass().toString() + ": " + message);
    }

    public void debug(String message) {
        logger.debug(message);
        writeToFile("[DEBUG] " + logger.getClass().toString() + ": " + message);
    }

    public void error(String message) {
        logger.error(message);
        writeToFile("[ERROR] " + logger.getClass().toString() + ": " + message);
    }

    private void writeToFile(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logFile.writeString("[" + timestamp + "] " + message + "\n", true);
    }

}
