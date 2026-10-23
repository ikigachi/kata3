package software.ulpgc.katas.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.ulpgc.katas.model.User;
import software.ulpgc.katas.ports.UserReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RandomUserReader implements UserReader {
    @Override
    public List<User> read(int n) {
        try {
            return readFrom(new URI("https://randomuser.me/api?results=" + n));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> readFrom(URI uri) throws IOException, InterruptedException {
        return readFrom(createRequestFor(uri));
    }

    private List<User> readFrom(HttpRequest request) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return readFrom(client.send(request, HttpResponse.BodyHandlers.ofString()));
        }
    }

    private List<User> readFrom(HttpResponse<String> response) {
        return readFrom(response.body());
    }

    private List<User> readFrom(String json) {
        return readFrom(new Gson().fromJson(json, JsonObject.class).get("results").getAsJsonArray());
    }

    private List<User> readFrom(JsonArray results) {
        List<User> list = new ArrayList<>();
        for (JsonElement result : results) {
            list.add(toUser(new Gson().fromJson(result, UserDto.class)));
        }
        return list;
    }

    private User toUser(UserDto userDto) {
        return new User(userDto.name(), userDto.birthday(), toByteArray(userDto.picture.large));
    }

    private byte[] toByteArray(String url) {
        try {
            try (InputStream inputStream = new URL(url).openStream()) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest createRequestFor(URI uri) {
        return HttpRequest.newBuilder().GET().uri(uri).build();
    }
}
