import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class AuthTokenRequest {

    public static String getToken(String clientId, String clientSecret, Long timestamp) throws IOException {

        String pwd = clientId + "_" + timestamp;
        String hashed = BCrypt.hashpw(pwd, clientSecret);
        //String clientSecretSign = Base64.getUrlEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));
    }

    public static String Connect(String clientId, String token, Long timestamp, String grantType, String type) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("client_id", clientId);
        data.put("client_secret_sign", token);
        data.put("timestamp", String.valueOf(timestamp));
        data.put("grant_type", grantType);
        data.put("type", type);

//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//        }

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
        URL url = new URL("https://api.commerce.naver.com/external/v1/oauth2/token");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token); // Authorization 헤더 추가
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        //요청 본문 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = query.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            System.out.println("Connect successful : " + status);

            // 서버로부터의 응답을 가져오기
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //응답 읽기
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);

            }
            in.close();

            String responseBody = response.toString();
            if (responseBody.contains("access_token")) {
                System.out.println(responseBody);
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
