package ca.bigmwaj.emapp.as.validator.rule.common;

import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
  Test de montée en charge : 100 threads simultanés.
  - Chaque thread exécute plusieurs appels à NotBlankRule.isValid(...)
  - Les erreurs/exception sont collectées et provoquent l'échec du test
  - Timeout global pour éviter blocage
*/
public class NotBlankRuleLoadTest {

    @Test
    public void concurrentLoadTest100Threads() throws InterruptedException {
        final int threadCount = 100;
        final int iterationsPerThread = 100; // ajuster si besoin
        final NotBlankRule rule = new NotBlankRule();

        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        Queue<Throwable> errors = new ConcurrentLinkedQueue<>();
        AtomicInteger trueCount = new AtomicInteger();
        AtomicInteger falseCount = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        for (int t = 0; t < threadCount; t++) {
            exec.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        try {
                            // cas attendu true
                            if (rule.isValid("abc", null)) {
                                trueCount.incrementAndGet();
                            } else {
                                errors.add(new AssertionError("Expected true for non-blank string"));
                            }

                            // cas attendu false : blank string
                            if (!rule.isValid("   ", null)) {
                                falseCount.incrementAndGet();
                            } else {
                                errors.add(new AssertionError("Expected false for blank string"));
                            }

                            // cas attendu false : null
                            if (!rule.isValid(null, null)) {
                                falseCount.incrementAndGet();
                            } else {
                                errors.add(new AssertionError("Expected false for null value"));
                            }

                            // cas attendu exception : non-string
                            try {
                                rule.isValid(123, null);
                                errors.add(new AssertionError("Expected exception for non-string value"));
                            } catch (Exception ignored) {
                                exceptionCount.incrementAndGet();
                            }
                        } catch (Throwable e) {
                            errors.add(e);
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    errors.add(ie);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        long start = System.nanoTime();
        startLatch.countDown(); // démarre tous les threads simultanément

        boolean finished = doneLatch.await(60, TimeUnit.SECONDS); // timeout global (ajuster si nécessaire)
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        exec.shutdownNow();

        assertTrue(finished, "Timeout waiting for threads to finish");
        assertTrue(errors.isEmpty(), "Errors occurred during concurrent execution: " + errors.size());
        // trueCount : 1 appel attendu true par itération
        assertEquals(threadCount * iterationsPerThread, trueCount.get(), "true count mismatch");
        // falseCount : 3 appels attendus false par itération
        assertEquals(threadCount * iterationsPerThread * 3, falseCount.get() + exceptionCount.get(), "false count mismatch");

        System.out.println("Completed " + (threadCount * iterationsPerThread * 4) + " calls in " + durationMs + " ms");
    }
}

