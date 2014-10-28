/*
 * This samples uses the native asynchronous request processing protocol
 * to post requests to the VoltDB server, thus leveraging to the maximum
 * VoltDB's ability to run requests in parallel on multiple database
 * partitions, and multiple servers.
 *
 * While asynchronous processing is (marginally) more convoluted to work
 * with and not adapted to all workloads, it is the preferred interaction
 * model to VoltDB as it allows a single client with a small amount of
 * threads to flood VoltDB with requests, guaranteeing blazing throughput
 * performance.
 *
 * Note that this benchmark focuses on throughput performance and
 * not low latency performance.  This benchmark will likely 'firehose'
 * the database cluster (if the cluster is too slow or has too few CPUs)
 * and as a result, queue a significant amount of requests on the server
 * to maximize throughput measurement. To test VoltDB latency, run the
 * SyncBenchmark client, also found in the reviewer sample directory.
 */

package reviewer.native_api;

import common.BookReviewsGenerator;
import common.Constants;
import common.DBConnection;
import common.ReviewerConfig;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NullCallback;
import org.voltdb.client.ProcedureCallback;
import reviewer.Benchmark;
import util.StdOut;

public class AsyncBenchmark extends NativeAPIBenchmark {

    /**
     * Constructor for benchmark instance.
     * Configures VoltDB client and prints configuration.
     *
     * @param config Parsed & validated CLI options.
     */
    public AsyncBenchmark(ReviewerConfig config) {
        super(config);
    }

    /**
     * Callback to handle the response to a stored procedure call.
     * Tracks response types.
     */
    class ReviewerCallback implements ProcedureCallback {
        @Override
        public void clientCallback(ClientResponse response) throws Exception {
            if (response.getStatus() == ClientResponse.SUCCESS) {
                long resultCode = response.getResults()[0].asScalarLong();
                stats.updateResults(resultCode);
            } else {
                stats.incrementFailedReviews();
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

        // Run the benchmark loop for the requested warmup time
        // The throughput may be throttled depending on client configuration
        StdOut.println("Warming up...");
        final long warmupEndTime = System.currentTimeMillis() + (1000l * config.warmup);
        while (warmupEndTime > System.currentTimeMillis()) {
            // Get the next review
            BookReviewsGenerator.Review call = reviewsGenerator.receive();

            // asynchronously call the "Review" procedure
            client.callProcedure(new NullCallback(),
                    "Review",
                    call.email, call.review,
                    call.bookId,
                    config.maxreviews);
        }

        // reset the stats after warmup
        fullStatsContext.fetchAndResetBaseline();
        periodicStatsContext.fetchAndResetBaseline();

        // print periodic statistics to the console
        stats.setStartTS(System.currentTimeMillis());
        schedulePeriodicStats();


        // Run the benchmark loop for the requested duration
        // The throughput may be throttled depending on client configuration
        StdOut.println("\nRunning benchmark...");
        final long benchmarkEndTime = System.currentTimeMillis() + (1000l * config.duration);
        while (benchmarkEndTime > System.currentTimeMillis()) {
            // Get the next review
            BookReviewsGenerator.Review call = reviewsGenerator.receive();

            // asynchronously call the "Review" procedure
            client.callProcedure(new ReviewerCallback(),
                    "Review",
                    call.email, call.review,
                    call.bookId,
                    config.maxreviews);
        }

        // stop the threads
        benchmarkComplete.set(true);

        // cancel periodic stats printing
        timer.cancel();

        // block until all outstanding txns return
        client.drain();

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
        config.parse(AsyncBenchmark.class.getName(), args);

        Benchmark benchmark = new AsyncBenchmark(config);
        benchmark.runBenchmark();
    }
}
