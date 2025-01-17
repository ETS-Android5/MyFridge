package com.example.myfridge.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.example.myfridge.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment{
    public static final String ITEM = "item";
    public static final String DATE = "date";
    public static final String CATEGORY = "category";

    String category;
    String itemAdded;
    String currentDateString = "";

    Button btnEnter;
    Button btnCalendar;
    EditText item;
    TextView tvDate;

    final Calendar c = Calendar.getInstance();

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        item = view.findViewById(R.id.etItemAdded);
        btnCalendar = view.findViewById(R.id.btnCalendar);
        btnEnter = view.findViewById(R.id.btnSubmit);
        tvDate = view.findViewById(R.id.tvDate);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
                datePickerDialog.show();
            }
        });


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAdded = item.getText().toString();
                //Check is item or date input is empty
                if(itemAdded.isEmpty()){
                    Toast.makeText(getContext(), "Item Can't be Empty", Toast.LENGTH_SHORT).show();
                }else if(currentDateString.isEmpty()){
                    Toast.makeText(getContext(), "Must have Date", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString(ITEM, itemAdded);
                    bundle.putString(DATE, currentDateString);
                    bundle.putString(CATEGORY, category);

                    //When Submit button is clicked then Refrigerator Fragment is displayed
                    FragmentManager fragmentManager = getFragmentManager();
                    RefrigeratorFragment fragment = new RefrigeratorFragment();
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragment, null).commit();
                    item.setText("");
                    tvDate.setText("");
                    scheduleNotification();
                }
            }
        });
    }

    //Initializes notification
    //Following code from: https://www.youtube.com/watch?v=oLngUva-Ves&t=187s
    //Retrieved on Jan. 9th, 2021
    private void scheduleNotification() {
        NotifyMe notifyMe = new NotifyMe.Builder(getContext())
                .title("MyFridge")
                .content( "Your " + itemAdded + " will EXPIRE today!")
                .color(105, 22, 247, 0)
                .led_color(255, 255, 255, 255)
                .time(c)
                .addAction(new Intent(String.valueOf(getActivity())), "Done")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Sets calendar instance to particular date & time for notification
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            //I'm setting it to notify individuals @ 8 A.M. of the expiration date
            //but time can be changed to test results
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            //This is to format date as: mm/dd/year
            year = year%100;
            //month is 9 because October is in index 9
            if(month < 9 && day < 10){
                currentDateString = "0" + (month+1) + "/" + "0" + day + "/" + year;
            } else if (month < 9) {
                currentDateString = "0" + (month+1) + "/" + day + "/" + year;
            } else if (day < 10) {
                currentDateString = (month+1) + "/" + "0" + day + "/" + year;
            }else{
                currentDateString = (month+1) + "/" + day + "/" + year;
            }
            tvDate.setText(currentDateString);
        }
    };
}