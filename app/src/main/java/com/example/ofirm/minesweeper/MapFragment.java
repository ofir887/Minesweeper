package com.example.ofirm.minesweeper;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private final LatLng AFEKA_COLLEGE = new LatLng(32.112986, 34.817830);
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<Score> scores;
    private ArrayList<Score> easy;
    private ArrayList<Score> medium;
    private ArrayList<Score> hard;


    private GoogleMap mMap;
    private Marker marker;
    private Score s;
    private float hueGreen;


    public MapFragment(ArrayList<Score> scores , ArrayList<Score> easy , ArrayList<Score> medium , ArrayList<Score> hard) {
        this.scores = scores;
        this.easy = easy;
        this.medium = medium;
        this.hard = hard;
    }


        @Override
    public void onResume() {
        super.onResume();
        Log.d("MyMap", "onResume");
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            Log.d("MyMap", "setUpMapIfNeeded");
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyMap", "onMapReady");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        showAll();

        mMap.addMarker(new MarkerOptions()
                .position(AFEKA_COLLEGE)
                .title("home :)")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
//        Marker hamburg = mMap.addMarker(new MarkerOptions().position(temp)
//                .title(scores.get(0).getName()));
//        Marker kiel = mMap.addMarker(new MarkerOptions()
//                .position(KIEL)
//                .title("Kiel")
//                .snippet("Kiel is cool")
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.mipmap.ic_launcher)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AFEKA_COLLEGE, 15));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    public void showAll(){
            showHard();
            showMedium();
            showEasy();
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater(null)));
        Log.d("add all markers"," ");

    }

    public void showEasy(){
        for(int i=1 ; i<easy.size() ; i++){
            addMarker(easy.get(i),i, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        if(easy.size()>0)
            addWinnerMarker(easy.get(0), R.drawable.trophy_blue);
    }


    public void showMedium(){
        for(int i=1 ; i<medium.size() ; i++){
            addMarker(medium.get(i), i, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        if(medium.size()>0)
            addWinnerMarker(medium.get(0), R.drawable.trophy_green);
    }


    public void showHard(){
        for(int i=1 ; i<hard.size() ; i++){
            addMarker(hard.get(i), i, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        if(hard.size()>0)
            addWinnerMarker(hard.get(0),R.drawable.trophy_yellow);
    }

    private void addMarker(Score s, int i, BitmapDescriptor hue){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(s.getLatitude(), s.getLogitude()))
                .title("#" + (i+1) + " " + s.getName() + " " + String.valueOf(s.getTime() + " sec"))
                .snippet(getCompleteAddressString(s.getLatitude(), s.getLogitude()))
                .icon(hue));
    }
    private void addWinnerMarker(Score s, int trophy_icon) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(s.getLatitude(), s.getLogitude()))
                .title("#1 "+s.getName() + " " + String.valueOf(s.getTime() + " sec"))
                .snippet(getCompleteAddressString(s.getLatitude(), s.getLogitude())))
                .setIcon(BitmapDescriptorFactory.fromResource(trophy_icon));
    }



    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

//    public void showEasy(){
//        Log.d("showEasy:@@@@@@@@@@@@@@", " ");
//        int id = 0;
//        for(int i=0 ; i<scores.size() ; i++){
//            if(scores.get(i).getLevel()==1){
//                Log.d(" id "+scores.get(i).getId()," "+scores.get(i).getTime());
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(scores.get(i).getLatitude(), scores.get(i).getLogitude()))
//                        .title(scores.get(i).getName()))
//                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            }
//        }
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(scores.get(0).getLatitude(), scores.get(0).getLogitude()))
//                .title(scores.get(0).getName()))
//                .setIcon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.trophy_blue));
//    }



//    public void showMedium(){
//        Log.d("showMedium:############", " ");
//        for(int i=0 ; i<scores.size() ; i++){
//            if(scores.get(i).getLevel()==2){
//                Log.d(" id "+scores.get(i).getId()," "+scores.get(i).getTime());
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(scores.get(i).getLatitude(), scores.get(i).getLogitude()))
//                        .title(scores.get(i).getName()))
//                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            }
//        }
//    }

//    public void showHard(){
//        Log.d("showHard:$$$$$$$$$$$$$$"," ");
//        for(int i=0 ; i<scores.size() ; i++){
//            if(scores.get(i).getLevel()==3){
//                Log.d(" id "+scores.get(i).getId()," "+scores.get(i).getTime());
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(scores.get(i).getLatitude(), scores.get(i).getLogitude()))
//                        .title(scores.get(i).getName()))
//                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//            }
//        }
//    }


}
