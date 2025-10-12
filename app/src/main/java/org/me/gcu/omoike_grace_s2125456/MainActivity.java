/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Omoike Grace
// Student ID           S2125456
// Programme of Study   BSc Computing
//

/*package org.me.gcu.omoike_grace_s2125456;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.EditText;
import android.widget.ViewSwitcher;
import android.widget.LinearLayout;



public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView rawDataDisplay;
    private Button startButton;
    private String result = "";
    private String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";
    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private CurrencyAdapter adapter;
    private android.widget.ListView listView;
    private ViewSwitcher viewSwitcher;
    private double selectedRate = 0.0;
    private String selectedCode = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewSwitcher = findViewById(R.id.viewSwitcher);




        // Set up the graphical components
        //rawDataDisplay = findViewById(R.id.rawDataDisplay);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        listView = findViewById(R.id.listView);
        adapter = new CurrencyAdapter(this, currencyList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyItem selectedCurrency = (CurrencyItem) adapter.getItem(position);
            selectedCode = selectedCurrency.getTargetCurrencyCode();
            selectedRate = selectedCurrency.getExchangeRate();

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            // Switch to the conversion screen
            viewSwitcher.showNext();
        });

        LinearLayout usdCard = findViewById(R.id.usdCard);
        LinearLayout eurCard = findViewById(R.id.eurCard);
        LinearLayout jpyCard = findViewById(R.id.jpyCard);

        View.OnClickListener summaryClickListener = v -> {
            String code = "";
            double rate = 0.0;

            int viewId = v.getId(); // Get the ID of the clicked view

            if (viewId == R.id.usdCard) {
                code = "USD";
            } else if (viewId == R.id.eurCard) {
                code = "EUR";
            } else if (viewId == R.id.jpyCard) {
                code = "JPY";
            }

            for (CurrencyItem item : currencyList) {
                if (item.getTargetCurrencyCode().equals(code)) {
                    rate = item.getExchangeRate();
                    break;
                }
            }

            selectedCode = code;
            selectedRate = rate;

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            viewSwitcher.showNext();
        };

        usdCard.setOnClickListener(summaryClickListener);
        eurCard.setOnClickListener(summaryClickListener);
        jpyCard.setOnClickListener(summaryClickListener);


        Button convertButton = findViewById(R.id.convertButton);
        Button reverseButton = findViewById(R.id.reverseButton);
        Button backButton = findViewById(R.id.backButton);
        EditText gbpInput = findViewById(R.id.gbpInput);
        TextView resultText = findViewById(R.id.resultText);

// Convert GBP → selected currency
        convertButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double gbpValue = Double.parseDouble(input);
                double result = gbpValue * selectedRate;
                resultText.setText(String.format("%.2f GBP = %.2f %s", gbpValue, result, selectedCode));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

// Reverse conversion (selected currency → GBP)
        reverseButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double foreignValue = Double.parseDouble(input);
                double result = foreignValue / selectedRate;
                resultText.setText(String.format("%.2f %s = %.2f GBP", foreignValue, selectedCode, result));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

// Back button → go back to list
        backButton.setOnClickListener(v -> viewSwitcher.showPrevious());


        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    @Override
    public void onClick(View aview) {
        startProgress();
    }

    public void startProgress() {
        // Improved threading approach using ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Task(urlSource));
        executor.shutdown();
    }

    private class Task implements Runnable {
        private final String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            Log.d("MyTask", "Thread started");

            try {
                Log.d("MyTask", "Downloading data from: " + url);
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTask", "IOException: " + ae.getMessage());
            }

            // --- Clean up XML ---
            int i = result.indexOf("<?");
            if (i >= 0) result = result.substring(i);
            i = result.indexOf("</rss>");
            if (i > 0) result = result.substring(0, i + 6);

            // --- Parse XML (no new variable!) ---
            ArrayList<CurrencyItem> parsedCurrencies = parseXML(result);
            Log.d("DEBUG", "Parsed " + parsedCurrencies.size() + " items.");


            MainActivity.this.runOnUiThread(() -> {
                currencyList.clear();
                currencyList.addAll(parsedCurrencies);
                adapter.updateData(parsedCurrencies);   // new helper method resets filter too

                // --- Update top 3 currencies ---
                TextView usdRateView = findViewById(R.id.usdRate);
                TextView eurRateView = findViewById(R.id.eurRate);
                TextView jpyRateView = findViewById(R.id.jpyRate);

                for (CurrencyItem item : currencyList) {
                    String code = item.getTargetCurrencyCode();
                    if (code == null) continue;

                    switch (code) {
                        case "USD":
                            usdRateView.setText(String.format("%.4f", item.getExchangeRate()));
                            break;
                        case "EUR":
                            eurRateView.setText(String.format("%.4f", item.getExchangeRate()));
                            break;
                        case "JPY":
                            jpyRateView.setText(String.format("%.2f", item.getExchangeRate()));
                            break;
                    }
                }

            });


        }
    }


    // --- XML PullParser method (modular, for architecture marks) ---
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
                } else if (eventType == XmlPullParser.TEXT) {
                    if (currentItem != null && currentTag != null) {
                        String text = xpp.getText();

                        switch (currentTag) {
                            case "title":
                                currentItem.setTitle(text);
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
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("item".equals(xpp.getName()) && currentItem != null) {
                        // Extract target currency code (e.g., GBP/USD → USD)
                        if (currentItem.getTitle() != null && currentItem.getTitle().contains("/")) {
                            String[] titleParts = currentItem.getTitle().split("/");
                            if (titleParts.length > 1) {
                                currentItem.setTargetCurrencyCode(titleParts[1].trim());
                                Log.d("ParsedCode", "Currency code: " + currentItem.getTargetCurrencyCode());

                            }
                        }
                        currencyList.add(currentItem);
                        Log.d("ParserItem", currentItem.toString());
                        currentItem = null;
                    }
                    currentTag = null;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            Log.e("Parsing", "Error during parsing: " + e.getMessage());
        }

        Log.d("Parsing", "Parsed " + currencyList.size() + " currency items.");
        return currencyList;
    }
}*/

