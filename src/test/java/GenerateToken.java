import java.util.Arrays;
import java.util.HashSet;

import io.smallrye.jwt.build.Jwt;

public class GenerateToken {

    public static void main(String[] args) {
        String token = Jwt.issuer("some-issuer")
                .upn("pawel")
                .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                .sign();

        System.out.println(token);
        System.exit(0);
    }
}