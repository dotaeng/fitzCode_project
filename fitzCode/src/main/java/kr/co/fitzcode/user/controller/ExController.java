package kr.co.fitzcode.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExController {

    @GetMapping("/resetPwd")
    public String joinEmail() {
        return "user/resetPw";
    }
}
