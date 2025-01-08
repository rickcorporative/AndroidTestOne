/*
 * Copyright 2024 Volt Active Data Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.example;

import java.util.concurrent.ThreadLocalRandom;
import org.voltdb.stream.api.ExecutionContext;
import org.voltdb.stream.api.Sinks;
import org.voltdb.stream.api.Sources;
import org.voltdb.stream.api.pipeline.VoltPipeline;
import org.voltdb.stream.api.pipeline.VoltStreamBuilder;

/**
 * Definition of a pipeline that generates random strings and sends them to Kafka.
 */
public class RandomToConsolePipeline implements VoltPipeline {

    @Override
    public void define(VoltStreamBuilder stream) {
        // Get the configuration value from the environment.
        ExecutionContext.ConfigurationContext configurator = stream.getExecutionContext().configurator();
        int tps = configurator.findByPath("tps").asInt();

        stream
                // Optionally name the stream
                .withName("Random Generator to Console")
                // Configure load driving source
                .consumeFromSource(
                        Sources.generateAtRate(
                                tps,
                                () -> "Hello Earth " + ThreadLocalRandom.current().nextInt(1000) + "!"
                        )
                )
                // Do some processing on the data
                .processWith(
                        string -> string.toUpperCase()
                )
                // Configure sink to print generated data to the console
                .terminateWithSink(Sinks.stdout());
    }
}
