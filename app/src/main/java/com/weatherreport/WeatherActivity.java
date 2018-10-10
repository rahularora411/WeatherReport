package com.weatherreport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity {
    public static String TAG = WeatherActivity.class.getSimpleName();

    @BindView(R.id.tvCityName)
    TextView tvCityName;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    private Handler handler;
    private ArrayList<WeatherPOJO> list;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.please_wait));
        progressBar.setCanceledOnTouchOutside(false);
        list = new ArrayList<>();
        handler = new Handler();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();
        updateWeatherData();
    }

    private void updateWeatherData() {
        progressBar.show();
        new Thread() {
            public void run() {

                final JSONObject json = NetworkCall.getJSON(WeatherActivity.this, "chandigarh");
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.dismiss();
                            Toast.makeText(WeatherActivity.this, getString(
                                    R.string.place_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void renderWeather(JSONObject json) {
        try {
            JSONObject jsonCity = json.getJSONObject("city");
            tvCityName.setText(jsonCity.getString("name").toUpperCase(Locale.US) + ", " + jsonCity.getString("country"));

            for (int i = 0; i < json.getJSONArray("list").length(); i++) {
                JSONObject details = json.getJSONArray("list").getJSONObject(i);
                String date = details.getString("dt_txt");

                JSONObject main = details.getJSONObject("main");
                String temp = main.getString("temp");
                String humidity = main.getString("humidity");
                String pressure = main.getString("pressure");

                JSONObject jsonArrayWeather = details.getJSONArray("weather").getJSONObject(0);
                String mains = jsonArrayWeather.getString("main");
                String description = jsonArrayWeather.getString("description");
                String icon = jsonArrayWeather.getString("icon");

                JSONObject wind = details.getJSONObject("wind");
                String speed = wind.getString("speed");
                String deg = wind.getString("deg");

                WeatherPOJO weatherPOJO = new WeatherPOJO();
                weatherPOJO.setCity(jsonCity.getString("name").toUpperCase(Locale.US) + ", " + jsonCity.getString("country"));
                weatherPOJO.setDate(date);
                weatherPOJO.setTemp(temp);
                weatherPOJO.setHumidity(humidity);
                weatherPOJO.setPressure(pressure);
                weatherPOJO.setMains(mains);
                weatherPOJO.setDescription(description);
                weatherPOJO.setSpeed(speed);
                weatherPOJO.setDeg(deg);
                weatherPOJO.setIcon(icon);
                list.add(weatherPOJO);
            }
        } catch (Exception e) {
            progressBar.dismiss();
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }

        setAdapter();
    }

    private void setAdapter() {
        WeatherAdapter adapter = new WeatherAdapter(this, list);
        recycleView.setAdapter(adapter);
        progressBar.dismiss();
    }
}