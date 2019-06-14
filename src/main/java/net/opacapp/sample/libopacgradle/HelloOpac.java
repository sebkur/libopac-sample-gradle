package net.opacapp.sample.libopacgradle;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;

import de.geeksfactory.opacclient.OpacApiFactory;
import de.geeksfactory.opacclient.apis.OpacApi;
import de.geeksfactory.opacclient.objects.DetailedItem;
import de.geeksfactory.opacclient.objects.Library;
import de.geeksfactory.opacclient.objects.SearchRequestResult;
import de.geeksfactory.opacclient.objects.SearchResult;
import de.geeksfactory.opacclient.searchfields.SearchField;
import de.geeksfactory.opacclient.searchfields.SearchQuery;

public class HelloOpac
{

	public static String LIBRARY_NAME = "Bremen";

	public static void main(final String[] args)
			throws JSONException, OpacApi.OpacErrorException, IOException
	{
		Security.addProvider(new BouncyCastleProvider());

		System.out.println("Hello OPAC!");

		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config.json");
		String config = IOUtils.toString(input);

		// Create a library object
		Library library = Library.fromJSON(LIBRARY_NAME,
				new JSONObject(config));

		// Instantiate the appropriate API class
		OpacApi api = OpacApiFactory.create(library, "HelloOpac/1.0.0");

		System.out.println("Obtaining search fields...");
		List<SearchField> searchFields = api.getSearchFields();
		System.out.println(
				String.format("Found %d search fields", searchFields.size()));
		for (SearchField searchField : searchFields) {
			System.out.println(String.format("Search field: %s",
					searchField.getDisplayName()));
		}

		SearchField searchField = searchFields.get(0);
		System.out.println(String.format("Using search field: %s",
				searchField.getDisplayName()));

		String needle = "Hello";
		List<SearchQuery> query = new ArrayList<>();
		query.add(new SearchQuery(searchField, needle));
		System.out.println(
				String.format("Searching for '%s' in this field...", needle));

		SearchRequestResult searchRequestResult = api.search(query);
		int n = searchRequestResult.getTotal_result_count();
		System.out.println(String.format("Found %d matches.",
				+searchRequestResult.getTotal_result_count()));

		List<SearchResult> results = searchRequestResult.getResults();
		System.out.println(String.format("Got %d results", results.size()));

		for (int i = 0; i < results.size(); i++) {
			try {
				System.out.println("Match: "
						+ searchRequestResult.getResults().get(i).toString());
				SearchResult result = searchRequestResult.getResults().get(i);
				DetailedItem detailedItem = api.getResultById(result.getId(),
						null);
				System.out.println("Got details: " + detailedItem.toString());
			} catch (UnsupportedOperationException e) {
				System.out.println("unsupported operation");
			}
		}
	}

}
