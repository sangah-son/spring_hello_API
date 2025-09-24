package hello.servlet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/bmi")
public class BmiController {

    // DTO: JSON 본문으로 받는 요청
    public record BmiRequest(
            @NotNull @Min(50) @Max(300) Integer heightCm, // 50~300 cm
            @NotNull @Min(10) @Max(400) Integer weightKg   // 10~400 kg
    ) {}

    // DTO: 응답
    public record BmiResponse(double bmi, String category) {}

    // 1) POST JSON: {"heightCm":170,"weightKg":65}
    @PostMapping
    public BmiResponse calcFromJson(@Valid @RequestBody BmiRequest req) {
        return compute(req.heightCm(), req.weightKg());
    }

    // 2) GET 경로변수: /api/bmi/170/65
    @GetMapping("/{heightCm}/{weightKg}")
    public BmiResponse calcFromPath(
            @PathVariable @Min(50) @Max(300) int heightCm,
            @PathVariable @Min(10) @Max(400) int weightKg
    ) {
        return compute(heightCm, weightKg);
    }

    // 공통 계산 로직
    private BmiResponse compute(int heightCm, int weightKg) {
        double h = heightCm / 100.0;
        double bmiRaw = weightKg / (h * h);
        double bmi = BigDecimal.valueOf(bmiRaw).setScale(1, RoundingMode.HALF_UP).doubleValue();
        String category = (bmi < 18.5) ? "Underweight"
                : (bmi < 23.0) ? "Normal (KR 기준)"
                : (bmi < 25.0) ? "Overweight (KR 기준)"
                : "Obese (KR 기준)";
        return new BmiResponse(bmi, category);
    }
}
