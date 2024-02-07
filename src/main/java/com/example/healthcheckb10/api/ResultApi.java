package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.result.AdminGetResultResponse;
import com.example.healthcheckb10.dto.result.GetResultResponse;
import com.example.healthcheckb10.dto.result.ResultRequest;
import com.example.healthcheckb10.dto.result.ResultResponse;
import com.example.healthcheckb10.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Tag(name = "Result Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResultApi {
    private final ResultService resultService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-result")
    @Operation(summary = "Метод добавления результата пациента",
            description = "Этот метод следует использовать для добавления результата пациента." +
                    "Права на метод имеет только админ!")
    public ResultResponse addResult(@RequestBody @Valid ResultRequest request) {
        return resultService.addResult(request);
    }

    @GetMapping
            @Operation(summary = "Метод получения результата",
            description = "Вы можете видеть только свой результат.")
    public GetResultResponse getResult(@RequestParam String resultNumber){
        return resultService.getResult(resultNumber);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/forAdmin")
    @Operation(summary = "Получить результат для администратора",
            description = "Эта операция используется для получения результата конкретно для администратора.")
    public List<AdminGetResultResponse> getResultForAdmin(@RequestParam Long userId){
        return resultService.getResultForAdmin(userId);
    }
}