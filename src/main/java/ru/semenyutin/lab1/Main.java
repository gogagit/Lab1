package ru.semenyutin.lab1;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;

public final class Main extends Application {
    private final Expedition game=new Expedition(new Random());
    private final Label score=new Label();
    private final TextArea history=new TextArea();
    private final HBox routes=new HBox(10);
    private Button dock;
    public static void main(String[] args){launch(args);}
    @Override public void start(Stage stage){
        for(var route:Expedition.Route.values()){
            Button b=Fx.button(route.toString(),()->{log(game.fly(route));refresh();});
            b.getStyleClass().add("accent");routes.getChildren().add(b);
        }
        dock=Fx.button("Сдать груз",()->{log("На станцию доставлено: "+game.dock());refresh();});
        Button reset=Fx.button("Новая экспедиция",()->{game.reset();history.clear();log("Экспедиция началась.");refresh();});
        history.setEditable(false);history.setWrapText(true);
        VBox panel=new VBox(14,Fx.title("Орбитальный сборщик"),new Label("Выберите сектор. Сдавайте груз вовремя: шторм уничтожает несданную добычу."),score,routes,new HBox(10,dock,reset),history);
        panel.setPadding(new Insets(22));VBox.setVgrow(history,Priority.ALWAYS);
        refresh();log("Станция выдала 8 единиц энергии.");
        Fx.show(stage,"Лабораторная 1 · Орбитальный сборщик",panel,850,550);
    }
    private void refresh(){
        score.setText("Энергия: "+game.energy()+"    Груз: "+game.cargo()+"    Полёты: "+game.flights()+(game.free()?"    · следующий полёт бесплатный":""));
        routes.setDisable(!game.canFly());dock.setDisable(game.cargo()==0);
    }
    private void log(String s){history.appendText(s+System.lineSeparator());}
}
