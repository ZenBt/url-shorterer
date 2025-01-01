package mephi.url_shorter.presenters;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mephi.url_shorter.domain.interactors.UrlShortererInteractor;

@RestController
@RequestMapping("/url")
public class UrlShortererController {

    private final UrlShortererInteractor interactor;

    @Autowired
    public UrlShortererController(UrlShortererInteractor interactor) {
        this.interactor = interactor;
    }

    @PostMapping("/shorten")
    public ShortenedUrlResponse shortenUrl(@RequestParam String url,
            @RequestParam(required = false) UUID userId) {
        if (userId != null) {
            return interactor.getShortUrl(url, userId);
        }
        return interactor.getShortUrl(url);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> expandUrl(@PathVariable String shortUrl) {
        Optional<String> initialUrl = interactor.getInitialUrl(shortUrl);

        // Если не найдено, вернем 404
        if (initialUrl.isEmpty()) {
            String errorMessage = "<html><body><h1>Ссылка истекла, никогда не была создана либо исчерпан лимит переходов</h1></body></html>";
            return ResponseEntity.status(404).body(errorMessage);
        }

        // Иначе редиректим пользователя
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", initialUrl.get());
        return ResponseEntity.status(302).headers(headers).build();
    }
}
