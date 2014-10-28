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

package common;

import org.voltdb.CLIConfig;

/**
 * Uses included {@link org.voltdb.CLIConfig} class to
 * declaratively state command line options with defaults
 * and validation.
 */
public class ReviewerConfig extends CLIConfig {
    @Option(desc = "Interval for performance feedback, in seconds.")
    public long displayinterval = 5;

    @Option(desc = "Benchmark duration, in seconds.")
    public int duration = 120;

    @Option(desc = "Warmup duration in seconds.")
    public int warmup = 5;

    @Option(desc = "Comma separated list of the form server[:port] to connect to.")
    public String servers = "localhost";

    @Option(desc = "Number of books in the reviewing time window (from 1 to 10).")
    public int books = 6;

    @Option(desc = "Maximum number of reviews cast per reviewer.")
    public int maxreviews = 2;

    @Option(desc = "Filename to write raw summary statistics to.")
    public String statsfile = "";

    @Option(desc = "Number of concurrent threads synchronously calling procedures.")
    public int threads = 40;

    @Option(desc = "User name for connection.")
    public String user = "";

    @Option(desc = "Password for connection.")
    public String password = "";

    @Option(desc = "Maximum TPS rate for benchmark.")
    public int ratelimit = Integer.MAX_VALUE;

    @Option(desc = "Report latency for async benchmark run.")
    public boolean latencyreport = false;

    @Override
    public void validate() {
        if (duration <= 0) exitWithMessageAndUsage("duration must be > 0");
        if (warmup < 0) exitWithMessageAndUsage("warmup must be >= 0");
        if (duration < 0) exitWithMessageAndUsage("warmup must be >= 0");
        if (displayinterval <= 0) exitWithMessageAndUsage("displayinterval must be > 0");
        if (books <= 0) exitWithMessageAndUsage("books must be > 0");
        if (maxreviews <= 0) exitWithMessageAndUsage("maxreviews must be > 0");
        if (threads <= 0) exitWithMessageAndUsage("threads must be > 0");
    }
}
