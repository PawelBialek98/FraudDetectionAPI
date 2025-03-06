package com.example.logging;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;
import java.lang.reflect.Field;

@Interceptor
@Logged
public class LoggingInterceptor {
    private static final Logger LOG = Logger.getLogger(LoggingInterceptor.class);

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        Object[] parameters = context.getParameters();
        String maskedParams = maskSensitiveData(parameters);

        LOG.infof("Calling: %s -> %s", methodName, maskedParams);

        Object result = context.proceed();

        String maskedResult = maskSensitiveData(result);
        LOG.infof("Ended: %s -> %s", methodName, maskedResult);

        return result;
    }

    private String maskSensitiveData(Object obj) {
        switch (obj) {
            case null -> {
                return "null";
            }
            case String str -> {
                return str.replaceAll("\\d(?=\\d{4})", "*");
            }
            case Number number -> {
                return "***";
            }
            default -> {
            }
        }

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MaskedLog.class)) {
                    field.setAccessible(true);
                    field.set(obj, maskSensitiveData(field.get(obj)));
                }
            }
        } catch (Exception e) {
            LOG.warn("Błąd maskowania danych", e);
        }
        return obj.toString();
    }
}