/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Omoike Grace
// Student ID           S2125456
// Programme of Study   BSc Computing
//

package org.me.gcu.omoike_grace_s2125456;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.EditText;
import android.widget.ViewSwitcher;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button startButton;
    private String result = "";
    private String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";
    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private CurrencyAdapter adapter;
    private android.widget.ListView listView;
    private ViewSwitcher viewSwitcher;
    private double selectedRate = 0.0;
    private String selectedCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewSwitcher = findViewById(R.id.viewSwitcher);

        // Set up the graphical components
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        listView = findViewById(R.id.listView);
        adapter = new CurrencyAdapter(this, currencyList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyItem selectedCurrency = (CurrencyItem) adapter.getItem(position);
            selectedCode = selectedCurrency.getTargetCurrencyCode();
            selectedRate = selectedCurrency.getExchangeRate();

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            // Switch to the conversion screen
            viewSwitcher.showNext();
        });

        LinearLayout usdCard = findViewById(R.id.usdCard);
        LinearLayout eurCard = findViewById(R.id.eurCard);
        LinearLayout jpyCard = findViewById(R.id.jpyCard);

        View.OnClickListener summaryClickListener = v -> {
            String code = "";
            double rate = 0.0;

            int viewId = v.getId(); // Get the ID of the clicked view
            if (viewId == R.id.usdCard) {
                code = "(USD)";
            } else if (viewId == R.id.eurCard) {
                code = "(EUR)";
            } else if (viewId == R.id.jpyCard) {
                code = "(JPY)";
            }

            for (CurrencyItem item : currencyList) {
                if (item.getTargetCurrencyCode() != null && item.getTargetCurrencyCode().contains(code)) {
                    rate = item.getExchangeRate();
                    selectedCode = item.getTargetCurrencyCode(); // Full text e.g. "US Dollar(USD)"
                    break;
                }
            }

            selectedRate = rate;

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            viewSwitcher.showNext();
        };

        usdCard.setOnClickListener(summaryClickListener);
        eurCard.setOnClickListener(summaryClickListener);
        jpyCard.setOnClickListener(summaryClickListener);

        // Conversion UI
        Button convertButton = findViewById(R.id.convertButton);
        Button reverseButton = findViewById(R.id.reverseButton);
        Button backButton = findViewById(R.id.backButton);
        EditText gbpInput = findViewById(R.id.gbpInput);
        TextView resultText = findViewById(R.id.resultText);

        // Convert GBP → selected currency
        convertButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double gbpValue = Double.parseDouble(input);
                double result = gbpValue * selectedRate;
                resultText.setText(String.format("%.2f GBP = %.2f %s", gbpValue, result, selectedCode));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

        // Reverse conversion (selected currency → GBP)
        reverseButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double foreignValue = Double.parseDouble(input);
                double result = foreignValue / selectedRate;
                resultText.setText(String.format("%.2f %s = %.2f GBP", foreignValue, selectedCode, result));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

        // Back button → go back to list
        backButton.setOnClickListener(v -> viewSwitcher.showPrevious());

        // Search filter
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onClick(View aview) {
        startProgress();
    }

    public void startProgress() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Task(urlSource));
        executor.shutdown();
    }

    private class Task implements Runnable {
        private final String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in;
            String inputLine;
            Log.d("MyTask", "Thread started");

            try {
                Log.d("MyTask", "Downloading data from: " + url);
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTask", "IOException: " + ae.getMessage());
            }

            int i = result.indexOf("<?");
            if (i >= 0) result = result.substring(i);
            i = result.indexOf("</rss>");
            if (i > 0) result = result.substring(0, i + 6);

            ArrayList<CurrencyItem> parsedCurrencies = parseXML(result);
            Log.d("DEBUG", "Parsed " + parsedCurrencies.size() + " items.");

            MainActivity.this.runOnUiThread(() -> {
                currencyList.clear();
                currencyList.addAll(parsedCurrencies);
                adapter.updateData(parsedCurrencies);

                // Update top 3 summary cards
                TextView usdRateView = findViewById(R.id.usdRate);
                TextView eurRateView = findViewById(R.id.eurRate);
                TextView jpyRateView = findViewById(R.id.jpyRate);

                for (CurrencyItem item : currencyList) {
                    String code = item.getTargetCurrencyCode();
                    if (code == null) continue;

                    if (code.contains("(USD)")) {
                        usdRateView.setText(String.format("%.4f", item.getExchangeRate()));
                    } else if (code.contains("(EUR)")) {
                        eurRateView.setText(String.format("%.4f", item.getExchangeRate()));
                    } else if (code.contains("(JPY)")) {
                        jpyRateView.setText(String.format("%.2f", item.getExchangeRate()));
                    }
                }
            });
        }
    }

    // --- XML PullParser method (modular) ---
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
                } else if (eventType == XmlPullParser.TEXT) {
                    if (currentItem != null && currentTag != null) {
                        String text = xpp.getText();
                        switch (currentTag) {
                            case "title":
                                currentItem.setTitle(text);
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
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("item".equals(xpp.getName()) && currentItem != null) {
                        if (currentItem.getTitle() != null && currentItem.getTitle().contains("/")) {
                            String[] titleParts = currentItem.getTitle().split("/");
                            if (titleParts.length > 1) {
                                currentItem.setTargetCurrencyCode(titleParts[1].trim()); // Keep "US Dollar(USD)"
                                Log.d("ParsedCode", "Currency code: " + currentItem.getTargetCurrencyCode());
                            }
                        }
                        currencyList.add(currentItem);
                        currentItem = null;
                    }
                    currentTag = null;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            Log.e("Parsing", "Error during parsing: " + e.getMessage());
        }

        Log.d("Parsing", "Parsed " + currencyList.size() + " currency items.");
        return currencyList;
    }
}

