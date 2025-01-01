package mephi.url_shorter.presenters;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mephi.url_shorter.domain.interactors.PermissionDeniedException;
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
    public ResponseEntity<?> shortenUrl(@RequestParam String url,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Integer maxRedirects) {
        ShortenedUrlResponse shortUrlResponse;
        if (userId != null) {
            shortUrlResponse = interactor.getShortUrl(url, userId);
        } else {
            shortUrlResponse = interactor.getShortUrl(url);
        }
        if (maxRedirects != null) {
            try {
                interactor.updateShortUrl(shortUrlResponse.getShortenedUrl(), shortUrlResponse.getUserId(),
                        maxRedirects);
            } catch (PermissionDeniedException e) {
                return ResponseEntity.status(403).body(e.getMessage());
            }
        }
        return ResponseEntity.ok(shortUrlResponse);
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

    @DeleteMapping("/{shortUrl}/{userId}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortUrl, @PathVariable UUID userId) {
        try {
            interactor.deleteShortUrl(shortUrl, userId);
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shortUrl}/{userId}")
    public ResponseEntity<?> updateMaxRedirects(@PathVariable String shortUrl, @PathVariable UUID userId,
            @RequestParam int maxRedirects) {
        try {
            interactor.updateShortUrl(shortUrl, userId, maxRedirects);
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
