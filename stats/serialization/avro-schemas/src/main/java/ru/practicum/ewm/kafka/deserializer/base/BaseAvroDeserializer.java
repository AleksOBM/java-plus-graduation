package ru.practicum.ewm.kafka.deserializer.base;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import javax.annotation.Nonnull;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

	private final DecoderFactory decoderFactory;
	private final DatumReader<T> reader;

	public BaseAvroDeserializer(Schema schema) {
		this(DecoderFactory.get(), schema);
	}

	public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
		this.decoderFactory = decoderFactory;
		this.reader = new SpecificDatumReader<>(schema);
	}

	@Override
	public T deserialize(String topic, @Nonnull byte[] data) {
		try {
			BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
			return reader.read(null, decoder);

		} catch (Exception e) {
			throw new DeserializationException("Ошибка десериализации данных из топика [" + topic + "]", e);
		}
	}
}