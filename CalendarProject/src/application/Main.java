package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Node;
import java.time.LocalDate;


public class Main extends Application {
	@Override
	public void start(Stage stage) {
//		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,1500,800);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	GridPane pane = new GridPane();
	pane.setAlignment(Pos.CENTER);
	
	DatePickerSkin date =  new DatePickerSkin(new DatePicker(LocalDate.now()));
	Node content = date.getPopupContent();
	
	pane.add(content, 0, 0);
	
	Scene scene = new Scene(pane, 250, 250);
	stage.setTitle("JavaFX calendar");
	stage.setScene(scene);
	stage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
