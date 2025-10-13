package org.me.gcu.omoike_grace_s2125456.model;


public class CurrencyItem {
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private double exchangeRate;
    private String targetCurrencyCode; // e.g., "USD"
    private String countryFlagUrl; // Optional: for flag icons

    // Getters and Setters for all fields

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public String getCountryFlagUrl() {
        return countryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        this.countryFlagUrl = countryFlagUrl;
    }

    // A helpful toString() method for debugging
    @Override
    public String toString() {
        return "CurrencyItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", targetCurrencyCode='" + targetCurrencyCode + '\'' +
                '}';
    }
}
