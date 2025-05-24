package com.xenoamess.x8l;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PerformanceTest {

    private static final String RPG_SETTINGS_FILE_PATH = "src/test/resources/RpgModuleDemoSettings.x8l";
    private static final String LARGE_BENCHMARK_FILE_PATH = "src/test/resources/large_sample_benchmark.x8l";
    private static final int WARMUP_ITERATIONS = 5;
    private static final int TEST_ITERATIONS = 10;

    private void runPerformanceTest(String filePath, String testName) {
        System.out.println("Starting X8L Performance Test: " + testName + " for file: " + filePath);
        File x8lFile = new File(filePath);

        if (!x8lFile.exists()) {
            System.err.println("X8L file not found: " + filePath);
            fail("X8L file not found: " + filePath);
            return;
        }

        List<Long> parsingTimes = new ArrayList<>();
        List<Long> serializationTimes = new ArrayList<>();
        X8lTree tree = null;

        try {
            // Warm-up phase
            System.out.println("Warm-up phase (" + WARMUP_ITERATIONS + " iterations) for " + testName + "...");
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                X8lTree warmUpTree = X8lTree.load(x8lFile);
                String x8lStringOutputWarmup = X8lTree.save(warmUpTree); // Perform serialization
            }

            // Test phase
            System.out.println("Test phase (" + TEST_ITERATIONS + " iterations) for " + testName + "...");
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                long startTimeParsing = System.nanoTime();
                tree = X8lTree.load(x8lFile);
                long endTimeParsing = System.nanoTime();
                parsingTimes.add(endTimeParsing - startTimeParsing);

                if (tree == null) { // Should not happen if load is successful
                    fail("Tree is null after parsing in " + testName);
                    return;
                }

                long startTimeSerialization = System.nanoTime();
                String x8lStringOutput = X8lTree.save(tree); // Perform serialization
                long endTimeSerialization = System.nanoTime();
                serializationTimes.add(endTimeSerialization - startTimeSerialization);
            }

            // Calculate and print averages
            double avgParsingTimeMs = parsingTimes.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
            double avgSerializationTimeMs = serializationTimes.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;

            System.out.println("\n--- Performance Results for " + testName + " ---");
            System.out.printf("Average X8L Parsing Time: %.3f ms%n", avgParsingTimeMs);
            System.out.printf("Average X8L Serialization Time (to String): %.3f ms%n", avgSerializationTimeMs);

            // Optionally, inspect the structure of the last parsed tree for the large benchmark
            if (filePath.equals(LARGE_BENCHMARK_FILE_PATH) && tree != null && tree.getRoot() != null) {
                System.out.println("\n--- Parsed Tree Structure for " + testName + " ---");
                System.out.println("Root node's children count: " + tree.getRoot().getChildren().size());
                if (!tree.getRoot().getChildren().isEmpty()) {
                    AbstractTreeNode firstChild = tree.getRoot().getChildren().get(0);
                    if (firstChild instanceof ContentNode) {
                        System.out.println("Name of the first child of root: " + ((ContentNode) firstChild).getName());
                    } else if (firstChild instanceof TextNode) {
                        System.out.println("First child of root is a TextNode with content (first 50 chars): \"" + ((TextNode) firstChild).getTextContent().substring(0, Math.min(50, ((TextNode) firstChild).getTextContent().length())) + "...\"");
                    } else if (firstChild instanceof CommentNode) {
                        System.out.println("First child of root is a CommentNode.");
                    }
                } else {
                    System.out.println("Root node has no children.");
                }
            }

        } catch (IOException e) {
            System.err.println("Error during performance test " + testName + ": " + e.getMessage());
            e.printStackTrace();
            fail("IOException during performance test " + testName + ": " + e.getMessage());
        }
        System.out.println("Performance Test " + testName + " Finished.");
    }

    @Test
    @Disabled("Performance benchmark, run manually or via specific profile")
    void testPerformance_RpgModuleDemoSettings() {
        runPerformanceTest(RPG_SETTINGS_FILE_PATH, "RpgModuleDemoSettings");
    }

    @Test
    @Disabled("Performance benchmark, run manually or via specific profile")
    void testPerformance_LargeSampleBenchmark() {
        runPerformanceTest(LARGE_BENCHMARK_FILE_PATH, "LargeSampleBenchmark");
    }
}
