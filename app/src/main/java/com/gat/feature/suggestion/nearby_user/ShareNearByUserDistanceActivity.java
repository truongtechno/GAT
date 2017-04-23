package com.gat.feature.suggestion.nearby_user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.util.MZDebug;
import com.gat.feature.suggestion.CompareListUtil;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistanceActivity
        extends ScreenActivity<ShareNearByUserDistanceScreen, ShareNearByUserDistancePresenter>
        implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMarkerClickListener,
        IRecyclerViewItemClickListener{

    public static final String TAG = ShareNearByUserDistanceActivity.class.getSimpleName();
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 2000;
    private static final int DEFAULT_ZOOM = 15;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String PASS_LIST_USER_DISTANCE = "LIST_USERS";
    public static final String PASS_USER_LOCATION_LATITUDE = "USER_LOCATION_LATITUDE";
    public static final String PASS_USER_LOCATION_LONGITUDE = "USER_LOCATION_LONGITUDE";

    @BindView(R.id.rv_users_near)
    RecyclerView mRecyclerViewUsersNear;

    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;
    private List<UserNearByDistance> mListUserShareNearByDistance;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double mCurrentLongitude;
    private double mCurrentLatitude;
    private LatLng mCurrentLatLng;
    private LatLng mCenterLatLng;
    private LatLng mWSLatLng;
    private LatLng mNELatLng;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrentLocationMaker;
    private int mReasonMapMoved;
    private ShareNearByUserDistanceAdapter adapter;
    private List<UserNearByDistance> mListUsers;
    private List<Marker> mListMarker;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share_near_by_user_distance;
    }

    @Override
    protected ShareNearByUserDistanceScreen getDefaultScreen() {
        return ShareNearByUserDistanceScreen.instance();
    }

    @Override
    protected Class getPresenterClass() {
        return ShareNearByUserDistancePresenter.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // composite presenter
        disposables = new CompositeDisposable(
                getPresenter().onPeopleNearByUserSuccess().subscribe(this::onListUserNearComplete),
                getPresenter().onError().subscribe(this::onError)
        );

        progressDialog = new ProgressDialog(this);

        // setup google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get LIST USER, CURRENT LOCATION passed via bundle
//        Bundle bundle = getIntent().getExtras();
//        mListUserShareNearByDistance = bundle.getParcelableArrayList(PASS_LIST_USER_DISTANCE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @OnClick(R.id.tv_header)
    void onHeaderBackTap() {
        finish();
    }

    @OnClick(R.id.iv_back_to_current_location)
    void backToCurrentLocation() {
        moveCameraToNewLatLng(mCurrentLatLng);
    }

    private void checkToRequestPermission () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, "Location permission is required! Ops! Ops!",
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        MZDebug.w("_______________________USER ACCEPT PERMISSION_________________________________");
        processLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MZDebug.w("_______________________USER REFUSE PERMISSION_________________________________");
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_COARSE_LOCATION)
    private void processLocation() {
        checkToRequestPermission();
        // TODO process
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        checkToRequestPermission();
        MZDebug.i("_______________ON MAP READY____MOVE TO CURRENT LOCATION____________________");
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        buildGoogleApiClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MZDebug.i("_________________________________Google api client onConnected _______________");
        checkToRequestPermission();

        // get current location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mCurrentLatitude = mLastLocation.getLatitude();
            mCurrentLongitude = mLastLocation.getLongitude();
            MZDebug.w("______Current: lat = " + mCurrentLatitude + ", long= "  + mCurrentLongitude);

            // add current location = marker icon
            mCurrentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mCurrentLatLng).title("You're here").icon(icon);
            mCurrentLocationMaker = mMap.addMarker(markerOptions);
            // move camera
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, DEFAULT_ZOOM);
            mMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mGoogleApiClient.connect();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE;
            MZDebug.w("______________________The user gestured on the map._______________________");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION;
            MZDebug.w("______________________The user TAPPED SOMETHING on the map._______________");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION;
            MZDebug.w("_________________________________developer move the camera _______________");
        }

    }

    @Override
    public void onCameraIdle() {

        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        mCenterLatLng = new LatLng(curScreen.getCenter().latitude, curScreen.getCenter().longitude);
        mWSLatLng = new LatLng(curScreen.southwest.latitude, curScreen.southwest.longitude);
        mNELatLng = new LatLng(curScreen.northeast.latitude, curScreen.northeast.longitude);

        switch (mReasonMapMoved) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE: {
                MZDebug.w("_____________The camera has stopped moving BY USER__ UPDATE DATA______");
                // kiểm tra nếu (top-left bottom-right) vẫn nằm trong khung map mà suggest fragment pass sang
                // thì không request lấy thêm làm gì cả
                // nếu khung màn hình map user kéo ra ngoài, thì mới request lấy list user near
                getPresenter().requestUserNearOnTheMap(mCenterLatLng, mNELatLng, mWSLatLng);

                break;
            }
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION: {
                MZDebug.w("___________________________The camera has stopped moving BY DEVELOPER_");

                MZDebug.w("southwest: lat= " + curScreen.southwest.latitude
                        + ", long= " + curScreen.southwest.longitude);
                MZDebug.w("northeast: lat= " + curScreen.northeast.latitude
                        + ", long= " +curScreen.northeast.longitude );
                getPresenter().requestUserNearOnTheMap(mCenterLatLng, mNELatLng, mWSLatLng);
                break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mCurrentLocationMaker)) {
            MZDebug.w("_________________ User tapped on current location ");
            return true;
        }

        UserNearByDistance user = (UserNearByDistance)marker.getTag();
        Toast.makeText(getApplicationContext(),user.getUserId() + " : " + user.getAddress(),
                Toast.LENGTH_SHORT).show();

        return true;
    }

    private void onListUserNearComplete(List<UserNearByDistance> list) {
        MZDebug.i("________________onListUserNearComplete________________________________________");
        if (adapter == null) {
            mListUsers = list;

            adapter = new ShareNearByUserDistanceAdapter(getApplicationContext(), mListUsers, this);
            mRecyclerViewUsersNear.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerViewUsersNear.setAdapter(adapter);

            mListMarker = addListMarker(mListUsers);
            return;
        }

        // so sánh mListUsers và list
        // nếu user nào ở list mà có trong mListUsers thì ko thêm
        // nếu user nào ở list mà không có trong mListUser thì thêm vào mListUsers để add vào map
        // update những user thêm được vào map
        List<UserNearByDistance> listDestination = CompareListUtil.destinationListUserNear(mListUsers, list);
        MZDebug.w("List destination size = " + listDestination.size());
        adapter.addListItems(listDestination);

        mListMarker.addAll(addListMarker(listDestination));
    }

    // request list user near failed
    private void onError (String message) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }


    private List<Marker> addListMarker (List<UserNearByDistance> listUsers) {
        List<Marker> listMarkers = new ArrayList<>();

        UserNearByDistance user;
        for (int i=(listUsers.size()-1); i>=0; i--) {
            user = listUsers.get(i);
            Marker marker = addMarker(new LatLng(user.getLatitude(), user.getLongitude()), user);
            listMarkers.add(marker);
        }
        return listMarkers;
    }

    private Marker addMarker (LatLng latLng, UserNearByDistance user) {
        // add icon type book stop
        // add icon type user share book
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_near);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(user.getName())
                .icon(icon);
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(user);

        return marker;
    }

    private void moveCameraToNewLocation(Location location) {
        Log.d(TAG, location.toString());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.animateCamera(cameraUpdate);
    }

    private void moveCameraToNewLatLng(LatLng latLng) {
        if (null != latLng) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onItemClickListener(View v, int position) {
        moveCameraToNewLatLng(new LatLng(mListUsers.get(position).getLatitude(),
                mListUsers.get(position).getLongitude()));
        Toast.makeText(this, "user id: " + mListUsers.get(position).getUserId(), Toast.LENGTH_SHORT).show();
    }
}