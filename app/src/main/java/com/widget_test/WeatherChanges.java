package com.widget_test;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricky on 01/04/2017.
 */

public class WeatherChanges {

    private XMLParser.Time times[];

    boolean rain = false;
    boolean snow = false;
    boolean thunderstorm = false;
    boolean clear = false;



    WeatherChanges(String data) throws IOException, XmlPullParserException {
        XMLParser xmlParser = new XMLParser();

        times = xmlParser.parse(data);

    }

    public double getCurrentTemperature()
    {
        DecimalFormat df = new DecimalFormat("00.0");
        return Double.parseDouble(df.format(times[0].temperature));
    }
    public double temperatureChange()
    {
        double change = times[1].temperature - times[0].temperature;

        DecimalFormat df = new DecimalFormat("00.0");
        return Double.parseDouble(df.format(change));
    }

    public int extremeTemperatureChange()
    {
        double change = temperatureChange();

        if(change > 5)
        {
            return 1;
        }
        else if(change < -5)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    public int getWeatherChange()
    {
        if(times[0].weatherCondition != times[1].weatherCondition)
        {
            return times[1].weatherCondition;
        }
        return -1; //no hubo cambio
    }
    public int getCurrentWeather(){
        return times[0].weatherCondition;
    }
}