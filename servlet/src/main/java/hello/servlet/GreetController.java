package hello.servlet;

import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Validated
@RestController
@RequestMapping("/greet")
public class GreetController {

    // 응답 DTO
    public record GreetResponse(
            String message,
            String name,
            String style,        // formal / casual
            String timeOfDay,    // morning/afternoon/evening/night
            String timestamp     // yyyy-MM-dd HH:mm:ss
    ) {}

    // 1) 쿼리 파라미터: GET /greet?name=상아&style=formal
    @GetMapping
    public GreetResponse greet(
            @RequestParam(defaultValue = "손님")
            @Pattern(regexp = "^[가-힣a-zA-Z0-9_\\-\\s]{1,20}$", message = "이름 형식이 올바르지 않습니다.")
            String name,
            @RequestParam(defaultValue = "casual") String style  // casual/formal
    ) {
        var now = LocalDateTime.now();
        var timeOfDay = toTimeOfDay(now.getHour());

        String message = switch (style.toLowerCase()) {
            case "formal" -> (koFormal(timeOfDay) + " " + name + "님.");
            default       -> ("안녕, " + name + "!");
        };

        return new GreetResponse(
                message,
                name,
                style.toLowerCase(),
                timeOfDay,
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    // 2) 경로변수 버전: GET /greet/상아?style=casual
    @GetMapping("/{name}")
    public GreetResponse greetPath(
            @PathVariable
            @Pattern(regexp = "^[가-힣a-zA-Z0-9_\\-\\s]{1,20}$", message = "이름 형식이 올바르지 않습니다.")
            String name,
            @RequestParam(defaultValue = "casual") String style
    ) {
        return greet(name, style);
    }

    // ---- helpers ----
    private String toTimeOfDay(int hour) {
        if (hour >= 5 && hour < 12)  return "morning";
        if (hour >= 12 && hour < 18) return "afternoon";
        if (hour >= 18 && hour < 22) return "evening";
        return "night";
    }

    private String koFormal(String t){
        return switch (t) {
            case "morning"   -> "좋은 아침입니다";
            case "afternoon" -> "좋은 오후입니다";
            case "evening"   -> "좋은 저녁입니다";
            default          -> "편안한 밤 되세요";
        };
    }
}
