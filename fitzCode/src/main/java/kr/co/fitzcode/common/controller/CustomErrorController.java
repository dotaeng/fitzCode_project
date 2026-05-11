package kr.co.fitzcode.common.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;

@Controller
public class CustomErrorController implements ErrorController {

    // 403 접근권한 에러
    @GetMapping("/error")
    public String handleError(Model model, jakarta.servlet.http.HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            model.addAttribute("status", status.toString());
        }
        model.addAttribute("error", error != null ? error.toString() : "알 수 없는 오류");
        return "access-denied";
    }
}