package no.hiof.fridgebro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setUpRecycleView();
    }
    /*
    private void setUpRecycleView(){
        recyclerView = findViewById(R.id.reycleMain);
        recyclerView.setAdapter();
        recyclerView.setLayoutManager();
    }*/

}
