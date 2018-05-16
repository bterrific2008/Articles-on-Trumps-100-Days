package nyt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NYT_search {

	private String[] startingDates;
	private String[] endingDates;
	private String[] fields;
	private String apiKey;
	private String query;

	public NYT_search(String api_key, String query, String begin_date, String end_date, String... fields){
		this.apiKey = api_key;
		this.query = query;
		this.loadDates(Integer.parseInt(begin_date.substring(0,4)), Integer.parseInt(begin_date.substring(4,6)), Integer.parseInt(begin_date.substring(6,8)), 
				Integer.parseInt(end_date.substring(0,4)), Integer.parseInt(end_date.substring(4,6)), Integer.parseInt(end_date.substring(6,8)));
		this.fields = fields;
	}


	public void startSearch() throws JSONException, IOException, InterruptedException{

		File file = new File("nyt"+query+".txt");
		File csvFile = new File("nyt"+query+".csv");

		BufferedWriter bfWriter = new BufferedWriter(new FileWriter(file));
		BufferedWriter csvBfWriter = new BufferedWriter(new FileWriter(csvFile));

		PrintWriter fileWriter = new PrintWriter(bfWriter);
		PrintWriter csvWriter = new PrintWriter(csvBfWriter);

		csvWriter.println("Year,Month,Day,Time,End Year,End Month,End Day,End Time,Display Date,Headline,Text,Media,Media Credit,Media Caption,Media Thumbnail,Type,Group,Background");
		csvWriter.println(",,,,,,,,,NYT Articles after a search for "+query+",A collection of NYT Articles written about "+query+".,http://static01.nytimes.com/packages/images/developer/logos/poweredby_nytimes_200a.png,New York Times Developers,\"<a href=\"\"http://developer.nytimes.com\"\" title=\"\"The New York Times Developers\"\">The New York Times Developers</a>\",,title,,");

		int page;
		for(int dateCount = 0; dateCount<startingDates.length; dateCount++){
			page = 1;

			JSONObject root = loadWebsite(startingDates[dateCount],endingDates[dateCount],page);
			TimeUnit.SECONDS.sleep(3);
			JSONObject response = root.getJSONObject("response");
			JSONArray docs = response.getJSONArray("docs");

			for(int docCount = 0; docCount < docs.length(); docCount++){
				JSONObject doc = docs.getJSONObject(docCount);

				JSONObject headline = doc.getJSONObject("headline");
				String pub_date = doc.getString("pub_date");
				String content = doc.getString("snippet");
				if(doc.has("abstract")){
					content = doc.getString("abstract");
				}

				if(doc.has("type_of_material") && !doc.get("type_of_material").equals(null)){
					if(doc.getString("type_of_material").equals("News")){
						fileWriter.println(headline.getString("main"));
						fileWriter.println(doc.getString("pub_date"));
						fileWriter.println(content);
						fileWriter.println(doc.getString("web_url"));
						fileWriter.println();

						String[] pub_dates = pub_date.split("-");

						csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+",");
						csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+","+",");
						csvWriter.print(headline.getString("main")+",");
						csvWriter.print("\""+content.trim()+"\"");
						csvWriter.print(" Found at: "+doc.getString("web_url")+",,,,,,,\n");
					}
				}
				fileWriter.flush();
				csvWriter.flush();
			}
			System.out.println(page);

			while(docs.length()>=10 && page<10){
				page++;
				root = loadWebsite(startingDates[dateCount],endingDates[dateCount],page);
				TimeUnit.SECONDS.sleep(3);
				response = root.getJSONObject("response");
				docs = response.getJSONArray("docs");

				for(int docCount = 0; docCount < docs.length(); docCount++){
					JSONObject doc = docs.getJSONObject(docCount);

					JSONObject headline = doc.getJSONObject("headline");
					String pub_date = doc.getString("pub_date");
					String content = doc.getString("snippet");
					if(doc.has("abstract")){
						content = doc.getString("abstract");
					}

					if(doc.has("type_of_material") && !doc.get("type_of_material").equals(null)){
						if(doc.getString("type_of_material").equals("News")){
							fileWriter.println(headline.getString("main"));
							fileWriter.println(doc.getString("pub_date"));
							fileWriter.println(content);
							fileWriter.println(doc.getString("web_url"));
							fileWriter.println();

							String[] pub_dates = pub_date.split("-");

							csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+",");
							csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+","+",");
							csvWriter.print(headline.getString("main")+",");
							csvWriter.print("\""+content.trim()+"\"");
							csvWriter.print(" Found at: "+doc.getString("web_url")+",,,,,,,\n");
						}
					}
					
					fileWriter.flush();
					csvWriter.flush();
				}
				System.out.println(page);
			}

			fileWriter.flush();
			csvWriter.flush();

			fileWriter.close();
			csvWriter.close();

		}

	}


	// loadWebsite
	// pre=Start Date of search, End Date of search, which Page;
	// post= Loads a JSONObject from an online JSON Object
	public JSONObject loadWebsite(String startDate, String endDate, int page){

		URL url = null;

		String urlAddress = "http://api.nytimes.com/svc/search/v2/articlesearch.json?"+
		"q="+query+"&page="+page+"&begin_date="+startDate+"&end_date="+endDate+"&fl=";
		for(int i = 0; i<fields.length; i++){
			urlAddress+=fields[i];
			if(i<fields.length-1)
				urlAddress+=",";
		}

		try {
			url= new URL(urlAddress+"&api-key="+apiKey);
		} catch (MalformedURLException e) {
			System.err.println("Error: URL invalid\n"+urlAddress);
			return null;
		}

		BufferedReader reader = null;
		StringBuilder docBuild = new StringBuilder();
		String line = "";

		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			while((line=reader.readLine())!=null)
				docBuild.append(line);

			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println(url.toString());
			e.printStackTrace();
			return null;
		} 

		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(docBuild.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}


	// dates
	// pre= takes in a starting and ending date in the format %y%m%d
	// post= instantiates startDate[] and endDate[] arrays
	public void loadDates(int year1, int month1, int day1, int year2, int month2, int day2){

		int monthsBetween = this.monthsBetweenDates(year1, month1, day1, year2, month2, day2);

		Calendar calendar= Calendar.getInstance();
		calendar.set(Calendar.YEAR, year1);
		calendar.set(Calendar.MONTH, month1-1);
		calendar.set(Calendar.DAY_OF_MONTH,day1);


		startingDates = new String[monthsBetween];
		endingDates = new String[monthsBetween];

		int year, month, day;
		for(int i = 0; i< monthsBetween-1; i++){

			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);

			startingDates[i] = String.format("%02d%02d%02d", year, month+1, day);

			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			endingDates[i] = String.format("%02d%02d%02d", year, month+1, day);
			calendar.add(Calendar.DAY_OF_YEAR, 1);

		}

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		startingDates[monthsBetween-1] = String.format("%02d%02d%02d", year, month+1, day);

		calendar.set(Calendar.DAY_OF_MONTH, day2);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		endingDates[monthsBetween-1] = String.format("%02d%02d%02d", year, month+1, day);

	}

	public int monthsBetweenDates(int year1, int month1, int day1, int year2, int month2, int day2){

		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, year1);
		start.set(Calendar.MONTH, month1-1);
		start.set(Calendar.DAY_OF_MONTH,day1);


		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, year2);
		end.set(Calendar.MONTH, month2-1);
		end.set(Calendar.DAY_OF_MONTH,day2);


		int monthsBetween = 0;
		int dateDiff = end.get(Calendar.DAY_OF_MONTH)-start.get(Calendar.DAY_OF_MONTH);      

		if(dateDiff<0) {
			int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);           
			dateDiff = (end.get(Calendar.DAY_OF_MONTH)+borrrow)-start.get(Calendar.DAY_OF_MONTH);
			monthsBetween--;

			if(dateDiff>0) {
				monthsBetween++;
			}
		}
		else {
			monthsBetween++;
		}      
		monthsBetween += end.get(Calendar.MONTH)-start.get(Calendar.MONTH);      
		monthsBetween  += (end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*12;      
		return monthsBetween;
	}

}
