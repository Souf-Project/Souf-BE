package com.souf.soufwebsite.domain.feed.competition.service;

import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankResDto;

import java.util.List;

public interface CompetitionService {

    List<CompetitionRankResDto> getCurrentCompetitionTop5();
}
