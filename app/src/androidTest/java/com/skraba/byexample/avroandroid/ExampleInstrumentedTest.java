package com.skraba.byexample.avroandroid;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.skraba.byexample.avroandroid.generated.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.skraba.byexample.avroandroid", appContext.getPackageName());

        User user = new User("David", 1, "blue");
        User.getClassSchema().addProp("android", "check");
        ByteBuffer encoded = User.getEncoder().encode(user);
        assertThat(encoded.remaining(), is(24));
    }
}