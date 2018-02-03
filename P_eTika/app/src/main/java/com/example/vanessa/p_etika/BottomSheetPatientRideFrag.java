package com.example.vanessa.p_etika;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vanessa.p_etika.common.Common;
import com.example.vanessa.p_etika.remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vanessa on 14/11/2017.
 */

public class BottomSheetPatientRideFrag extends BottomSheetDialogFragment{

    String mLocation, mDestination;
    boolean isTapOnMap;
    String mDiseases;
    String mCost;
    IGoogleAPI mService;
    TextView txtCostCalc, txtloc, txtdest, txtDiseases;

    ArrayAdapter<String> adapter;

    public final static String EXTRA_COST = "EXTRA_COST";
    public final int REQUEST_RESPONSE = 1; //chnge from pub fin to fin only

    public static  BottomSheetPatientRideFrag newInstance(String location, String destination, boolean isTapOnMap, String mDiseases) {
        BottomSheetPatientRideFrag bsp = new BottomSheetPatientRideFrag();
        //pass bottomsheet notification to another activity/frag

        Bundle args = new Bundle();
        args.putString("location", location);
        args.putString("destination", destination);
        args.putBoolean("isTapOnMap", isTapOnMap);
        args.putString("diseases", mDiseases);
        bsp.setArguments(args);
        return bsp;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mDiseases = getActivity().getIntent().getStringExtra(DiseasesActivity.EXTRA_DISEASE);

        mLocation = getArguments().getString("location");
        mDestination = getArguments().getString("destination");
        isTapOnMap = getArguments().getBoolean("isTapOnMap");
        mDiseases = getArguments().getString("diseases");
        mCost = getArguments().getString("serviceCost");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_patientride, container, false);
        txtloc = (TextView)view.findViewById(R.id.txtLocation);
        txtdest = (TextView)view.findViewById(R.id.txtDestination);
        txtCostCalc = (TextView)view.findViewById(R.id.txtCostCalculation);
        txtDiseases = (TextView)view.findViewById(R.id.txtPatientAilment);

        mService = Common.getGoogleService();

        mDiseases = getActivity().getIntent().getStringExtra(DiseasesActivity.EXTRA_DISEASE);
        String j = mDiseases;

        getPrice(mLocation, mDestination);

//        set location & destination data
        if (!isTapOnMap) {
//            call frag placeautocomplete....
            txtloc.setText(mLocation);
            txtdest.setText(mDestination);
            txtDiseases.setText(j);
        }
        return view;
    }

    private void getPrice(String mLocation, String mDestination) {
        String requestUrl = null;

        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    +"transit_routing_preference=less_driving&"
                    +"origin="+mLocation+"&"+"destination="+mDestination+"&"
                    +"key="+getResources().getString(R.string.google_browser_key);

            Log.e("LINK", requestUrl);
            mService.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
//                    get object
                    try {
                        Log.d("check", response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");

                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");

                        JSONObject legsObject = legs.getJSONObject(0);

//                        obtin distance
                        JSONObject distance = legsObject.getJSONObject("distance");
                        String distance_txt = distance.getString("text");
//                        use regex to extract double from string
                        Double distance_val = Double.parseDouble(distance_txt.replaceAll("[^0-9\\\\.]+", ""));

//                        obtain time
                        JSONObject time = legsObject.getJSONObject("duration");
                        String time_txt = distance.getString("text");
//                        use regex to extract double from string
                        Integer time_val = Integer.parseInt(distance_txt.replaceAll("\\D+", ""));

                        String final_calculation = String.format("%s + %s = $%.2f", distance_txt, time_txt,
                                                   Common.getPrice(distance_val, time_val));

                        txtCostCalc.setText(final_calculation);
                        final_calculation = mCost;

                        if (isTapOnMap) {
                            String start_address = legsObject.getString("start_address");
                            String end_address = legsObject.getString("end_address");

                            txtloc.setText(start_address);
                            txtdest.setText(end_address);
                            txtDiseases.setText(mDiseases);
                        }

                        Intent intent = new Intent(getActivity(), Payment.class);
                        intent.putExtra(EXTRA_COST, "serviceCost");
//                        startActivityForResult(intent, REQUEST_RESPONSE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("error", t.getMessage());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
