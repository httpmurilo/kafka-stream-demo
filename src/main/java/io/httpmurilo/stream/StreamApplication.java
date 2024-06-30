package io.httpmurilo.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication

public class StreamApplication {

	private static final Logger logger = LoggerFactory.getLogger(StreamApplication.class);


	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-example");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		KafkaStreams streams = getKafkaStreams(props);

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));


		SpringApplication.run(StreamApplication.class, args);
	}

	private static KafkaStreams getKafkaStreams(Properties props) {
		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, String> sourceStream = builder.stream("teste-entrada");


		KStream<String, String> processedStream = sourceStream.mapValues(value -> {

			logger.debug("Recebido objeto: {}", value);
			String upperCaseValue = value.toUpperCase();

			logger.info("Processed value: {}", upperCaseValue);
			return upperCaseValue;
		});


		processedStream.to("teste-saida");

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
		return streams;
	}

}
