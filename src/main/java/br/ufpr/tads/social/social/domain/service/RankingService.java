package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import br.ufpr.tads.social.social.dto.response.profile.UserRanking;
import br.ufpr.tads.social.social.dto.response.profile.UserStatistics;
import br.ufpr.tads.social.social.infrastructure.adapter.ReceiptClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {

    @Autowired
    private ReceiptClient receiptClient;

    @Autowired
    private ProfileService profileService;

    public SliceImpl<UserRanking> getRanking(Pageable pageable) {
        Slice<UserStatistics> userStatistics = receiptClient.getUserStatistics(pageable);

        List<UserRanking> userRankings = new ArrayList<>();

        userStatistics.getContent().forEach(userStatistic -> {
            GetUserProfileDTO profile = profileService.getProfile(userStatistic.getUserId());

            userRankings.add(UserRanking.builder()
                            .userId(userStatistic.getUserId())
                            .totalReceipts(userStatistic.getTotalReceipts())
                            .totalProducts(userStatistic.getTotalProducts())
                            .name(profile.getFirstName())
                            .urlImage(profile.getPhoto())
                            .build());
        });

        return new SliceImpl<>(userRankings, userStatistics.getPageable(), userStatistics.hasNext());
    }
}
