import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public class NaverStoreApi {
    private static final String API_KEY = "24e4Sy6UmsLAh1212LHHe6";
    private static final String API_URL = "https://api.commerce.naver.com/external/v1/pay-order/seller/orders/{orderId}/product-order-ids";

    public static void getNewOrderList() {


        try {
            // 현재 시간에서 이틀 전 시간 계산
            ZonedDateTime now = Instant.now().atZone(ZoneOffset.UTC);
            ZonedDateTime beforeDate = now.minusDays(2);
            String iosFormat = beforeDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // HTTP 요청 설정
            URL url = new URL(API_URL + "?lastChangedFrom=" + iosFormat + "&lastChangedType=DISPATCHED");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", API_KEY);

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // JSON 응답 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> resData = objectMapper.readValue(response.toString(), HashMap.class);

                if (!resData.containsKey("data")) {
                    System.out.println("주문 내역 없음");
                    return;
                }

                Map<String, Object> data = (Map<String, Object>) resData.get("data");
                if (!data.containsKey("lastChangeStatuses")) {
                    System.out.println("주문 내역 없음");
                    return;
                }

                // 주문 정보 출력
                for (Map<String, Object> order : (List<Map<String, Object>>) data.get("lastChangeStatuses")) {
                    System.out.println(order);
                }
            } else {
                System.out.println("HTTP 응답 코드: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getNewOrderList();
    }
}

