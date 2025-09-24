package hello.servlet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")  //GET http://localhost:8080/hello
    public String hello(){
        return "hello";
    }
}
