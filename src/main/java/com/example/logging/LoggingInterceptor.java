package com.example.logging;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Interceptor
@Logged
public class LoggingInterceptor {
    private static final Logger LOG = Logger.getLogger(LoggingInterceptor.class);

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        Object[] parameters = context.getParameters();
        Object[] maskedParameters = Arrays.stream(parameters)
                .map(this::maskSensitiveData)
                .toArray();

        LOG.infof("Calling: %s(%s)", methodName, Arrays.toString(maskedParameters));

        Object result = context.proceed();

        String maskedResult = maskSensitiveData(result);
        LOG.infof("Ended: %s -> %s", methodName, maskedResult);

        return result;
    }

    private String maskSensitiveData(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof String str) {
            return str.replaceAll("\\d(?=\\d{4})", "*"); // Zamiana cyfr na '*', ale zostawienie 4 ostatnich znaków
        }

        if (obj instanceof Number) {
            return "***"; // Zamiana liczb na ***
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
