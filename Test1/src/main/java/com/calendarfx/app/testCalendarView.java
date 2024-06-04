package com.calendarfx.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
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
    	saveUpdate(10000);
    	
    	//StackPane holds the ui elements
        StackPane stackPane=new StackPane();
        
        //buttons for exporting and importing
        Button saveButton=new Button("EXPORT");
        Button loadButton=new Button("IMPORT");

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent eventt) {
        		FileChooser fileChooser=new FileChooser();
        		File file=fileChooser.showSaveDialog(primaryStage);
        		if(file!=null)
        			saveFile(calendarView,file);
        		//saveCalendarView(calendarView,SaveFileName);
        	}
        });
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load Calendar View");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    calendarView = loadFile(file);
                    if (calendarView != null) {
                        stackPane.getChildren().clear(); // Clear existing content
                        stackPane.getChildren().addAll(calendarView, saveButton, loadButton); // Add loaded calendar
                        calendarView.showMonthPage();
                    }
                }
            }
        });
        
        saveButton.setScaleX(0.9);
        saveButton.setScaleY(0.9);
        loadButton.setScaleX(0.9);
        loadButton.setScaleY(0.9);
    	
        
        
        
        //give calendarView details to stackPane
        stackPane.getChildren().addAll(calendarView,saveButton,loadButton); 
        
        StackPane.setAlignment(saveButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(loadButton, Pos.TOP_RIGHT);
        StackPane.setMargin(saveButton, new Insets(10, 275, 0, 0));
        StackPane.setMargin(loadButton, new Insets(10, 215, 0, 0));

        
        
        
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
        //primaryStage.sizeToScene();
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        //alert if event is occuring today
        List<Entry<?>> entries=getEntries(calendarView);
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
    	Thread saveUpdateThread = new Thread("Calendar: Save Update Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        saveCalendarView(calendarView,SaveFileName);
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
        saveUpdateThread.setPriority(Thread.MIN_PRIORITY);
        saveUpdateThread.setDaemon(true);
        saveUpdateThread.start();
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
    
    public static void saveFile(CalendarView calendarView, File file) {
    	try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
        	String id=null;
            List<CalendarSource> calendarSources = calendarView.getCalendarSources();
            for (CalendarSource calendarSource : calendarSources) {
                List<Calendar> calendars = calendarSource.getCalendars();
                for (Calendar<?> calendar : calendars) {
                    writer.println("Calendar:" + calendar.getName());
                    List<Entry<?>> entries = calendar.findEntries("");
                    for (Entry<?> entry : entries) {
                    	if(id==null||id!=entry.getId()) {
	                        writer.println("  Entry:");
	                        writer.println("    Title:" + entry.getTitle());
	                        writer.println("    ID:"+entry.getId());
	                        writer.println("    Location:" + entry.getLocation());
	                        writer.println("    StartDate:" + entry.getStartDate());
	                        writer.println("    EndDate:" + entry.getEndDate());
	                        writer.println("    StartTime:" + entry.getStartTime().toString());
	                        writer.println("    EndTime:" + entry.getEndTime().toString());
                    	}
                    	id=entry.getId();
                    }
                }
            }
            System.out.println("Calendar view data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static CalendarView loadFile(File file) {
    	CalendarSource calendarSource = new CalendarSource();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Calendar<?> currentCalendar = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Calendar:")) {
                    String calendarName = line.substring("Calendar:".length());
                    currentCalendar = new Calendar(calendarName);
                    calendarSource.getCalendars().add(currentCalendar);
                } else if (line.trim().startsWith("Entry:")) {
                    String title = reader.readLine().trim().substring("Title:".length());
                    String id = reader.readLine().trim().substring("ID:".length());
                    String location = reader.readLine().trim().substring("Location:".length());
                    String startDateStr = reader.readLine().trim().substring("StartDate:".length());
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    String endDateStr = reader.readLine().trim().substring("EndDate:".length());
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    String startTimeStr = reader.readLine().trim().substring("StartTime:".length());
                    LocalTime startTime = LocalTime.parse(startTimeStr);
                    String endTimeStr = reader.readLine().trim().substring("EndTime:".length());
                    LocalTime endTime = LocalTime.parse(endTimeStr);

                    Entry<?> entry = new Entry<>(title);
                    entry.setTitle(title);
                    entry.setId(id);
                    entry.setLocation(location);
                    entry.changeStartDate(startDate);
                    entry.changeEndDate(endDate);
                    entry.changeStartTime(startTime);
                    entry.changeEndTime(endTime);

                    if (currentCalendar != null) {
                        currentCalendar.addEntry(entry);
                    }
                }
            }
            System.out.println("Calendar view data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        CalendarView calendarView = new CalendarView();
        calendarView.getCalendarSources().add(calendarSource);
        return calendarView;
    }
    
    
    /*
     * saveCalendarView takes a CalendarView view and a String file name and saves the
     * calendar details in the view into the file name given.
     */
    public static void saveCalendarView(CalendarView calendarView, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
        	String id=null;
            List<CalendarSource> calendarSources = calendarView.getCalendarSources();
            for (CalendarSource calendarSource : calendarSources) {
                List<Calendar> calendars = calendarSource.getCalendars();
                for (Calendar<?> calendar : calendars) {
                    writer.println("Calendar:" + calendar.getName());
                    List<Entry<?>> entries = calendar.findEntries("");
                    for (Entry<?> entry : entries) {
                    	if(id==null||id!=entry.getId()) {
	                        writer.println("  Entry:");
	                        writer.println("    Title:" + entry.getTitle());
	                        writer.println("    ID:"+entry.getId());
	                        writer.println("    Location:" + entry.getLocation());
	                        writer.println("    StartDate:" + entry.getStartDate());
	                        writer.println("    EndDate:" + entry.getEndDate());
	                        writer.println("    StartTime:" + entry.getStartTime().toString());
	                        writer.println("    EndTime:" + entry.getEndTime().toString());
                    	}
                    	id=entry.getId();
                    }
                }
            }
            System.out.println("Calendar view data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * loadCalendarView takes a String file name and reads the details in the save file and
     * creates a CalendarView object containing the calendars and events in the file.
     */
    public static CalendarView loadCalendarView(String fileName) {
        calendarSource = new CalendarSource();
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
                    String id = reader.readLine().trim().substring("ID:".length());
                    String location = reader.readLine().trim().substring("Location:".length());
                    String startDateStr = reader.readLine().trim().substring("StartDate:".length());
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    String endDateStr = reader.readLine().trim().substring("EndDate:".length());
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    String startTimeStr = reader.readLine().trim().substring("StartTime:".length());
                    LocalTime startTime = LocalTime.parse(startTimeStr);
                    String endTimeStr = reader.readLine().trim().substring("EndTime:".length());
                    LocalTime endTime = LocalTime.parse(endTimeStr);

                    Entry<?> entry = new Entry<>(title);
                    entry.setTitle(title);
                    entry.setId(id);
                    entry.setLocation(location);
                    entry.changeStartDate(startDate);
                    entry.changeEndDate(endDate);
                    entry.changeStartTime(startTime);
                    entry.changeEndTime(endTime);

                    if (currentCalendar != null) {
                        currentCalendar.addEntry(entry);
                    }
                }
            }
            System.out.println("Calendar view data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        calendarView = new CalendarView();
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(calendarSource);
        return calendarView;
    }
    
    /*
     * saveCalendarSouce takes a CalendarSource source and a String file name and saves the 
     * calendar details in the source into the file name given.
     */
    public static void saveCalendarSource(CalendarSource calendarSource, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
        	String id=null;
            List<Calendar> calendars = calendarSource.getCalendars();
            for (Calendar<?> calendar : calendars) {
                writer.println("Calendar:" + calendar.getName());
                List<Entry<?>> entries=calendar.findEntries("");
                for (Entry<?> entry : entries) {
                	if(id==null||id!=entry.getId()) {
                		writer.println("  Entry:");
                        writer.println("    Title:" + entry.getTitle());
                        writer.println("    ID:"+entry.getId());
                        writer.println("    Location:"+entry.getLocation());
                        writer.println("    StartDate:" + entry.getStartDate());
                        writer.println("    EndDate:" + entry.getEndDate());
                        writer.println("    StartTime:"+entry.getStartTime().toString());
                        writer.println("    EndTime:"+entry.getEndTime().toString());
                	}
                    
                	id=entry.getId();

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
                    String id = reader.readLine().trim().substring("ID:".length());
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
                    entry.setTitle(title);
                    entry.setId(id);
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
    public static List<Entry<?>> getEntries(CalendarView view) {
        List<Entry<?>> allEntries = new ArrayList<>();
        List<CalendarSource> sources = view.getCalendarSources();
        for (CalendarSource source : sources) {
            List<Calendar> calendars = source.getCalendars();
            for (Calendar calendar : calendars) {
                List<Entry<?>> entries = calendar.findEntries("");
                allEntries.addAll(entries);
            }
        }
        return allEntries;
    }
    
    /*
     * getEntries takes a CalendarSource object and returns all entries
     */
	public static List<Entry<?>> getEntries(CalendarSource source) {
	    List<Entry<?>> allEntries = new ArrayList<>();
	    List<Calendar> calendars = source.getCalendars();
	    for (Calendar calendar : calendars) {
	        List<Entry<?>> entries = calendar.findEntries("");
	        allEntries.addAll(entries);
	    }
	    return allEntries;
	}    
	//End of Methods//

    
    public static void main(String[] args) {
    	/*calendarSource=loadCalendarSource(SaveFileName);
        if(calendarSource==null) {
            System.out.print("no Calendar Source data");
            Calendar test=new Calendar("test");
            calendarSource= new CalendarSource();
            calendarSource.getCalendars().add(test);
            saveCalendarSource(calendarSource,SaveFileName);
        }
        calendarView=createCalendarView(calendarSource);
		*/
    	calendarView=loadCalendarView(SaveFileName);
    	if(calendarView==null) {
    		System.out.println("No Calendar View Data Found:");
    		Calendar test=new Calendar("test");
    		System.out.println("Creating New Calendar:");
            calendarSource= new CalendarSource();
    		System.out.println("Creating New Calendar Source:");
            calendarSource.getCalendars().add(test);
    		System.out.println("Assigning Calendar into Source:");
            calendarView.getCalendarSources().setAll(calendarSource);
    		System.out.println("Assigning Source into View:");
    		System.out.println("New Calendar View Class Created:");
    		saveCalendarView(calendarView,SaveFileName);
    	}
    	
    	

        launch(args);
    }


}