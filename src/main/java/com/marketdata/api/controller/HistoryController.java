package com.marketdata.api.controller;

import com.marketdata.api.dto.HistoryResponseDto;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.marketdata.domain.model.TimeframeMapper.parse;

@RestController
@RequestMapping("/history")
@Validated
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    private final QueryHistoryUseCase queryHistory;

    public HistoryController(QueryHistoryUseCase queryHistory) {
        this.queryHistory = queryHistory;
    }

    @GetMapping
    public ResponseEntity<HistoryResponseDto> history(
            @RequestParam @NotBlank(message = "Symbol is required") String symbol,
            @RequestParam @NotBlank(message = "Interval is required") String interval,
            @RequestParam @Min(value = 0, message = "From timestamp must be >= 0") long from,
            @RequestParam @Min(value = 0, message = "To timestamp must be >= 0") long to
    ) {
        if (to < from) {
            throw new IllegalArgumentException("'to' timestamp must be >= 'from' timestamp");
        }
        
        logger.info("History request: symbol={}, interval={}, from={}, to={}", symbol, interval, from, to);
        
        var timeframe = parse(interval);
        var candles = queryHistory.getHistory(symbol, timeframe, from, to);
        var response = HistoryResponseDto.from(candles);
        
        logger.info("Returning {} candles for {} (Java aggregation)", candles.size(), symbol);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sql")
    public ResponseEntity<HistoryResponseDto> historyWithSql(
            @RequestParam @NotBlank(message = "Symbol is required") String symbol,
            @RequestParam @NotBlank(message = "Interval is required") String interval,
            @RequestParam @Min(value = 0, message = "From timestamp must be >= 0") long from,
            @RequestParam @Min(value = 0, message = "To timestamp must be >= 0") long to
    ) {
        if (to < from) {
            throw new IllegalArgumentException("'to' timestamp must be >= 'from' timestamp");
        }
        
        logger.info("History SQL request: symbol={}, interval={}, from={}, to={}", symbol, interval, from, to);
        
        var timeframe = parse(interval);
        var candles = queryHistory.getHistoryWithSql(symbol, timeframe, from, to);
        var response = HistoryResponseDto.from(candles);
        
        logger.info("Returning {} candles for {} (SQL aggregation)", candles.size(), symbol);
        return ResponseEntity.ok(response);
    }
}
