package com.weatherreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.weatherreport.Constant.ROOT_IMAGE;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {

    private List<WeatherPOJO> list;
    private Context ctx;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMain)
        TextView tvMain;

        @BindView(R.id.tvDes)
        TextView tvDes;

        @BindView(R.id.tvTemp)
        TextView tvTemp;

        @BindView(R.id.tvHumidity)
        TextView tvHumidity;

        @BindView(R.id.tvPressure)
        TextView tvPressure;

        @BindView(R.id.tvDate)
        TextView tvDate;

        @BindView(R.id.imgView)
        ImageView imgView;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    WeatherAdapter(Context ctx, List<WeatherPOJO> list) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tvMain.setText(list.get(position).getMains());
        holder.tvDes.setText(list.get(position).getDescription().toUpperCase(Locale.US));
        holder.tvTemp.setText("Temp: " + String.format("%.0f", c2f(Double.parseDouble(list.get(position).getTemp()))) + (char) 0x00B0 + "F");
        holder.tvHumidity.setText("Humidity: " + list.get(position).getHumidity() + "%");
        holder.tvPressure.setText("Pressure: " + list.get(position).getPressure() + " hPa");
        holder.tvDate.setText("Last update: " + convertDateFormat(list.get(position).getDate()));
        Glide.with(ctx).load(ROOT_IMAGE + list.get(position).getIcon() + ".png")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Celcius to Fahrenhiet method
    private double c2f(double c) {
        return (c * 9) / 5 + 32;
    }

    @SuppressLint("SimpleDateFormat")
    private String convertDateFormat(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        return spf.format(newDate);
    }
}