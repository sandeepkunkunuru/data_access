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

package models;

import common.Constants;
import util.StdOut;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sandeep on 8/4/14.
 */
public class Stats {
    private AtomicLong invocations = new AtomicLong(0);
    private AtomicLong invalidEntity = new AtomicLong(0);
    private AtomicLong invalid = new AtomicLong(0);
    private AtomicLong accepted = new AtomicLong(0);
    private AtomicLong failed = new AtomicLong(0);
    private long throughput;
    private long aborts;
    private long errors;
    private boolean latencyReport;
    private double latency;
    private double latency_95;
    private long endTS;
    private long startTS;

    public void updateResults(long resultCode) {
        if (resultCode == Constants.ERR_INVALID_BOOK) {
            invalidEntity.incrementAndGet();
        } else if (resultCode == Constants.ERR_REVIEWER_OVER_REVIEW_LIMIT) {
            invalid.incrementAndGet();
        } else {
            assert (resultCode == Constants.REVIEW_SUCCESSFUL);
            accepted.incrementAndGet();
        }

        invocations.incrementAndGet();
    }

    public void incrementFailedReviews() {
        failed.incrementAndGet();
        invocations.incrementAndGet();
    }

    public void printStatistics() {
        long time = getTime();

        StdOut.printf("%02d:%02d:%02d ", time / 3600, (time / 60) % 60, time % 60);
        StdOut.printf("Throughput %d/s, ", throughput);
        StdOut.printf("Aborts/Failures %d/%d", aborts, errors);
        if (latencyReport) {
            StdOut.printf(", Avg/95%% Latency %.2f/%.2fms", latency, latency_95);
        }
        StdOut.printf("\n");
    }

    public void printResults(long invocationsCompleted) {
        // 1. results and performance statistics
        String display = "\n" +
                Constants.HORIZONTAL_RULE +
                " Results\n" +
                Constants.HORIZONTAL_RULE +
                "\nA total of %d reviews were received...\n" +
                " - %,9d Accepted\n" +
                " - %,9d Rejected (Invalid Book)\n" +
                " - %,9d Rejected (Maximum Review Count Reached)\n" +
                " - %,9d Failed (Transaction Error)\n\n";
        StdOut.printf(display, invocationsCompleted,
                accepted.get(), invalidEntity.get(),
                invalid.get(), failed.get());
    }

    public void setStartTS(long startTS) {
        this.startTS = startTS;
    }

    @Override
    public String toString() {
        return new StringBuilder("Results [")
                .append("benchmark start time=").append(startTS).append(", current time=").append(getTime())
                .append(", throughput=").append(throughput).append(", aborts=").append(aborts)
                .append(", errors=").append(errors).append(", avg latency=").append(latency)
                .append(", latency =").append(latency_95).append(", accepted reviews=").append(accepted)
                .append(", bad book reviews=").append(invalidEntity).append(", invocations=").append(invocations)
                .append(", bad review count reviews=").append(invalid).append(", failed reviews=").append(failed)
                .append("]").toString();
    }

    public long getTime() {
        return endTS == 0 ? Math.round((System.currentTimeMillis() - startTS) / 1000.0) :
                Math.round((endTS - startTS) / 1000.0);
    }

    public long getThroughput() {
        return throughput;
    }

    public Stats setThroughput(long throughput) {
        this.throughput = throughput;
        return this;
    }

    public long getAborts() {
        return aborts;
    }

    public Stats setAborts(long aborts) {
        this.aborts = aborts;
        return this;
    }

    public long getErrors() {
        return errors;
    }

    public Stats setErrors(long errors) {
        this.errors = errors;
        return this;
    }

    public double getLatency() {
        return latency;
    }

    public Stats setLatency(double latency) {
        this.latency = latency;
        return this;
    }

    public double getLatency_95() {
        return latency_95;
    }

    public Stats setLatency_95(double latency_95) {
        this.latency_95 = latency_95;
        return this;
    }

    public long getEndTS() {
        return endTS;
    }

    public Stats setEndTS(long endTS) {
        this.endTS = endTS;
        return this;
    }

    public boolean isLatencyReport() {
        return latencyReport;
    }

    public Stats setLatencyReport(boolean latencyReport) {
        this.latencyReport = latencyReport;
        return this;
    }

    public long getInvalidEntity() {
        return invalidEntity.get();
    }

    public void setInvalidEntity(long invalidEntity) {
        this.invalidEntity = new AtomicLong(invalidEntity);
    }

    public long getInvalid() {
        return invalid.get();
    }

    public void setInvalid(long invalid) {
        this.invalid = new AtomicLong(invalid);
    }

    public long getAccepted() {
        return accepted.get();
    }

    public void setAccepted(long accepted) {
        this.accepted = new AtomicLong(accepted);
    }

    public long getFailed() {
        return failed.get();
    }

    public void setFailed(long failed) {
        this.failed = new AtomicLong(failed);
    }

    public long getStartTS() {
        return startTS;
    }

    public long getInvocations() {
        return invocations.get();
    }

    public void setInvocations(long invocations) {
        this.invocations = new AtomicLong(invocations);
    }
}
