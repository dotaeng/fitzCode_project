package kr.co.fitzcode.user.controller;

import kr.co.fitzcode.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DuplicateController {

    private final UserService userService;

    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = userService.emailDuplicate(email);
        response.put("available", !isDuplicate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = userService.nicknameDuplicate(nickname);
        response.put("available", !isDuplicate);
        return ResponseEntity.ok(response);
    }
}