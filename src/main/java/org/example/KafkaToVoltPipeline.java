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

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.voltdb.stream.api.ExecutionContext;
import org.voltdb.stream.api.Sinks;
import org.voltdb.stream.api.Sources;
import org.voltdb.stream.api.kafka.KafkaStartingOffset;
import org.voltdb.stream.api.pipeline.VoltPipeline;
import org.voltdb.stream.api.pipeline.VoltStreamBuilder;

/**
 * Definition of a pipeline that reads strings from Kafka and inserts them into a Volt table via
 * procedure call.
 */
public class KafkaToVoltPipeline implements VoltPipeline {

    @Override
    public void define(VoltStreamBuilder stream) {
        // Get the configuration value from the environment.
        ExecutionContext.ConfigurationContext configurator = stream.getExecutionContext().configurator();
        String voltdbCluster = configurator.findByPath("sink.voltdb.cluster").asString();

        stream
                // Optionally name the stream
                .withName("Kafka to Volt Stream")
                // Configure Kafka source
                .consumeFromSource(
                        Sources.kafka()
                               .withStartingOffset(KafkaStartingOffset.EARLIEST)
                               .withKeyDeserializer(IntegerDeserializer.class)
                               .withValueDeserializer(StringDeserializer.class)
                )
                // Transform message from Kafka
                .processWith(request -> new Object[]{request.getValue()})
                // Send data to VoltDB
                .terminateWithSink(
                        Sinks.volt().procedureCall()
                             .withHostAndStandardPort(voltdbCluster)
                             .withProcedureName("GREETINGS.insert")
                );
    }
}
