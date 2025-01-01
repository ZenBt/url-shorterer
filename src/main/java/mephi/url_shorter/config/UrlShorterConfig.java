package mephi.url_shorter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mephi.url_shorter.domain.interactors.UrlShortererInteractor;
import mephi.url_shorter.domain.repositories.UrlShortererRepository;

@Configuration
public class UrlShorterConfig {
    private final UrlShortererConfigProperties properties;

    public UrlShorterConfig(UrlShortererConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public UrlShortererInteractor urlShortererInteractor(UrlShortererRepository repository) {
        return new UrlShortererInteractor(repository, properties.getMaxRedirects());
    }
}
