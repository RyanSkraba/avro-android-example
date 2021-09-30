package com.skraba.byexample.avroandroid;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.skraba.avro.enchiridion.recipe.Bake;

import com.skraba.avro.enchiridion.recipe.Ingredient;
import com.skraba.avro.enchiridion.recipe.Recipe;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.internal.ThreadLocalWithInitial;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Instrumented test, of Avro APIs, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class AvroTest {

    private static Recipe puffedWheatSquares = null;

    private Recipe getPuffedWheatSquares() {
        if (puffedWheatSquares == null) {
            puffedWheatSquares =
                    Recipe.newBuilder()
                            .setTitle("Chocolate Puffed Wheat Squares")
                            .setIngredients(
                                    Arrays.asList(
                                            new Ingredient(
                                                    "2L | 8 cups | 120g",
                                                    "puffed wheat cereal",
                                                    Collections.emptyList(),
                                                    Collections.emptyList()),
                                            Ingredient.newBuilder()
                                                    .setQ("75 mL | 0.33 cups | 75g")
                                                    .setN("butter | margarine")
                                                    .build(),
                                            new Ingredient(
                                                    "60 mL | 0.25 cups | 50g",
                                                    "packed brown sugar",
                                                    Collections.emptyList(),
                                                    Collections.emptyList()),
                                            new Ingredient(
                                                    "125 mL | 0.5 cups | 120g",
                                                    "corn syrup",
                                                    Collections.emptyList(),
                                                    Collections.emptyList()),
                                            new Ingredient(
                                                    "45 mL | 3 tbsp",
                                                    "unsweetened cocoa powder",
                                                    Collections.emptyList(),
                                                    Collections.emptyList()),
                                            new Ingredient(
                                                    "5 mL | 1 tsp",
                                                    "vanilla extract",
                                                    Collections.emptyList(),
                                                    Collections.emptyList())))
                            .setTodo(
                                    Arrays.asList(
                                            "Combine all ingredients but puffed wheat in a saucepan.",
                                            "Stir continually over medium heat until boils.",
                                            "Boil for 1 minute, then remove from heat.",
                                            "Press into buttered 23x23cm pan.",
                                            "Pour mixture over puffed wheat and mix well.",
                                            "Cool and cut into squares."))
                            .build();
        }
        return puffedWheatSquares;
    }

    @Test
    public void testSimpleBuilder() {
        Recipe r = getPuffedWheatSquares();

        // This should be generated as a test resource.
        assertThat(r, instanceOf(GenericRecord.class));
    }

    @Test
    public void testBinaryEncoding() throws IOException {

        final byte[] serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            DatumWriter<Recipe> w = new SpecificDatumWriter<>(Recipe.class);
            w.write(getPuffedWheatSquares(), encoder);
            encoder.flush();
            serialized = baos.toByteArray();
        }

        assertThat(serialized.length, is(547));

        final Recipe roundTrip;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized)) {
            Decoder decoder = DecoderFactory.get().binaryDecoder(bais, null);
            DatumReader<Recipe> r = new SpecificDatumReader<>(Recipe.class);
            roundTrip = r.read(null, decoder);
        }

        assertThat(roundTrip, is(getPuffedWheatSquares()));
    }

    @Test
    public void testJsonEncoding() throws IOException {

        final String serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().jsonEncoder(Recipe.getClassSchema(), baos);
            DatumWriter<Recipe> w = new SpecificDatumWriter<>(Recipe.class);
            w.write(getPuffedWheatSquares(), encoder);
            encoder.flush();
            serialized = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }

        assertThat(serialized.length(), is(1011));

        final Recipe roundTrip;
        try (ByteArrayInputStream bais =
                     new ByteArrayInputStream(serialized.getBytes(StandardCharsets.UTF_8))) {
            Decoder decoder = DecoderFactory.get().jsonDecoder(Recipe.getClassSchema(), bais);
            DatumReader<Recipe> r = new SpecificDatumReader<>(Recipe.class);
            roundTrip = r.read(null, decoder);
        }

        assertThat(roundTrip, is(getPuffedWheatSquares()));
    }

    @Test
    public void testMessageEncoding() throws IOException {
        ByteBuffer serialized = Recipe.getEncoder().encode(getPuffedWheatSquares());
        assertThat(serialized.remaining(), is(557));

        Recipe roundTrip = Recipe.fromByteBuffer(serialized);
        assertThat(roundTrip, is(getPuffedWheatSquares()));
    }

    @Test
    public void testJsonProperties() throws IOException {
        // Uses MapEntry, fails with 1.10.2
        Bake.getClassSchema().addProp("android", "check");
    }

    @Test
    public void testThreadLocal() throws IOException {
        // Uses ThreadLocal.withInitial, added in API 26
        String hi = ThreadLocalWithInitial.of(() -> "Hello").get();
        assertThat(hi, is("Hello"));
    }
}