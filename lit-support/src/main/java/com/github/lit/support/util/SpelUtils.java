package com.github.lit.support.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * User : liulu
 * Date : 2018/3/25 17:07
 * version $Id: SpelUtils.java, v 0.1 Exp $
 */
@Slf4j
public abstract class SpelUtils {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();


    public static String getExpressionValue(String expr, Map<String, Object> content) {

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariables(content);

        try {
            return expressionParser.parseExpression(expr).getValue(evaluationContext, String.class);
        } catch (Exception e) {
            //
            log.warn("parse el expression {} failed, because of {}", expr, e.getMessage());
        }
        return expr;
    }


}
