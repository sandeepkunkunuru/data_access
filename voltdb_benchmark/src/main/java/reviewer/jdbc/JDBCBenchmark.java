/*
 * This samples uses multiple threads to post synchronous requests to the
 * VoltDB server, simulating multiple client application posting
 * synchronous requests to the database, using the VoltDB JDBC client
 * library.
 *
 * While synchronous processing can cause performance bottlenecks (each
 * caller waits for a transaction answer before calling another
 * transaction), the VoltDB cluster at large is still able to perform at
 * blazing speeds when many clients are connected to it.
 */

package reviewer.jdbc;

import common.DBConnection;
import org.voltdb.client.ProcCallException;
import org.voltdb.jdbc.IVoltDBConnection;
import reviewer.Benchmark;
import common.BookReviewsGenerator;
import common.Constants;
import common.ReviewerConfig;
import util.StdOut;

import java.io.IOException;
import java.sql.*;

public class JDBCBenchmark extends Benchmark {
    // Reference to the database connection we will use
    Connection connection;

    /**
     * Constructor for benchmark instance. Configures VoltDB connection and prints
     * configuration.
     *
     * @param config Parsed & validated CLI options.
     */
    public JDBCBenchmark(ReviewerConfig config) {
        super(config);
    }

    /**
     * Connect to a set of servers in parallel. Each will retry until
     * connection. This call will block until all have connected.
     *
     * @param servers A comma separated list of servers using the hostname:port
     *                syntax (where :port is optional).
     * @throws InterruptedException   if anything bad happens with the threads.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    void connect(String servers) throws InterruptedException,
            ClassNotFoundException, SQLException {
        connection = DBConnection.getJDBCConnection(servers);

        periodicStatsContext = ((IVoltDBConnection) connection).createStatsContext();
        fullStatsContext = ((IVoltDBConnection) connection).createStatsContext();
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
                    final PreparedStatement reviewCS = connection
                            .prepareCall("{call Review(?,?,?,?)}");
                    reviewCS.setString(1, call.email);
                    reviewCS.setString(2, call.review);
                    reviewCS.setInt(3, call.bookId);
                    reviewCS.setLong(4, config.maxreviews);
                } catch (Exception e) {
                }
            }

            while (benchmarkComplete.get() == false) {
                // Get the next review
                BookReviewsGenerator.Review call = reviewsGenerator.receive();

                // synchronously call the "Review" procedure
                try {

                    final PreparedStatement reviewCS = connection
                            .prepareCall("{call Review(?,?,?,?)}");
                    reviewCS.setString(1, call.email);
                    reviewCS.setString(2, call.review);
                    reviewCS.setInt(3, call.bookId);
                    reviewCS.setLong(4, config.maxreviews);

                    try {
                        long resultCode = reviewCS.executeUpdate();

                        stats.updateResults(resultCode);
                    } catch (Exception x) {
                        stats.incrementFailedReviews();
                    }
                } catch (Exception e) {
                    stats.incrementFailedReviews();
                }
            }

        }

    }

    /**
     * Core benchmark code. Connect. Initialize. Run the loop. Cleanup. Print
     * Results.
     *
     * @throws Exception if anything unexpected happens.
     */
    public void runBenchmark() throws Exception {
        StdOut.print(Constants.HORIZONTAL_RULE);
        StdOut.println(" Setup & Initialization");
        StdOut.println(Constants.HORIZONTAL_RULE);

        // connect to one or more servers, loop until success
        connect(config.servers);

        // initialize using synchronous call
        // Initialize the application
        StdOut.println("\nPopulating Static Tables\n");
        final PreparedStatement initializeCS = connection
                .prepareCall("{call Initialize(?,?)}");
        initializeCS.setInt(1, config.books);
        initializeCS.setString(2, Constants.BOOK_NAMES_CSV);
        initializeCS.executeUpdate();

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
        // connection.drain();

        // join on the threads
        for (Thread t : reviewrThreads) {
            t.join();
        }

        // print the summary results
        printResults();

        // close down the connection connections
        connection.close();
    }

    @Override
    protected void getSummaryCSV() throws IOException {
        // 4. Write stats to file if requested
        if (!"".equals(config.statsfile.trim()))
            ((IVoltDBConnection)connection).writeSummaryCSV(fullStatsContext.fetch().getStats(), config.statsfile);
    }

    public void getWinner() throws IOException, ProcCallException {
        try {
            final PreparedStatement reviewCS = connection
                    .prepareCall("{call Results()}");

            try {
                ResultSet result = reviewCS.executeQuery();

                StdOut.println("Book Name\t\tReviews Received");
                while (result.next()) {
                    StdOut.printf("%s\t\t%,14d\n", result.getString(0), result.getLong(2));
                }
                StdOut.printf("\nThe Winner is: %s\n\n", result.getString(0));
            } catch (Exception x) {
                x.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        config.parse(Benchmark.class.getName(), args);

        Benchmark benchmark = new JDBCBenchmark(config);
        benchmark.runBenchmark();
    }
}
