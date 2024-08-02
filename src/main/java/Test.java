import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String clientId = "24e4Sy6UmsLAh1212LHHe6";
        String clientSecret = "$2a$04$hj7FQg.rBFKhvAezmAJUTu";
        String grantType = "client_credentials";
        String type = "SELF";

        String access_token = AuthTokenRequest.access_token;

        Long timestamp = System.currentTimeMillis();

        String token = AuthTokenRequest.getToken(clientId,clientSecret,timestamp);
        AuthTokenRequest.Connect(clientId,token,timestamp,grantType,type); //access token 반환해줌


        OrderInfo(access_token);

    }
    public static String OrderInfo(String access_token) throws IOException {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("https://api.commerce.naver.com/external/v1/pay-order/seller/orders/2024073067966281/product-order-ids")
                .get()
                .addHeader("Authorization", "Bearer "+ access_token)
                .build();

        //Response response = client.newCall(request).execute();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("HTTP error code: " + response.code());
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            System.out.println("상품 주문 번호: " + responseBody);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
