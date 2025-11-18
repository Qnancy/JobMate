package cn.edu.zju.cs.jobmate.exceptions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A dummy controller for testing GlobalExceptionHandler.
 */
@RestController
public class DummyControllerUtil {

    @GetMapping("/business-ex")
    public void throwBusinessException() {
        throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
    }

    @GetMapping("/uncaught-ex")
    public void throwUncaughtException() {
        throw new RuntimeException("System failure");
    }
}
