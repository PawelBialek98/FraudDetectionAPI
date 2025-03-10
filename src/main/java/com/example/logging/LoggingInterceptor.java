package com.example.logging;

import io.quarkus.logging.Log;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.stream.Collectors;

@Interceptor
@Logged
public class LoggingInterceptor {

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();
        String params = Arrays.stream(context.getParameters())
                .map(param -> param != null ? param.toString() : "null")
                .collect(Collectors.joining(", "));

        Log.infof("Calling: %s.%s(%s)", className, methodName, params);

        try {
            Object result = context.proceed();
            Log.infof("Method %s.%s returned successfully", className, methodName);
            return result;
        } catch (Exception e) {
            Log.errorf("Method %s.%s failed: %s", className, methodName, e.getMessage());
            throw e;
        }
    }
}
