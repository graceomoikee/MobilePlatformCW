package org.me.gcu.omoike_grace_s2125456.repository;



import android.util.Log;

import org.me.gcu.omoike_grace_s2125456.model.CurrencyItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CurrencyRepository {

    private static final String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";

    public ArrayList<CurrencyItem> fetchAndParseData() {
        ArrayList<CurrencyItem> currencyList = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        try {
            URL aurl = new URL(urlSource);
            URLConnection yc = aurl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();

            currencyList = parseXML(result.toString());

        } catch (Exception e) {
            Log.e("Repository", "Error fetching data: " + e.getMessage());
        }

        return currencyList;
    }

    private ArrayList<CurrencyItem> parseXML(String xmlData) {
        ArrayList<CurrencyItem> currencyList = new ArrayList<>();
        CurrencyItem currentItem = null;
        String currentTag = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentTag = xpp.getName();
                    if ("item".equals(currentTag)) {
                        currentItem = new CurrencyItem();
                    }
                } else if (eventType == XmlPullParser.TEXT && currentItem != null && currentTag != null) {
                    String text = xpp.getText();
                    switch (currentTag) {
                        case "title":
                            currentItem.setTitle(text);
                            Log.d("RSS_TITLE", "Title: " + text);
                            break;
                        case "description":
                            currentItem.setDescription(text);
                            if (text.contains("=")) {
                                String[] parts = text.split("=");
                                if (parts.length > 1) {
                                    String ratePart = parts[1].trim().split(" ")[0];
                                    try {
                                        currentItem.setExchangeRate(Double.parseDouble(ratePart));
                                    } catch (NumberFormatException e) {
                                        Log.e("Parsing", "Rate parse error: " + e.getMessage());
                                    }
                                }
                            }
                            break;
                        case "pubDate":
                            currentItem.setPubDate(text);
                            break;
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("item".equals(xpp.getName()) && currentItem != null) {
                        if (currentItem.getTitle() != null && currentItem.getTitle().contains("/")) {
                            String[] titleParts = currentItem.getTitle().split("/");
                            currentItem.setTargetCurrencyCode(titleParts[1].trim());
                        }
                        currencyList.add(currentItem);
                        currentItem = null;
                    }
                    currentTag = null;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e("Parsing", "Error during parsing: " + e.getMessage());
        }

        return currencyList;
    }
}
