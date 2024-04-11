import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    private static String URL = "https://jsonmock.hackerrank.com/api/articles?author=%s&page=%d";

    public static void main(String[] args) {
        List<String> results = getTitles("epaga");
        results.forEach(System.out::println);
    }

    private static List<String> getTitles(String author) {
        List<String> results = new ArrayList<>();
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            int page = 1;
            while (true) {
                String apiUrl = String.format(URL, author, page);
                HttpGet request  = new HttpGet(apiUrl);
                CloseableHttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    break;
                }
                String result = EntityUtils.toString(entity);
                Gson gson = new Gson();
                AuthorResponse res = gson.fromJson(result, AuthorResponse.class);

                List<AuthorData> data = res.getData();
                List<String> titles = data.stream()
                        .filter(it -> Objects.nonNull(it.getTitle()) || Objects.nonNull(it.getStory_title()))
                        .map(it -> Objects.isNull(it.getTitle())? it.getStory_title() : it.getTitle())
                        .collect(Collectors.toList());
                results.addAll(titles);

                // next page
                Integer totalPage = res.getTotal_pages();
                if (totalPage == null || page >= totalPage) {
                    break;
                }
                page = page + 1;
            }

        } catch (Exception ex) {
            System.out.println("error: " + ex.getMessage());
        }
        return results;
    }

    public class AuthorResponse {
        private Integer page;
        private Integer per_page;
        private Integer total;
        private Integer total_pages;
        private List<AuthorData> data;

        public AuthorResponse(Integer page, Integer per_page, Integer total, Integer total_pages, List<AuthorData> data) {
            super();
            this.page = page;
            this.per_page = per_page;
            this.total = total;
            this.total_pages = total_pages;
            this.data = data;
        }

        public Integer getTotal_pages() {
            return total_pages;
        }

        public List<AuthorData> getData() {
            return data;
        }

        public void setData(List<AuthorData> data) {
            this.data = data;
        }
    }

    public class AuthorData {
        private String title;
        private String story_title ;

        public AuthorData(String title, String story_title) {
            super();
            this.title = title;
            this.story_title = story_title;
        }

        public String getTitle() {
            return title;
        }

        public String getStory_title() {
            return story_title;
        }
    }
}
