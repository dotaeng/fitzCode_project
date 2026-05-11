package kr.co.fitzcode.common.controller;

import kr.co.fitzcode.common.dto.UserDTO;
import kr.co.fitzcode.common.enums.UserRole;
import kr.co.fitzcode.common.service.CustomUserDetails;
import kr.co.fitzcode.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommonUserController {
    private final UserService userService;

    @GetMapping("/getUserByEmail")
    public ResponseEntity<UserDTO> getUserByEmail(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUserRolesById")
    public ResponseEntity<List<Integer>> userRolesById(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userId = userDetails.getUserId();
        List<Integer> roles = userService.getUserRolesByUserId(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/getUserRolesInStringByUserId")
    public ResponseEntity<List<UserRole>> userRolesInStringByUserId(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userId = userDetails.getUserId();
        List<UserRole> roles = userService.findRolesInStringByUserId(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/getCurrentUserId")
    public ResponseEntity<Integer> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return ResponseEntity.ok(userDetails.getUserId());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}