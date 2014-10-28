/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Sandeep Kunkunuru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reviewer.native_api;

import common.ReviewerConfig;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ProcCallException;
import reviewer.Benchmark;
import util.StdOut;

import java.io.IOException;

/**
 * Created by sandeep on 8/4/14.
 */
public abstract class NativeAPIBenchmark extends Benchmark {
    // Reference to the database connection we will use
    public Client client;

    public NativeAPIBenchmark(ReviewerConfig config) {
        super(config);

        ClientConfig clientConfig = new ClientConfig(config.user, config.password, new StatusListener());
        clientConfig.setMaxTransactionsPerSecond(config.ratelimit);

        this.client = ClientFactory.createClient(clientConfig);

        periodicStatsContext = client.createStatsContext();
        fullStatsContext = client.createStatsContext();
    }


    public void getWinner() throws IOException, ProcCallException {
        // 2. results
        VoltTable result = client.callProcedure("Results").getResults()[0];

        StdOut.println("Book Name\t\tReviews Received");
        while (result.advanceRow()) {
            StdOut.printf("%s\t\t%,14d\n", result.getString(0), result.getLong(2));
        }
        StdOut.printf("\nThe Winner is: %s\n\n", result.fetchRow(0).getString(0));
    }

    @Override
    protected void getSummaryCSV() throws IOException {
        // 4. Write stats to file if requested
        if (!"".equals(config.statsfile.trim()))
            (client).writeSummaryCSV(fullStatsContext.fetch().getStats(), config.statsfile);
    }

}
