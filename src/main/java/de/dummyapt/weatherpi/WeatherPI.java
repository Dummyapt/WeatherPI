package de.dummyapt.weatherpi;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public final class WeatherPI extends Application {
    private final BorderPane borderPane = new BorderPane();
    private Connection connection;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        try {
            connection = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        borderPane.setTop(addHBox());
        borderPane.setLeft(addVBox());
        borderPane.setCenter(addFlowPane());
        borderPane.setBottom(addVBoxBottom());
        borderPane.setOnMousePressed(ae -> {
            xOffset = ae.getSceneX();
            yOffset = ae.getSceneY();
        });
        borderPane.setOnMouseDragged(ae -> {
            stage.setX(ae.getScreenX() - xOffset);
            stage.setY(ae.getScreenY() - yOffset);
        });
        borderPane.setStyle("""
                -fx-background-size: 1200 900;
                -fx-background-radius: 30;
                -fx-border-radius: 30;
                -fx-border-width:5;
                -fx-border-color: #FC3D44;""");

        final var scene = new Scene(borderPane);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.show();
    }

    private HBox addHBox() {
        final var title = new Label("WeatherPI");
        title.setFont(new Font(26));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        final var hBox = new HBox();
        hBox.setPadding(new Insets(7.5, 6, 7.5, 6));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(title);
        return hBox;
    }

    private VBox addVBox() {
        final var arduinos = new Label("Arduinos");
        arduinos.setFont(new Font(24));
        arduinos.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        final var vBox = new VBox(arduinos);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(8);

        final var buttons = new Button[5];
        for (int i = 0; i < 5; i++) {
            buttons[i] = new Button("Arduino " + (i + 1));
            VBox.setMargin(buttons[i], new Insets(0, 0, 0, 8));
            vBox.getChildren().add(buttons[i]);
            final var id = i;
            final var style = """
                    -fx-border-color: gold;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    -fx-background-size: 1200 900;
                    -fx-background-color: rgb(50,50,50);""";
            buttons[i].setOnAction(ae -> {
                final var location = getStations().get(id).location();
                final var temperature = getStations().get(id).temperature();
                final var humidity = getStations().get(id).humidity();

                final var lblLocation = new Label("Location:  " + location);
                lblLocation.setFont(new Font(19));

                final var lblTemp = new Label("Temperature: " + temperature + "°C");
                lblTemp.setFont(new Font(19));

                final var lblHumid = new Label("Humidity: " + humidity + "%");
                lblHumid.setFont(new Font(19));

                final var flowPane = new FlowPane();
                flowPane.setPadding(new Insets(5, 0, 5, 0));
                flowPane.setVgap(4);
                flowPane.setHgap(8);
                flowPane.setPrefWrapLength(250);
                flowPane.setStyle(style);
                flowPane.setAlignment(Pos.TOP_CENTER);
                flowPane.getChildren().addAll(Arrays.asList(lblLocation, lblTemp, lblHumid));
                borderPane.setCenter(flowPane);
            });
        }
        vBox.setPadding(new Insets(7.5, 18, 7.5, 6));
        return vBox;
    }

    private VBox addVBoxBottom() {
        final var btnExit = new Button("_Exit");
        btnExit.setOnAction(e -> System.exit(0));
        btnExit.setMaxSize(325, 200);
        btnExit.setAlignment(Pos.CENTER);
        btnExit.setOnAction(ae -> System.exit(-1));

        final var vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(8);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(btnExit);
        VBox.setMargin(btnExit, new Insets(0, 0, 0, 8));
        return vBox;
    }

    private FlowPane addFlowPane() {
        final var flowPane = new FlowPane();
        flowPane.setPadding(new Insets(5, 0, 5, 0));
        flowPane.setVgap(4);
        flowPane.setHgap(8);
        flowPane.setPrefWrapLength(250);
        flowPane.setStyle("""
                -fx-border-color: gold;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-background-size: 1200 900;
                -fx-background-color: rgb(50,50,50);""");
        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.getChildren().add(new Label("Choose an Arduino"));
        return flowPane;
    }

    private ObservableList<Station> getStations() {
        final ObservableList<Station> stations = FXCollections.observableArrayList();
        try (var resultSet = connection.createStatement().executeQuery("SELECT * FROM v_monitoring;")) {
            while (resultSet.next())
                stations.add(new Station(resultSet.getInt("id"), resultSet.getString("location"), resultSet.getDouble("temperature"), resultSet.getDouble("humidity")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }
}
