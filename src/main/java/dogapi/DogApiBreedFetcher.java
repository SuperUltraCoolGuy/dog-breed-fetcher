package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://dog.ceo/api";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = String.format("%s/breed/%s/list", API_URL, breed.toLowerCase());

        final Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            String status = responseBody.getString("status");
            if (!"success".equals(status)) {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }
            List<String> arrayList = new ArrayList<>();
            JSONArray array = responseBody.getJSONArray("message");
            for (int i = 0; i < array.length(); i++) {
                String s = array.getString(i);
                arrayList.add(s);
            }
            return arrayList;

        } catch (IOException | BreedNotFoundException e) {
            throw new BreedNotFoundException("Failed!");
        }
    }
}