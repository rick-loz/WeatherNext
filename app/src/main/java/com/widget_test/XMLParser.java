package com.widget_test;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ricky on 01/04/2017.
 */

public class XMLParser {

    private static final String ns = null;

    public Time[] parse(String data) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(new StringReader(data));
            parser.nextTag();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return readForecasts(parser);
        }
    }

    private Time[] readForecasts(XmlPullParser parser) throws XmlPullParserException, IOException {
        Time forecasts[] = new Time[2];


        parser.require(XmlPullParser.START_TAG, ns, "weatherdata");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag


            if (name.equals("forecast")) {
                parser.require(XmlPullParser.START_TAG, ns, "forecast");
                for (int i = 0; i < 2 && parser.next() != XmlPullParser.END_TAG; i++) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name2 = parser.getName();
                    if (name2.equals("time")) {
                        forecasts[i] = readTime(parser);
                    }

                }
            } else {
                skip(parser);
            }
        }
        return forecasts;
    }

    public static class Time {
        public final String from;
        public final String to;
        public final double temperature;
        public final int weatherCondition;

        private Time(String from, String to, double temperature, int weatherCondition) {
            this.from = from;
            this.to = to;
            this.temperature = temperature;
            this.weatherCondition = weatherCondition;
        }

        public String getInformation()
        {
            return "The temperature from: " + from + " to: " + to + " is: " + temperature + "\n\n";
        }


    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Time readTime(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "time");
        String from = parser.getAttributeValue(null, "from");
        String to = parser.getAttributeValue(null, "to");
        double temperature = 0;
        int weatherCondition = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("temperature")) {
                temperature = readTemperature(parser);
            }
            else if(name.equals("symbol"))
            {
                weatherCondition = readWeatherCondition(parser);
            }
            else
            {
                skip(parser);
            }
        }
        return new Time(from, to, temperature, weatherCondition);
    }


    // Processes link tags in the feed.
    private double readTemperature(XmlPullParser parser) throws IOException, XmlPullParserException {
        double temperature = 0;
        parser.require(XmlPullParser.START_TAG, ns, "temperature");
        String tag = parser.getName();
        String tempValue = parser.getAttributeValue(null, "value");

        temperature = Double.parseDouble(tempValue) - 273.15;

        if (tag.equals("temperature")) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "temperature");
        return temperature;
    }

    private int readWeatherCondition(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        int condition = 0;
        parser.require(XmlPullParser.START_TAG, ns, "symbol");
        String tag = parser.getName();
        String condValue = parser.getAttributeValue(null, "number");

        int attrValue = Integer.parseInt(condValue);

        if(attrValue >= 800 && attrValue <= 804) //Clear
        {
            condition = 0;
        }
        else if(attrValue >= 500 && attrValue <= 531)//lluvia
        {
            condition = 1;
        }
        else if(attrValue >= 200 && attrValue <= 232)//tormenta electrica
        {
            condition = 2;
        }
        else if(attrValue >= 600 && attrValue <= 622)//nieve
        {
            condition = 3;
        }
        else //cualquier otra cosa
        {
            condition = 4;
        }

        if (tag.equals("symbol")) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "symbol");

        return condition;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
