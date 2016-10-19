package com.example.ofirm.minesweeper;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class PagerActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public static FragmentManager fragmentManager;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private PlaceholderFragment f1;
    private PlaceholderFragment f2;
    private PlaceholderFragment f3;
    private MapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        f1 = f1.newInstance(1);
        f2 = f2.newInstance(2);
        f3 = f3.newInstance(3);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {



        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return f1;
                    //return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return f2;
                    //return PlaceholderFragment.newInstance(position + 1);
                case 2:
                    return f3;
                    //return PlaceholderFragment.newInstance(position + 1);
                case 3: {
                    DataBase db = new DataBase(getBaseContext());
                    ArrayList<Score> dbscores = db.getAllScoresByLevel(4);
                    ArrayList<Score> easy = db.getAllScoresByLevel(1);
                    ArrayList<Score> medium = db.getAllScoresByLevel(2);
                    ArrayList<Score> hard = db.getAllScoresByLevel(3);

                    db.close();
                    map = new MapFragment(dbscores,easy,medium,hard);
                    return map;
                }
                    //return new MapFragment();
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "EASY";
                case 1:
                    return "MEDIUM";
                case 2:
                    return "HARD";
                case 3:
                    return "MAP";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private SectionsPagerAdapter mSectionsPagerAdapter;
        private ArrayList<Score> scores = new ArrayList<>();
        private Score score;
        private ScoreAdapter adapter;
        private ListView listView;
        private boolean flag = false;
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);


            if (!flag) {
                setListViewAdapter(rootView);
                flag = true;
            }
            else{
                adapter.clear();
                flag = false;
            }

            return rootView;
        }

        public void setListViewAdapter(View root){
            ListView listView = (ListView)root.findViewById(R.id.scorelistView);
            DataBase db = new DataBase(root.getContext());
            for (int i=0; i < Score.MAX_SCORES ; i++){
                scores.add(new Score());
            }
            adapter = new ScoreAdapter(getContext(),scores);
            listView.setAdapter(adapter);
            setScoresByLevel(getArguments().getInt(ARG_SECTION_NUMBER),db);
        }

        public void setScoresByLevel(int level,DataBase db){
            ArrayList<Score> DBScores = db.getAllScoresByLevel(level);

            if (level !=4) {
                for (int i = 0; i < Score.MAX_SCORES && DBScores.size() > i; i++) {
                    scores.set(i, DBScores.get(i));
//                    Log.d("name", DBScores.get(i).getName());
//                    Log.d("lat", String.valueOf(DBScores.get(i).getLatitude()));
//                    Log.d("long",String.valueOf(DBScores.get(i).getLogitude()));
                }
            }
            adapter.notifyDataSetChanged();
           // db.close();
        }
    }



}
