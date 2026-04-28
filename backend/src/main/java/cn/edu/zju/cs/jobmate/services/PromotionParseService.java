package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseRequest;
import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseResponse;

/**
 * Parses unstructured promotion text into structured job or activity fields using an LLM.
 */
public interface PromotionParseService {

    /**
     * Calls the configured LLM and resolves company id when possible.
     *
     * @param request target type and raw text
     * @return structured fields plus warnings (never persists)
     */
    PromotionParseResponse parse(PromotionParseRequest request);
}
