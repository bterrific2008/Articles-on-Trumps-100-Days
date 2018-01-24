import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.annotation.processing.Filer;

import org.json.*;

public class JSON_Tests {

	public static void main(String[] args) throws IOException, JSONException, URISyntaxException, InterruptedException {

		String apiKey = API_Keys.NYT_apiKey;

		GettingDates dates = new GettingDates();
		String[] startDate = dates.getStartingDates();
		String[] endDate = dates.getEndingDates();

		File fileWrite = new File("testOutput.txt");
		File csvWriteFile = new File("./testCSV.csv");
		
		BufferedWriter bfwriter = new BufferedWriter(new FileWriter(fileWrite));
		BufferedWriter csvBFwriter = new BufferedWriter(new FileWriter(csvWriteFile));
		PrintWriter writer = new PrintWriter(bfwriter);
		PrintWriter csvWriter = new PrintWriter(csvBFwriter);
		
		writer.println("NYT Test Doc");
		writer.println();
		
		csvWriter.println("Year,Month,Day,Time,End Year,End Month,End Day,End Time,Display Date,Headline,Text,Media,Media Credit,Media Caption,Media Thumbnail,Type,Group,Background");
		csvWriter.println(",,,,,,,,,NYT Articles for 100 Days of Trump,A collection of NYT Articles written during President Trump's first 100 days.,http://storage.torontosun.com/v1/suns-prod-images/1297660878712_LARGE_BOX.jpg?quality=80&stmp=1481008151209,Zach Wise/verite.co,\"<a href=\"\"http://www.flickr.com/photos/zachwise/6115056146/\"\" title=\"\"Chicago by zach.wise, on Flickr\"\">Chicago by zach.wise</a>\",,title,,");
		
		for(int dateCount = 0; dateCount<startDate.length; dateCount++){

//			File fileReader = new File("./testFileNYT");

			URL url= new URL("http://api.nytimes.com/svc/search/v2/articlesearch.json?q=trump&facet_field=source&begin_date="+startDate[dateCount]+"&end_date="+endDate[dateCount]+"&api-key="+apiKey);
			
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
//			BufferedReader reader = new BufferedReader(new FileReader(fileReader));
			StringBuilder docBuild = new StringBuilder();
			while((line=reader.readLine())!=null){
					docBuild.append(line);
			}

			reader.close();

//			JSONTokener tokener = null;
//			
//			try{
//				tokener = new JSONTokener(uri.toURL().openStream());
//			}catch(IOException e){
				
			JSONObject nyt = new JSONObject(docBuild.toString());
			JSONObject response = nyt.getJSONObject("response");
			JSONArray docs = response.getJSONArray("docs");
			
			System.out.println("Number of docs: "+docs.length());
			
			for(int i = 0; i<docs.length(); i++){
				JSONObject doc = docs.getJSONObject(i);

				String snippet = doc.getString("snippet");

				if(doc.has("abstract"))
					snippet = doc.getString("abstract");

				JSONObject headline = doc.getJSONObject("headline");
				String pub_date = doc.getString("pub_date");
				String[] pub_dates = pub_date.split("-");
				
				csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+",");
				csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+","+",");
				csvWriter.print(headline.getString("main")+",");
				
				csvWriter.print("\""+snippet.trim()+"\"");
				
				
				csvWriter.print(" Found at: "+doc.getString("web_url")+",,,,,,,\n");

				
				if(doc.has("type_of_material") && !doc.get("type_of_material").equals(null)){
					if(doc.getString("type_of_material").equals("News")){

						writer.println(headline.getString("main"));
						writer.println(doc.getString("pub_date"));
						writer.println(snippet);
						writer.println(doc.getString("web_url"));
						writer.println();
						
						
					}
				}

			}

			writer.flush();
			TimeUnit.SECONDS.sleep(2);
			System.out.println(dateCount);
		}
		
		csvWriter.flush();
		csvWriter.close();
		
		writer.flush();
		writer.close();

	}
}