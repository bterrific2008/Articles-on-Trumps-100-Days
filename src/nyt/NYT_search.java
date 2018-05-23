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

	private String startingDates[], endingDates[], fields_str[];
	private String apiKey, query;

	public NYT_search(String apiKey, String query, String startDate, String endDate, String[] fields_str){
		this.loadDates(Integer.parseInt(startDate.substring(0, 4)), Integer.parseInt(startDate.substring(4, 6)), Integer.parseInt(startDate.substring(6, 8)), 
				Integer.parseInt(endDate.substring(0, 4)), Integer.parseInt(endDate.substring(4, 6)), Integer.parseInt(endDate.substring(6, 8)));
		this.apiKey = apiKey;
		this.query = query;
		this.fields_str = fields_str;
	}


	public void search() throws JSONException, IOException, InterruptedException{
		System.out.println("confirm");
		File file = new File(query+".txt");
		File csvFile = new File(query+".csv");

		BufferedWriter bfWriter = new BufferedWriter(new FileWriter(file));
		BufferedWriter csvBfWriter = new BufferedWriter(new FileWriter(csvFile));

		PrintWriter fileWriter = new PrintWriter(bfWriter);
		PrintWriter csvWriter = new PrintWriter(csvBfWriter);


		for(int i = 0; i<fields_str.length; i++){
			csvWriter.print(fields_str[i]);
			if(i+1<fields_str.length){
				csvWriter.print(",");
			}
		}
		
		csvWriter.println();
		System.out.println("catch");
		int page;
		for(int dateCount = 0; dateCount<startingDates.length; dateCount++){
			page = 1;

			JSONObject root = loadWebsite(startingDates[dateCount],endingDates[dateCount],page);
			TimeUnit.SECONDS.sleep(1);
			JSONObject response = root.getJSONObject("response");
			JSONArray docs = response.getJSONArray("docs");

			for(int docCount = 0; docCount < docs.length(); docCount++){
				JSONObject doc = docs.getJSONObject(docCount);

				for(int i = 0; i<fields_str.length; i++){
//					try{
//						JSONObject field = doc.getJSONObject(fields_str[i]);
//						System.out.println(field.length());
//					}catch(JSONException e){
//						System.err.println(e);
//					}
					fileWriter.println(doc.get(fields_str[i]).toString());
					csvWriter.print("\""+doc.get(fields_str[i]).toString().replaceAll("\"", "\"\"")+"\"");
					if(docCount+1<docs.length()){
						csvWriter.print(",");
					}
				}
				csvWriter.println();
				
				fileWriter.flush();
				csvWriter.flush();
			}
			System.out.println(page);

			while(docs.length()>=10 && page<10){
				page++;
				root = loadWebsite(startingDates[dateCount],endingDates[dateCount],page);
				TimeUnit.SECONDS.sleep(1);
				response = root.getJSONObject("response");
				docs = response.getJSONArray("docs");

				for(int docCount = 0; docCount < docs.length(); docCount++){
					JSONObject doc = docs.getJSONObject(docCount);

					for(int i = 0; i<fields_str.length; i++){
//						try{
//							JSONObject field = doc.getJSONObject(fields_str[i]);
//							System.out.println(field.length());
//						}catch(JSONException e){
//							System.err.println(e);
//						}
						fileWriter.println(doc.get(fields_str[i]).toString());
						csvWriter.print("\""+doc.get(fields_str[i]).toString()+"\"");
						if(docCount+1<docs.length()){
							csvWriter.print(",");
						}
					}
					csvWriter.println();
					
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

		String urlAddress = "http://api.nytimes.com/svc/search/v2/articlesearch.json?q="+query
				+"&facet_field=source&page="+page+"&begin_date="+startDate+"&end_date="+endDate;

		for(int i = 0; i<fields_str.length; i++){
			urlAddress +=fields_str[i];
			if(i+1<fields_str.length){
				urlAddress+=",";
			}
		}

		urlAddress+="&api-key=";

		try {
			url= new URL(urlAddress+apiKey);
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
