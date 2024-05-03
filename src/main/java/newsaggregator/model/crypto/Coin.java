package newsaggregator.model.crypto;

import newsaggregator.model.BaseModel;

import java.util.AbstractMap;
import java.util.List;

public class Coin extends BaseModel {

    // Attributes

    private String symbol;
    private String name;
    private String marketCap;
    private int rank;
    private Double btcPrice;
    private String thumbnail_image;
    private List<AbstractMap.SimpleEntry<String, String>> prices;

    // Constructors

    public Coin() {
    }

    // Methods

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Double getBtcPrice() {
        return btcPrice;
    }

    public void setBtcPrice(Double btcPrice) {
        this.btcPrice = btcPrice;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getPrices() {
        return prices;
    }

    public void setPrices(List<AbstractMap.SimpleEntry<String, String>> prices) {
        this.prices = prices;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Symbol: " + symbol);
        System.out.println("Name: " + name);
        System.out.println("Market cap: " + marketCap);
        System.out.println("Rank: " + rank);
        System.out.println("Compare to BTC: " + btcPrice + "%");
        System.out.println("Thumbnail image: " + thumbnail_image);
        System.out.println("Prices: ");
        for (AbstractMap.SimpleEntry<String, String> price : prices) {
            System.out.println("Date: " + price.getKey() + " - Price: $" + price.getValue());
        }
        System.out.println("==================================================================");


    }
}
