import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class AuthTokenRequest {
    public static void main(String[] args) throws IOException {

        String clientId = "24e4Sy6UmsLAh1212LHHe6";
        String clientSecret = "$2a$04$hj7FQg.rBFKhvAezmAJUTu";
        String grantType = "client_credentials";
        String type = "SELF";

        String token = getToken(clientId, clientSecret, type);
        System.out.println(token);
    }

    private static String getToken(String clientId, String clientSecret, String type) throws IOException {

        long timestamp = System.currentTimeMillis();
        String pwd = clientId + "_" + timestamp;
        String hashed = BCrypt.hashpw(pwd, clientSecret);
        String clientSecretSign = Base64.getUrlEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));
        System.out.println(timestamp);

        Map<String, String> data = new HashMap<>();
        data.put("client_id", clientId);
        data.put("timestamp", String.valueOf(timestamp));
        data.put("client_secret_sign", clientSecretSign);
        data.put("grant_type", "client_credentials");
        data.put("type", type);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        //데이터 URL 인코딩
        String query = data.entrySet().stream()
                .map(entry -> {
                    try {
                        return encodeValue(entry.getKey()) + "=" + encodeValue(entry.getValue());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("&"));


        //요청 본문 데이터
        URL url = new URL("https://api.commerce.naver.com/external/v1/oauth2/token?" + query);

        System.out.println(url);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        //요청 본문 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = query.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String responseBody = response.toString();
            if (responseBody.contains("access_token")) {
                return parseAccessToken(responseBody);
            } else {
                throw new RuntimeException("토큰 요청 실패: " + responseBody);
            }
        } else {
            throw new RuntimeException("Request failed. HTTP error code: " + status);
        }
    }

    private static String parseAccessToken(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        return json.getString("access_token");
    }

    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

