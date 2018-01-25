package secret;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class breit_website_parsing_test {

	public static void main(String[] args) throws UnsupportedEncodingException, IOException, InterruptedException {

		File fileWrite = new File("btestOutput.txt");
		File csvWriteFile = new File("./btestCSV.csv");

		BufferedWriter bfwriter = new BufferedWriter(new FileWriter(fileWrite));
		BufferedWriter csvBFwriter = new BufferedWriter(new FileWriter(csvWriteFile));
		PrintWriter writer = new PrintWriter(bfwriter);
		PrintWriter csvWriter = new PrintWriter(csvBFwriter);

		writer.println("Breitbart Test Doc");
		writer.println();

		csvWriter.println("Year,Month,Day,Time,End Year,End Month,End Day,End Time,Display Date,Headline,Text,Media,Media Credit,Media Caption,Media Thumbnail,Type,Group,Background");
		csvWriter.println(",,,,,,,,,Breitbart Articles for 100 Days of Trump,A collection of Breitbart Articles written during President Trump's first 100 days.,http://storage.torontosun.com/v1/suns-prod-images/1297660878712_LARGE_BOX.jpg?quality=80&stmp=1481008151209,Zach Wise/verite.co,\"<a href=\"\"http://www.flickr.com/photos/zachwise/6115056146/\"\" title=\"\"Chicago by zach.wise, on Flickr\"\">Chicago by zach.wise</a>\",,title,,");

		LinkedList<String> url_Strings = new LinkedList<String>();
		//		BufferedWriter bfwriter = new BufferedWriter(new FileWriter(new File("./breitbartlist.txt")));
		//		PrintWriter writer = new PrintWriter(bfwriter);

		for(int pageNumber = 30; pageNumber>=1; pageNumber--){

			Document doc = Jsoup.connect("http://www.breitbart.com/tag/president-trump/page/"+pageNumber+"/").get();


			Elements articles = doc.select("article");



			for(int i = 0; i<articles.size();i++){
				Elements links = articles.get(i).select("a[href]");

				url_Strings.add(String.format("%s", links.get(0).attr("abs:href")));
			}

			System.out.println(pageNumber);
			TimeUnit.SECONDS.sleep(1);

		}
		System.out.println("Finished");
		//		writer.flush();
		//		writer.close();

		int index = url_Strings.size();
		for(String url: url_Strings){
			System.out.println(index--);
			Document doc = Jsoup.connect(url).get();

			Elements articles = doc.select("meta[property=\"og:title\"]");

			String title = doc.selectFirst("meta[property=\"og:title\"]").attr("content");
			String description = (doc.selectFirst("meta[property=\"og:description\"]").attr("content"));
			String published_date = (doc.selectFirst("meta[property=\"article:published_time\"]").attr("content"));


			String[] pub_dates = published_date.split("-");

			csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+",");
			csvWriter.print(pub_dates[0]+","+pub_dates[1]+","+pub_dates[2].substring(0, 2)+","+pub_dates[2].substring(3, 10)+","+",");
			csvWriter.print(title+",");

			csvWriter.print("\""+description.trim()+"\"");


			csvWriter.print(" Found at: "+url+",,,,,,,\n");



			writer.println(title);
			writer.println(published_date);
			writer.println(description);
			writer.println(url);
			writer.println();
			writer.flush();
			TimeUnit.SECONDS.sleep(1);

		}




		csvWriter.flush();
		csvWriter.close();

		writer.flush();
		writer.close();



	}

	public static void goal() throws UnsupportedEncodingException, IOException, InterruptedException{

		LinkedList<String> url_Strings = new LinkedList<String>();
		//		BufferedWriter bfwriter = new BufferedWriter(new FileWriter(new File("./breitbartlist.txt")));
		//		PrintWriter writer = new PrintWriter(bfwriter);

		for(int pageNumber = 30; pageNumber>=1; pageNumber--){

			Document doc = Jsoup.connect("http://www.breitbart.com/tag/president-trump/page/"+pageNumber+"/").get();


			Elements articles = doc.select("article");



			for(int i = 0; i<articles.size();i++){
				Elements links = articles.get(i).select("a[href]");

				url_Strings.add(String.format("%s", links.get(0).attr("abs:href")));
			}

			System.out.println(pageNumber);
			TimeUnit.SECONDS.sleep(1);

		}
		System.out.println("Finished");
		//		writer.flush();
		//		writer.close();

		for(String url: url_Strings){
			Document doc = Jsoup.connect(url).get();

		}

	}

}
