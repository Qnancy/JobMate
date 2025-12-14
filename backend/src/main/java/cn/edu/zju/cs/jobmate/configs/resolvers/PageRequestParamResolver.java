package cn.edu.zju.cs.jobmate.configs.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;

/**
 * Param resolver for {@link PageRequest} used in resolving query params.
 */
@Component
public class PageRequestParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return PageRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
        @NonNull MethodParameter parameter,
        @Nullable ModelAndViewContainer mavContainer,
        @NonNull NativeWebRequest webRequest,
        @Nullable WebDataBinderFactory binderFactory
    ) throws Exception {
        // Get all request parameters.
        Map<String, String[]> paramMap = webRequest.getParameterMap();
        // Convert to camelCase keys.
        Map<String, Object> camelMap = new HashMap<>();
        paramMap.forEach((k, v) -> {
            String camelKey = toCamelCase(k);
            camelMap.put(camelKey, v[0]);
        });
        // Convert to PageRequest or its subclass.
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(camelMap, parameter.getParameterType());
    }

    private String toCamelCase(String s) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : s.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else if (upper) {
                sb.append(Character.toUpperCase(c));
                upper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
