import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String clientId = "24e4Sy6UmsLAh1212LHHe6";
        String clientSecret = "$2a$04$hj7FQg.rBFKhvAezmAJUTu";
        String grantType = "client_credentials";
        String type = "SELF";
        Long timestamp = System.currentTimeMillis();

        String token = AuthTokenRequest.getToken(clientId,clientSecret,timestamp);
        AuthTokenRequest.Connect(clientId,token,timestamp,grantType,type);
    }
}
