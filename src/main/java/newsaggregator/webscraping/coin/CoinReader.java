package newsaggregator.webscraping.coin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.model.crypto.Coin;
import newsaggregator.webscraping.Scraper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoinReader extends Scraper<Coin> {

    private final Dotenv dotenv = Dotenv.load();

    @Override
    public void crawl() {
        System.out.println("\u001B[32m" + "Đang lấy dữ liệu từ CoinRanking API..." + "\u001B[0m");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode arrayNode = mapper.readTree(fetchCoins()).get("data").get("coins");
            if (arrayNode.isArray()) {
                List<Coin> coinList = new ArrayList<>();
                for (JsonNode coinNode : arrayNode) {
                    Coin currentCoin = new Coin();
                    // set guid
                    String guid = coinNode.get("uuid").textValue();
                    System.out.println(guid);
                    currentCoin.setGuid(guid);
                    // set type
                    currentCoin.setType("coin");
                    // set symbol
                    currentCoin.setSymbol(coinNode.get("symbol").textValue());
                    // set name
                    currentCoin.setName(coinNode.get("name").textValue());
                    // set market cap
                    currentCoin.setMarketCap(coinNode.get("marketCap").textValue());
                    // set rank
                    currentCoin.setRank(coinNode.get("rank").intValue());
                    // set btc price
                    currentCoin.setBtcPrice(Double.parseDouble(coinNode.get("btcPrice").textValue()) * 100);
                    // set thumbnail image
                    currentCoin.setThumbnailImage("https://assets.coincap.io/assets/icons/" +
                            coinNode.get("symbol").textValue().toLowerCase() + "@2x.png");
                    // get prices
                    JsonNode priceHistoryNode = mapper.readTree(fetchPriceHistory(guid));
                    JsonNode priceHistoryArrayNode = priceHistoryNode.get("data").get("history");
                    if (priceHistoryArrayNode.isArray()) {
                        List<AbstractMap.SimpleEntry<String, String>> prices = new ArrayList<>();
                        for (JsonNode priceNode : priceHistoryArrayNode) {
                            Long date = priceNode.get("timestamp").asLong();
                            String price = priceNode.get("price").textValue();
                            prices.add(new AbstractMap.SimpleEntry<>(convertDateToUTC(date), price));
                        }
                        currentCoin.setPrices(prices);
                    }

                    coinList.add(currentCoin);
                }
                setDataList(coinList);
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        System.out.println("\u001B[32m" + "Lấy dữ liệu từ CoinRanking API thành công!" + "\u001B[0m");
    }

    public String fetchCoins() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=100&offset=0")
                .get()
                .addHeader("X-RapidAPI-Key", dotenv.get("RAPID_API_KEY"))
                .addHeader("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String fetchPriceHistory(String guid) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://coinranking1.p.rapidapi.com/coin/" + guid + "/history?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=1y")
                .get()
                .addHeader("X-RapidAPI-Key", dotenv.get("RAPID_API_KEY"))
                .addHeader("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String convertDateToUTC(Long date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return outputFormat.format(new Date(date * 1000L));
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }
}
