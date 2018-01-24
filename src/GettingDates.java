import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class GettingDates {

	private String[] startingDates = null;
	
	public String[] getStartingDates() {
		return startingDates;
	}

	public String[] getEndingDates() {
		return endingDates;
	}
	
	private String[] endingDates = null;
	private Date[] dateStart = null;
	private Date[] dateEnd = null;
	
	
	public Date[] getDateStart() {
		return dateStart;
	}

	public Date[] getDateEnd() {
		return dateEnd;
	}

	public static void main(String[] args) {
		GettingDates dates = new GettingDates();
		String[] start = dates.getStartingDates();
		String[] end = dates.getEndingDates();
		
		for(int i = 0; i<start.length; i++){
			System.out.println(start[i]);
			System.out.println(end[i]+"\n");
		}
		
	}
	
	public GettingDates() {
		Calendar calendar = Calendar.getInstance();
		//		calendar.add(Calendar.YEAR, -1);
		System.out.println(calendar.get(Calendar.YEAR));

		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.DAY_OF_YEAR, 20);
		//		System.out.println(calendar.getTime().toString());

		int year, month, day;

		startingDates = new String[4];
		endingDates = new String[4];
		dateStart = new Date[4];
		dateEnd = new Date[4];
		
		for(int i = 0; i< 3; i++){

			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);

			startingDates[i] = String.format("%02d%02d%02d", year, month+1, day);
			dateStart[i] = (Date) calendar.getTime().clone();
			
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			endingDates[i] = String.format("%02d%02d%02d", year, month+1, day);
			dateEnd[i] = (Date) calendar.getTime().clone();

			
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		startingDates[3] = String.format("%02d%02d%02d", year, month+1, day);

		calendar.set(Calendar.DAY_OF_MONTH, 29);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		endingDates[3] = String.format("%02d%02d%02d", year, month+1, day);

		calendar.add(Calendar.DAY_OF_YEAR, 1);

	}
}
