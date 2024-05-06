package newsaggregator.webscraping.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import newsaggregator.model.content.Post;
import newsaggregator.webscraping.Scraper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedditReader extends Scraper<Post> {
    @Override
    public void crawl() {
        System.out.println("\u001B[32m" + "Đang lấy dữ liệu từ các Subreddit..." + "\u001B[0m");
        List<Post> postList = new ArrayList<>();
        try {
            File newsList = new File("src/main/resources/reddit/postSources.txt");
            Scanner postListScanner = new Scanner(newsList);
            while(postListScanner.hasNextLine()) {
                String urlString = postListScanner.nextLine();
                String after = "";
                while (after != null) {
                    String response = fetchPost(urlString + "?after=" + after);
                    ObjectMapper mapper = new ObjectMapper();
                    after = mapper.readTree(response).get("data").get("after").textValue();
                    System.out.println(after);
                    JsonNode arrayNode = mapper.readTree(response).get("data").get("children");
                    if (arrayNode.isArray()) {
                        for (JsonNode node : arrayNode) {
                            Post currentPost = new Post(
                                    getGuid(node),
                                    getLink(node),
                                    getSource(node),
                                    "post",
                                    getTitle(node),
                                    getDetailedContent(node),
                                    getDate(node),
                                    getAuthor(node),
                                    getCategories(node),
                                    getUpvotes(node),
                                    getDownvotes(node),
                                    getMediaURL(node)
                            );
                            postList.add(currentPost);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setDataList(postList);
        System.out.println("\u001B[32m" + "Đã lấy dữ liệu từ các Subreddit..." + "\u001B[0m");
    }

    private String fetchPost(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Via", "1.1 varnish")
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private String getGuid(JsonNode node) {
        return "reddit_" + node.get("data").get("id").textValue();
    }

    private String getLink(JsonNode node) {
        return "www.reddit.com" + node.get("data").get("permalink").textValue();
    }

    private String getSource(JsonNode node) {
        return "r/" + node.get("data").get("subreddit").textValue();
    }

    private String getTitle(JsonNode node) {
        return node.get("data").get("title").textValue().trim();
    }

    private String getDetailedContent(JsonNode node) {
        return node.get("data").get("selftext").textValue().trim();
    }

    private String getDate(JsonNode node) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return outputFormat.format(new Date(node.get("data").get("created").asLong() * 1000L));
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getAuthor(JsonNode node) {
        return node.get("data").get("author").textValue();
    }

    private List<String> getCategories(JsonNode node) {
        if (node.get("data").get("link_flair_text").isNull() || node.get("data").get("link_flair_text").textValue().isEmpty()) {
            return Arrays.asList("general");
        }
        return Arrays.asList(node.get("data").get("link_flair_text").textValue().toLowerCase());
    }

    private int getUpvotes(JsonNode node) {
        return node.get("data").get("ups").intValue();
    }

    private int getDownvotes(JsonNode node) {
        return node.get("data").get("downs").intValue();
    }

    private String getMediaURL(JsonNode node) {
        if (node.get("data").get("url").textValue().contains("i.redd.it") || node.get("data").get("url").textValue().contains("v.redd.it")) {
            return node.get("data").get("url").textValue();
        }
        return null;
    }
}
