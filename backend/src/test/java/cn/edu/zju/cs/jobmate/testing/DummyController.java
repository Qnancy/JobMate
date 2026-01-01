package cn.edu.zju.cs.jobmate.testing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * A dummy controller for testing.
 */
@Validated
@RestController
public class DummyController {

    @GetMapping("/business-ex")
    public void throwBusinessException() {
        throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
    }

    @GetMapping("/uncaught-ex")
    public void throwUncaughtException() {
        throw new RuntimeException("System failure");
    }

    @GetMapping("/api/test")
    public String test() {
        return "ok";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/test/admin")
    public String testAdmin() {
        return "admin ok";
    }

    @PostMapping("/api/test/valid")
    public String testValid(@Valid @RequestBody ValidTestRequest req) {
        return "ok";
    }

    @GetMapping("/api/test/constraint/{id}")
    public String testConstraint(@PathVariable @Positive Integer id) {
        return "ok";
    }

    /**
     * A request class for validation testing.
     */
    public static class ValidTestRequest {
        @NotBlank
        public String username;
    }
}
