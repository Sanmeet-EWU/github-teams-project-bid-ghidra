
package com.calendarfx.app;

import com.calendarfx.view.MonthView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestCalendarMonth extends Application {

    @Override
    public void start(Stage primaryStage) {
    	//MonthView holds all the elements for the month calendar
        MonthView monthView = new MonthView();

        //Used to layer children element on top of each other
        StackPane layering = new StackPane();
        //give the elements in monthView to the layers
        layering.getChildren().addAll(monthView);
        //Scene displays the calendar elements
        Scene scene = new Scene(layering);
        //sets the title for the window
        primaryStage.setTitle("Month View");
        primaryStage.setScene(scene);
        //fits the calendar to the screen size
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        //primaryStage.
        //displays the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);//launches the calendar
    }
}
