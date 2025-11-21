package cn.edu.zju.cs.jobmate.utils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;

/**
 * A dummy controller for testing.
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

    @GetMapping("/api/test")
    public String test() {
        return "ok";
    }
}
