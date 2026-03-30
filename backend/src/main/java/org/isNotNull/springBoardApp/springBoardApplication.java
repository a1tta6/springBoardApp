package org.isNotNull.springBoardApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the backend that serves the React frontend.
 *
 * Example:
 * Run the application and open http://localhost:9500/api
 */
@SpringBootApplication
public class springBoardApplication {

    public static void main(final String[] arguments) {
        SpringApplication.run(springBoardApplication.class, arguments);
    }
}
