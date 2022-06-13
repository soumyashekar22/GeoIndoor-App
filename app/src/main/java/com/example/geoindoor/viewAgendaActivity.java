package com.example.geoindoor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.geoindoor.models.Agenda;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class viewAgendaActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Agenda> agendaList = new ArrayList<>();

    DatabaseReference databaseReference;
    private final Agenda agendaModel = new Agenda();

    private static final String TAG = "viewAgendaActivity";

    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;
    private String mobile,status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_agenda);
        getSupportActionBar().setTitle(R.string.view_agenda);

        // Pushing Mobile number
        mobile = getIntent().getStringExtra("userMobileNumber");

        listView = (ListView) findViewById(R.id.listView);
        final AgendaListAdapter adapter = new AgendaListAdapter(this, R.layout.adapterview, agendaList);

        databaseReference = FirebaseDatabase.getInstance().getReference("agendas");
        databaseReference.orderByChild("mobile").equalTo(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //change
                //databaseReference.orderByChild("status").equalTo(false);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //change
                    Agenda agendas = dataSnapshot1.getValue(Agenda.class);
                    if(!agendas.isStatus()) {
                        agendaList.add(agendas);
                        adapter.notifyDataSetChanged();
                    }
                    //Store date,time and destination on a new Trip.
//                    String datePassed = agendas.getDate().toString();
//                    String timePassed = agendas.getTime().toString();
//                    System.out.println("what is this date:" +datePassed);
//                    System.out.println("what is this time:" +timePassed);
//
//
//                    Long timestamp = null;
//                    try {
//                        timestamp = toMilli(datePassed+ " " + timePassed);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("what is this :" +timestamp);

//                    agendas.setTimestamp(timestamp);

//                    Collections.sort(agendaList);

                }
                listView.setAdapter(adapter);
                if(agendaList.isEmpty())
                {
                    toastMsg(getString(R.string.no_agenda));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int count = (int)id;
                Intent intent = new Intent(viewAgendaActivity.this, detailActivity.class);
                intent.putExtra("count",count);
                intent.putExtra("mobileNumber", mobile);
                intent.putParcelableArrayListExtra("key",  agendaList);
                startActivity(intent);



            }
        });
        gestureDetector = new GestureDetector(this, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector){
                float scale = 1-detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if(mScale<0.1f)
                    mScale = 0.1f;

                if(mScale>10f)
                    mScale=10f;

                ScaleAnimation scaleAnimation = new ScaleAnimation(1f/prevScale,1f/mScale,1f/prevScale,1f/mScale, detector.getFocusX(),detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                listView.startAnimation(scaleAnimation);

                return true;
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e){
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e){
            return true;
        }
    }

//    public Long toMilli (String dateIn) throws ParseException {
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        Date date = (Date) formatter.parse(dateIn);
//        long output = date.getTime() / 1000L;
//        String str = Long.toString(output);
//        long timestamp = Long.parseLong(str) * 1000;
//        return timestamp;
//    }

    public void toastMsg(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

}
