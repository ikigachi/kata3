package software.ulpgc.katas.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class JsonWriter {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void write(VideogameExport data, OutputStream output) throws IOException {
        Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
        gson.toJson(data, writer);
        writer.flush();
    }
}
