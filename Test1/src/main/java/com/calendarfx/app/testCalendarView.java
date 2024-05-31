package com.calendarfx.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class testCalendarView extends Application {
    //Start of GlobalVariables//
    public static final String SaveFileName="calendarSourceData.txt";
    public static CalendarView calendarView;
    public static CalendarSource calendarSource;
    //End of GlobalVariables//

    public void start(Stage primaryStage) {
    	
    	//save the calendar details every 10 seconds
       saveUpdate(10);

        //StackPane holds the ui elements
        StackPane stackPane=new StackPane();
        //give calendarView details to stackPane
        stackPane.getChildren().addAll(calendarView); 
        
        //Update time every 10 seconds
        timeUpdate(10000);
        //sets the page view to month
        calendarView.showMonthPage();
        
        //create scene that holds the display
        Scene scene=new Scene(stackPane);
        //show what is being interacted with at the moment
        scene.focusOwnerProperty().addListener(it -> System.out.println("focus owner: " + scene.getFocusOwner()));
        CSSFX.start(scene);
        
        //set the details for the window
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        //alert if event is occuring today
        List<Entry> entries=getEntries(calendarView);
        for(Entry entry:entries) {
        	LocalDate entryDate=entry.getStartDate();
        	if(LocalDate.now().isEqual(entryDate))
        		openAlertWindow(entry);
        }
    }
    
    //Start of Methods//    
    /*
     * timeUpdate creates and starts a thread that updates the time on the calendar
     * for time milliseconds
    */
    public void timeUpdate(int time) {
    	Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        //starts the threads
        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
    }
    
    /*
     * saveUpdate saves the calendar details every time seconds
     */
    public void saveUpdate(int time) {
    	 ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

         // Schedule the task to run every minute
         scheduler.scheduleAtFixedRate(new Runnable() {
             @Override
             public void run() {
                 // Call your method here
                 saveCalendarSource(calendarSource, SaveFileName);
             }
         }, 0, time, TimeUnit.SECONDS);
    }
    
    /*
     * openAlertWindow creates a new window that takes entry and places the entry 
     * details into the window.
     */
    public void openAlertWindow(Entry<?> entry) {
        Stage settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Alert!: EVENT TODAY!");

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Title: " + entry.getTitle());
        titleLabel.setFont(new Font("Arial",20));
        Label startDateLabel = new Label("Start Date: " + entry.getStartDate().toString());
        Label endDateLabel = new Label("End Date: " + entry.getEndDate().toString());
        Label startTimeLabel = new Label("Start Time: " + entry.getStartTime().toString());
        Label endTimeLabel = new Label("End Time: " + entry.getEndTime().toString());
        Label locationLabel = new Label("Location: " + entry.getLocation());

        root.getChildren().addAll(titleLabel, startDateLabel, endDateLabel, startTimeLabel, endTimeLabel, locationLabel);

        Scene scene = new Scene(root, 300, 250);
        settingsStage.setScene(scene);
        settingsStage.show();
    }

    /*
     * saveCalendarSouce takes a CalendarSource source and a String file name and saves the 
     * calendar details in the source into the file name given.
     */
    public static void saveCalendarSource(CalendarSource calendarSource, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            List<Calendar> calendars = calendarSource.getCalendars();
            for (Calendar<?> calendar : calendars) {
                writer.println("Calendar:" + calendar.getName());
                List<Entry<?>> entries=calendar.findEntries("");
                for (Entry<?> entry : entries) {
                    writer.println("  Entry:");
                    writer.println("    Title:" + entry.getTitle());
                    writer.println("    Location:"+entry.getLocation());
                    writer.println("    StartDate:" + entry.getStartDate());
                    writer.println("    EndDate:" + entry.getEndDate());
                    writer.println("    StartTime:"+entry.getStartTime().toString());
                    writer.println("    EndTime:"+entry.getEndTime().toString());

                    // Add more entry details as needed
                }
            }
            System.out.println("Calendar source data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * loadCalendarSource takes a String fileName and reads the details in the save file and
     * creates a calendarSource class containing the calendars and events in the file
     */
    public static CalendarSource loadCalendarSource(String fileName) {
        CalendarSource calendarSource = new CalendarSource();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            Calendar<?> currentCalendar = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Calendar:")) {
                    String calendarName = line.substring("Calendar:".length());
                    currentCalendar = new Calendar(calendarName);
                    calendarSource.getCalendars().add(currentCalendar);
                } else if (line.trim().startsWith("Entry:")) {
                    String title = reader.readLine().trim().substring("Title:".length());
                    String location=reader.readLine().trim().substring("Location:".length());
                    String startDateStr = reader.readLine().trim().substring("StartDate:".length());
                    LocalDate startDate=LocalDate.parse(startDateStr);
                    String endDateStr = reader.readLine().trim().substring("EndDate:".length());
                    LocalDate endDate=LocalDate.parse(endDateStr);
                    //localTime
                    String startTimeStr=reader.readLine().trim().substring("StartTime:".length());
                    LocalTime startTime=LocalTime.parse(startTimeStr);
                    String endTimeStr=reader.readLine().trim().substring("EndTime:".length());
                    LocalTime endTime=LocalTime.parse(endTimeStr);
                    //set date
                    Entry<?> entry = new Entry<>(title);
                    entry.setLocation(location);
                    entry.changeStartDate(startDate);
                    entry.changeEndDate(endDate);
                    //set time
                    entry.changeStartTime(startTime);
                    entry.changeEndTime(endTime);
                    // Add more entry details as needed
                    if (currentCalendar != null) {
                        currentCalendar.addEntry(entry);
                    }
                }
            }
            System.out.println("Calendar source data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return calendarSource;
    }

    /*
     * createCalendarView creates and sets a calendarView object with a calendarSource object
     */
    public static CalendarView createCalendarView(CalendarSource source) {
        CalendarView calendarView=new CalendarView();
        calendarView.getCalendarSources().setAll(source);
        return calendarView;
    }

    /*
     * getEntries takes a CalendarView Object and returns all entries
     */
    public static List<Entry> getEntries(CalendarView view){
    	List<Entry> entries=null;
    	List<CalendarSource> sources=calendarView.getCalendarSources();
        for(CalendarSource source:sources) {
            List<Calendar> calendars=source.getCalendars();
            for(Calendar calendar:calendars) {
                entries=calendar.findEntries("");
                //return entries;
            }
        }
    	return entries;
    }
    
    /*
     * getEntries takes a CalendarSource object and returns all entries
     */
    public static List<Entry> getEntries(CalendarSource source){
    	List<Entry> entries=null;

    	List<Calendar> calendars=source.getCalendars();
    	for(Calendar calendar:calendars) {
    		entries=calendar.findEntries("");
    		//return entries;
    	}
    	return entries;
    }
    //End of Methods//

    
    public static void main(String[] args) {
        calendarSource=loadCalendarSource(SaveFileName);
        if(calendarSource==null) {
            System.out.print("no Calendar Source data");
            Calendar test=new Calendar("test");
            calendarSource= new CalendarSource();
            calendarSource.getCalendars().add(test);
            saveCalendarSource(calendarSource,SaveFileName);
        }
        calendarView=createCalendarView(calendarSource);


        launch(args);
    }


}