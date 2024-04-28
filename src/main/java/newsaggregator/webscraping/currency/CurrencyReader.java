package newsaggregator.webscraping.currency;

import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.model.currency.Coin;
import newsaggregator.webscraping.Scraper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyReader extends Scraper<Coin> {
    @Override
    public void crawl() {
        Dotenv dotenv = Dotenv.load();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=1y&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0")
                .get()
                .addHeader("X-RapidAPI-Key", dotenv.get("RAPID_API_KEY"))
                .addHeader("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
                .build();

        try(Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        CurrencyReader reader = new CurrencyReader();
        reader.crawl();
    }
}
