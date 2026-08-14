package com.vdbond;

import com.vdbond.config.GameConfigLoader;
import com.vdbond.domain.GameConfig;
import com.vdbond.report.ReportFormatter;
import com.vdbond.simulation.SimulationRunner;
import com.vdbond.stats.SimulationStatistics;
import com.vdbond.stats.StatisticsAccumulator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class Main {

    private static final Path REPORT_PATH = Path.of("report.txt");

    static void main(String[] args) {
        try {
            run(args);
        } catch (IllegalArgumentException | UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args) {
        CliOptions options = CliOptions.parse(args);
        GameConfig config = GameConfigLoader.loadFromClasspath(options.configResource());

        Instant start = Instant.now();
        StatisticsAccumulator accumulator = new SimulationRunner(config, new SecureRandom())
                .run(options.rounds());
        Duration elapsed = Duration.between(start, Instant.now());

        SimulationStatistics stats = accumulator.summarize(config.bet());
        String report = ReportFormatter.format(stats, elapsed);

        System.out.println(report);
        writeReport(report);
    }

    private static void writeReport(String report) {
        try {
            Files.writeString(REPORT_PATH, report);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write report to " + REPORT_PATH, e);
        }
    }

}
