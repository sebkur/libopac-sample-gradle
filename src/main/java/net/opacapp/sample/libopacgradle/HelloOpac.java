package net.opacapp.sample.libopacgradle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import de.geeksfactory.opacclient.OpacApiFactory;
import de.geeksfactory.opacclient.apis.OpacApi;
import de.geeksfactory.opacclient.objects.DetailedItem;
import de.geeksfactory.opacclient.objects.Library;
import de.geeksfactory.opacclient.objects.SearchRequestResult;
import de.geeksfactory.opacclient.searchfields.SearchField;
import de.geeksfactory.opacclient.searchfields.SearchQuery;

public class HelloOpac
{

	public static String LIBRARY_NAME = "Bremen";

	public static void main(final String[] args)
			throws JSONException, OpacApi.OpacErrorException, IOException
	{
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
		System.out.println("Found a first search field: "
				+ searchFields.get(0).getDisplayName());

		List<SearchQuery> query = new ArrayList<>();
		query.add(new SearchQuery(searchFields.get(0), "Hello"));
		System.out.println("Searching for 'hello' in this field...");

		SearchRequestResult searchRequestResult = api.search(query);
		System.out.println("Found "
				+ searchRequestResult.getTotal_result_count() + " matches.");
		System.out.println("First match: "
				+ searchRequestResult.getResults().get(0).toString());

		System.out.println("Fetching details for the first result...");
		DetailedItem detailedItem = api.getResult(0);
		System.out.println("Got details: " + detailedItem.toString());
	}

}
