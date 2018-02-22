package red.guih.games.solitaire;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import red.guih.games.BaseActivity;
import red.guih.games.R;

public class SolitaireActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitaire);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.solitaire);
            a.setDisplayHomeAsUpEnabled(true);
        }


//        setUserPreferences();


    }

//    @Override
//    public void start(Stage stage) throws Exception {
//		final Pane group = new Pane();
//		final BorderPane borderPane = new BorderPane(group);
//		borderPane.setStyle("-fx-background-color:green;");
//        final Scene scene = new Scene(borderPane);
//		SolitaireModel.create(group, scene);
//        stage.setScene(scene);
//		stage.setWidth(700);
//		stage.setHeight(600);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
}
