package com.candleservice.api.controller;

import com.candleservice.api.dto.HistoryResponseDto;
import com.candleservice.domain.port.in.QueryHistoryUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.candleservice.domain.model.TimeframeMapper.parse;

@RestController
@RequestMapping("/history")
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    private final QueryHistoryUseCase queryHistory;

    public HistoryController(QueryHistoryUseCase queryHistory) {
        this.queryHistory = queryHistory;
    }

    @GetMapping
    public ResponseEntity<HistoryResponseDto> history(
            @RequestParam String symbol,
            @RequestParam String interval,
            @RequestParam long from,
            @RequestParam long to
    ) {
        logger.info("History request: symbol={}, interval={}, from={}, to={}", symbol, interval, from, to);
        
        var timeframe = parse(interval);
        var candles = queryHistory.getHistory(symbol, timeframe, from, to);
        var response = HistoryResponseDto.from(candles);
        
        logger.info("Returning {} candles for {}", candles.size(), symbol);
        return ResponseEntity.ok(response);
    }
}
