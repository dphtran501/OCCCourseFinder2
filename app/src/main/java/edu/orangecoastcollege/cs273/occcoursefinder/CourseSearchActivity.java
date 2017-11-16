package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;

    private OfferingListAdapter offeringListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList);
        allInstructorsList = db.getAllInstructors();
        allCoursesList = db.getAllCourses();

        courseTitleEditText = (EditText) findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);
        instructorSpinner = (Spinner) findViewById(R.id.instructorSpinner);


        offeringsListView = (ListView) findViewById(R.id.offeringsListView);
        offeringListAdapter =
                new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);
        offeringsListView.setAdapter(offeringListAdapter);


        // Construct instructorSpinnerAdapter using the method getInstructorNames()
        // to populate the spinner.
        ArrayAdapter<String> instructorSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getInstructorNames());
        instructorSpinner.setAdapter(instructorSpinnerAdapter);
        instructorSpinner.setOnItemSelectedListener(instructorSpinnerListener);

    }

    // Create a method getInstructorNames that returns a String[] containing the entry
    // "[SELECT INSTRUCTOR]" at position 0, followed by all the full instructor names in the
    // allInstructorsList
    private String[] getInstructorNames()
    {
        String[] instructorNames = new String[allInstructorsList.size() + 1];
        instructorNames[0] = "[Select Instructor]";
        for (int i = 1; i < instructorNames.length; i++)
            instructorNames[i] = allInstructorsList.get(i - 1).getFullName();

        return instructorNames;
    }

    // Create a void method named reset that sets the test of the edit text back to an
    // empty string, sets the selection of the Spinner to 0 and clears out the offeringListAdapter,
    // then rebuild it with the allOfferingsList
    public void reset(View v)
    {
        // Set spinner back
        instructorSpinner.setSelection(0);
        courseTitleEditText.setText("");
        // Clear out the list adapter
        offeringListAdapter.clear();
        // Repopulate it from allOfferingsList
        offeringListAdapter.addAll(allOfferingsList);
    }

    // Create a TextWatcher named courseTitleTextWatcher that will implement the onTextChanged method.
    // In this method, set the selection of the instructorSpinner to 0, then
    // Clear the offeringListAdapter
    // If the entry is an empty String "", the offeringListAdapter should addAll from the allOfferingsList
    // Else, the offeringListAdapter should add each Offering whose course title starts with the entry.

    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            String entry = charSequence.toString().trim().toUpperCase();
            // Clear the list adapter
            offeringListAdapter.clear();
            // Let's loop through Offerings
            for (Offering o : allOfferingsList)
            {
                // If the course title starts with entry, add it back to the list adapter
                if (o.getCourse().getTitle().toUpperCase().startsWith(entry))
                    offeringListAdapter.add(o);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    // Create an AdapterView.OnItemSelectedListener named instructorSpinnerListener and implement
    // the onItemSelected method to do the following:
    // If the selectedInstructorName != "[Select Instructor]", clear the offeringListAdapter,
    // then rebuild it with every Offering that has an instructor whose full name equals the one selected.
    public AdapterView.OnItemSelectedListener instructorSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View view, int i, long l)
        {
            // Retrieve the instructor name
            String instructorName = String.valueOf(spinner.getItemAtPosition(i));
            // Clear the adapter
            offeringListAdapter.clear();
            if (instructorName.equals("[Select Instructor]"))
                offeringListAdapter.addAll(allOfferingsList);
            else
            {
                for (Offering offering : allOfferingsList)
                    if (offering.getInstructor().getFullName().equals(instructorName))
                        offeringListAdapter.add(offering);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };

}
