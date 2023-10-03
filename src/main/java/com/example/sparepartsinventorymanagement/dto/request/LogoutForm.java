package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogoutForm {

        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
        private String refreshToken;

        public String getRefreshToken() {
            return DigestUtils.sha3_256Hex(refreshToken);
        }


}
