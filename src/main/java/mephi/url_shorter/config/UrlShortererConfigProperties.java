package mephi.url_shorter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlShortererConfigProperties {

    @Value("${urlshorter.max-redirects}")
    private int maxRedirects;

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }
}