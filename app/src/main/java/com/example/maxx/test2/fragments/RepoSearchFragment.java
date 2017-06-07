package com.example.maxx.test2.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maxx.test2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * My  {@link Fragment} subclass.
 */
public class RepoSearchFragment extends Fragment implements View.OnClickListener {

    private RetainedFragment dataFragment;

    List<String> items = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    public RepoSearchFragment() {
        // Required empty public constructor
    }

    public static RepoSearchFragment newInstance (){
        RepoSearchFragment fragment = new RepoSearchFragment();

        Bundle args = new Bundle();
        args.putString("search_type", "repositories");
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);

        Button searchButton = (Button) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            // load the data from the web
            dataFragment.setData(items);
        } else {
            items = dataFragment.getData();
        }



        return rootView;
    }

    public void onClick(View view){
        EditText ed = (EditText) getActivity().findViewById(R.id.editText);
        if (!ed.getText().toString().equals("")) {
            Toast t = Toast.makeText(getContext(), ed.getText().toString(), Toast.LENGTH_SHORT);
            t.show();
            try {
                new RequestAsyncTask().execute(
                        new URL("https://api.github.com/search/"
                                + this.getArguments().getString("search_type")
                                + "?q=" + ed.getText().toString() + "&per_page=100"));
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Repositories search");

        getActivity().findViewById(R.id.usr_switch).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.rep_switch).setVisibility(View.INVISIBLE);

        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, items);

        ListView list = (ListView) getActivity().findViewById(R.id.data_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                String url = "https://github.com/" + ((TextView)itemClicked).getText().toString();

                Intent openUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openUrl);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        dataFragment.setData(items);
    }

    @Override
    public void onStop() {
        super.onStop();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        getActivity().findViewById(R.id.usr_switch).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.rep_switch).setVisibility(View.VISIBLE);
    }

    private class RequestAsyncTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            String response = "";
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) params[0].openConnection();
                if (urlConnection.getHeaderField("Status").equals("200 OK")) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine);
                    }

                    response = buffer.toString();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(String input) {
            if (!input.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(input);
                    JSONArray receivedItems = jsonObject.getJSONArray("items");
                    System.out.println(receivedItems.length());
                    items.clear();
                    for (int i = 0; i < receivedItems.length(); i++) {
                        items.add(receivedItems.getJSONObject(i).getString("full_name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (items != null) {
                ListView list = (ListView) getActivity().findViewById(R.id.data_list);
                list.setAdapter(adapter);
            }
        }

    }

}
