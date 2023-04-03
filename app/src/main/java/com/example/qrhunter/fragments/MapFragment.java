package com.example.qrhunter.fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrhunter.R;
import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.example.qrhunter.searchPlayer.QRCodeAdapter;
import com.example.qrhunter.searchPlayer.QRCodeListItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Class for the fragment that shows the Leaderboard and Search Player functionality **/
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, OnMapsSdkInitializedCallback {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("CodeList");
    private View view;
    private Context mContext;
    private SearchView searchView;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location myLocation;
    private static final long MIN_TIME = 60;
    private static final float MIN_DISTANCE = 10;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Called to create the view hierarchy associated with the fragment. This method is responsible for
     * inflating the fragment's layout and returning the root View of the inflated layout. If the fragment
     * does not have a UI or does not need to display a view, you can return null from this method.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to. This value may be null
     *                           if the fragment is not being attached to a parent view.
     * @param savedInstanceState A Bundle containing any saved state information for the fragment. This value may be null
     *                           if the fragment is being instantiated for the first time.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getActivity().getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

        view = inflater.inflate(R.layout.fragment_map, container, false);
        searchView = view.findViewById(R.id.search_view_map);

        //implemnting searhing for locations
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the query is input in the search view
             *
             * @param s String of the query input
             * @return None
             */
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                    Address address = addressList.get(0);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } catch (Exception e) {
                    Toast.makeText(mContext, "Cannot find location with that name.", Toast.LENGTH_LONG).show();
                    Log.d("Runtime exception", String.valueOf(e));
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.qr_map);
        mapFragment.getMapAsync(this);

        //check for location access and ask for permission if not
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "First enable LOCATION ACCESS", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return null;
        }

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        return view;
    }

    /**
     * Initilizes the map with the latest renderer
     *
     * @param renderer renderer object
     * @return None
     */
    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("MapsDemo", "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.d("MapsDemo", "The legacy version of the renderer is used.");
                break;
        }
    }

    /**
     * Hnadle permission request either accpeted or denied
     *
     * @param requestCode request code passed in the functions
     * @param permissions array of all the permissions requested
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED.
     * @return None
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, access the location
            } else {
                // Permission denied
            }
        }
    }

    /**
     * Called when map is ready to be used
     *
     * @param googleMap A non-null instance of a GoogleMap associated with the MapFragment.
     * @return None
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Adding current location markers and zoom in/out functionality
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        } else {
            // Request the missing location permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Called when lcoation of the user changes by a certain distance and time
     *
     * @param location current updated location of user
     * @return true or false
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(latLng,15);
        if (mMap == null) {
            return;
        }
        mMap.moveCamera(current);
        mMap.clear();
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            /**
             * Called when the query is able to execute, and get data from the database
             *
             * @param task Has a task object that has all the documents required
             * @return None
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        String owner = document.getString("owner");
                        //getting location data
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        if (geoPoint == null) {
                            continue;
                        }
                        double lat = geoPoint.getLatitude();
                        double lng = geoPoint.getLongitude();

                        Location markerLocation = new Location("");
                        markerLocation.setLatitude(lat);
                        markerLocation.setLongitude(lng);

                        double distance = 0.0;
                        try{
                            distance = location.distanceTo(markerLocation);
                            Log.d("distance ", String.valueOf(distance));
                        }catch(Exception e){
                            Log.e("error 2 mylocation", String.valueOf(myLocation));
                            Log.e("error 2 markerlocation", String.valueOf(markerLocation));
                        }


                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.custom_marker, (distance == 0.0) ? "-" : (String.valueOf(Math.round(distance)) + "m") )));
                        Marker locationMarker = mMap.addMarker(markerOptions);
                        locationMarker.setTag((String) document.getId());
                        locationMarker.showInfoWindow();

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                db.collection("CodeList").document((String) marker.getTag()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    /**
                     * Called when the query is able to execute, and get data from the database
                     *
                     * @param documentSnapshot Has a documentSnapshot object that has all the documents required
                     * @return None
                     */
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent = new Intent(getActivity(), QRProfileActivity.class);
                        intent.putExtra("DOC_ID", documentSnapshot.getId());
                        intent.putExtra("OWNER_NAME", (String) documentSnapshot.get("owner"));
                        startActivity(intent);
                    }
                });
                return false;
            }
        });
    }

    /**
     * Called to draw text on marker drawable of google map
     *
     * @param drawableId drawable id of the drawbel text needs to be written
     * @param text text that needs to be written
     * @return Bitmap
     */
    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = ResourcesCompat.getFont(mContext, R.font.rajdhani_bold);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(mContext, 11)*5);

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 2))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(mContext, 13));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (int) (canvas.getWidth() / 1.7) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) (canvas.getHeight() - (canvas.getHeight() / 1.295) ) ;

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }

    /**
     * Converts a value from Density-independent Pixels to Pixels according to the device's display density.
     *
     * @param context The context of the application.
     * @param nDP The value to convert, specified in Density-independent Pixels.
     * @return An integer value equivalent to the input value in Pixels.
     */
    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }
}
