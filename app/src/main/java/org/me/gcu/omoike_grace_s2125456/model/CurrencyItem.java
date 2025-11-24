package org.me.gcu.omoike_grace_s2125456.model;

public class CurrencyItem {
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private double exchangeRate;
    private String targetCurrencyCode; // e.g. "USD"
    private String countryFlagUrl;     // optional flag icon
    private String countryName;        // e.g. "China"
    private String currencyName;       // e.g. "Yuan"

    // --- Getters and Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getPubDate() { return pubDate; }
    public void setPubDate(String pubDate) { this.pubDate = pubDate; }

    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }

    public String getTargetCurrencyCode() { return targetCurrencyCode; }
    public void setTargetCurrencyCode(String targetCurrencyCode) { this.targetCurrencyCode = targetCurrencyCode; }

    public String getCountryFlagUrl() { return countryFlagUrl; }
    public void setCountryFlagUrl(String countryFlagUrl) { this.countryFlagUrl = countryFlagUrl; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }



    @Override
    public String toString() {
        return "CurrencyItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", targetCurrencyCode='" + targetCurrencyCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", currencyName='" + currencyName + '\'' +
                '}';
    }
}
