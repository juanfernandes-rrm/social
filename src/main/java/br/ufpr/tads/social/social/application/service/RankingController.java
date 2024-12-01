package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping
    public ResponseEntity<?> getRanking(@RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting ranking");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(rankingService.getRanking(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
