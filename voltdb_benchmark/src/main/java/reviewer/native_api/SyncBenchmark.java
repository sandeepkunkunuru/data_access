/*
 * This samples uses multiple threads to post synchronous requests to the
 * VoltDB server, simulating multiple client application posting
 * synchronous requests to the database, using the native VoltDB client
 * library.
 *
 * While synchronous processing can cause performance bottlenecks (each
 * caller waits for a transaction answer before calling another
 * transaction), the VoltDB cluster at large is still able to perform at
 * blazing speeds when many clients are connected to it.
 */

package reviewer.native_api;

import common.BookReviewsGenerator;
import common.Constants;
import common.DBConnection;
import common.ReviewerConfig;
import org.voltdb.client.ClientResponse;
import reviewer.Benchmark;
import util.StdOut;

public class SyncBenchmark extends NativeAPIBenchmark {

    public SyncBenchmark(ReviewerConfig config) {
        super(config);
    }

    /**
     * While <code>benchmarkComplete</code> is set to false, run as many
     * synchronous procedure calls as possible and record the results.
     */
    class ReviewerThread implements Runnable {

        @Override
        public void run() {
            while (warmupComplete.get() == false) {
                // Get the next review
                BookReviewsGenerator.Review call = reviewsGenerator.receive();

                // synchronously call the "Review" procedure
                try {
                    client.callProcedure("Review", call.email, call.review,
                            call.bookId, config.maxreviews);
                } catch (Exception e) {
                }
            }

            while (benchmarkComplete.get() == false) {
                // Get the next review
                BookReviewsGenerator.Review call = reviewsGenerator.receive();

                // synchronously call the "Review" procedure
                try {
                    ClientResponse response = client.callProcedure("Review",
                            call.email, call.review,
                            call.bookId,
                            config.maxreviews);

                    long resultCode = response.getResults()[0].asScalarLong();
                    stats.updateResults(resultCode);
                } catch (Exception e) {
                    stats.incrementFailedReviews();
                }
            }
        }
    }

    /**
     * Core benchmark code.
     * Connect. Initialize. Run the loop. Cleanup. Print Results.
     *
     * @throws Exception if anything unexpected happens.
     */
    public void runBenchmark() throws Exception {
        StdOut.print(Constants.HORIZONTAL_RULE);
        StdOut.println(" Setup & Initialization");
        StdOut.println(Constants.HORIZONTAL_RULE);

        // connect to one or more servers, loop until success
        DBConnection.connect(config.servers, client);

        // initialize using synchronous call
        StdOut.println("\nPopulating Static Tables\n");
        client.callProcedure("Initialize", config.books, Constants.BOOK_NAMES_CSV);

        StdOut.print(Constants.HORIZONTAL_RULE);
        StdOut.println(" Starting Benchmark");
        StdOut.println(Constants.HORIZONTAL_RULE);

        // create/start the requested number of threads
        Thread[] reviewrThreads = new Thread[config.threads];
        for (int i = 0; i < config.threads; ++i) {
            reviewrThreads[i] = new Thread(new ReviewerThread());
            reviewrThreads[i].start();
        }

        // Run the benchmark loop for the requested warmup time
        StdOut.println("Warming up...");
        Thread.sleep(1000l * config.warmup);

        // signal to threads to end the warmup phase
        warmupComplete.set(true);

        // reset the stats after warmup
        fullStatsContext.fetchAndResetBaseline();
        periodicStatsContext.fetchAndResetBaseline();

        // print periodic statistics to the console
        stats.setStartTS(System.currentTimeMillis());
        schedulePeriodicStats();

        // Run the benchmark loop for the requested warmup time
        StdOut.println("\nRunning benchmark...");
        Thread.sleep(1000l * config.duration);

        // stop the threads
        benchmarkComplete.set(true);

        // cancel periodic stats printing
        timer.cancel();

        // block until all outstanding txns return
        client.drain();

        // join on the threads
        for (Thread t : reviewrThreads) {
            t.join();
        }

        // print the summary results
        printResults();

        // close down the client connections
        client.close();
    }

    /**
     * Main routine creates a benchmark instance and kicks off the run method.
     *
     * @param args Command line arguments.
     * @throws Exception if anything goes wrong.
     * @see {@link ReviewerConfig}
     */
    public static void main(String[] args) throws Exception {
        // create a configuration from the arguments
        ReviewerConfig config = new ReviewerConfig();
        config.parse(SyncBenchmark.class.getName(), args);

        Benchmark benchmark = new SyncBenchmark(config);
        benchmark.runBenchmark();
    }
}
