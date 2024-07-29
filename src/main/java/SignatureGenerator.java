import org.mindrot.jbcrypt.BCrypt;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class SignatureGenerator {
    public static String generateSignature(String clientId, String clientSecret, Long timestamp) {
        // 밑줄로 연결하여 password 생성
        String password = StringUtils.joinWith("_", clientId, timestamp);
        // bcrypt 해싱
        String hashedPw = BCrypt.hashpw(password, clientSecret);
        // base64 인코딩
        return Base64.getUrlEncoder().encodeToString(hashedPw.getBytes(StandardCharsets.UTF_8));
    }

    public static String Generrator(){
        String clientId = "24e4Sy6UmsLAh1212LHHe6";
        String clientSecret = "$2a$04$hj7FQg.rBFKhvAezmAJUTu";
        Long timestamp = System.currentTimeMillis();
        System.out.println(generateSignature(clientId, clientSecret, timestamp));

        return generateSignature(clientId, clientSecret, timestamp);


    }


}
