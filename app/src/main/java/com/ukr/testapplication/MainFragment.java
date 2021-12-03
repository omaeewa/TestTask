package com.ukr.testapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ukr.testapplication.databinding.FragmentMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private static ArrayList<String> allIds;
    private static int curNum = 0;

    private static final String requestA = "http://demo3005513.mockable.io/api/v1/entities/getAllIds";
    private static final String requestB = "http://demo3005513.mockable.io/api/v1/object/";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        allIds = new ArrayList<>();

        requestApi(requestA, "a");

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curNum==allIds.size()){
                    curNum=0;
                }
                requestApi(requestB+allIds.get(curNum), "b");
            }
        });

        return binding.getRoot();
    }

    private void requestApi(String url, String type){

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(type.equals("a")){
                            getDataJson(response);
                            requestApi(requestB+allIds.get(curNum), "b");
                        }
                        if(type.equals("b")){
                            try {
                                String value = null;
                                if(response.getString("type").equals("text")){
                                    value = response.getString("message");
                                }else if(response.getString("type").equals("webview")||response.getString("type").equals("image")){
                                    value = response.getString("url");
                                }else if(response.getString("type").equals("game")){
                                    Toast.makeText(getContext(), "Ignore", Toast.LENGTH_SHORT).show();
                                }
                                if(value!=null){
                                    changeView(response.getString("type"),value);
                                }
                                curNum++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MainActivity", error.toString());
                    }
                }
        );
        requestQueue.add(objectRequest);
    }

    private void getDataJson(JSONObject response){
        JSONArray m_jArry = null;
        try {
            m_jArry = response.getJSONArray("data");
            for(int i = 0;i<m_jArry.length();i++){
                JSONObject item = m_jArry.getJSONObject(i);
                allIds.add(item.getString("id")+"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeView(String type, String value){
        hideAll();
        switch (type){
            case "text":
                binding.textView.setVisibility(View.VISIBLE);
                binding.textView.setText(value);
                return;
            case "webview":
                binding.webView.setVisibility(View.VISIBLE);
                binding.webView.loadUrl(value);
                return;
            case "image":
                binding.imageView.setVisibility(View.VISIBLE);
                Glide
                        .with(getContext())
                        .load(value)
                        .into(binding.imageView);
        }
    }

    private void hideAll(){
        binding.textView.setVisibility(View.GONE);
        binding.webView.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
    }

}