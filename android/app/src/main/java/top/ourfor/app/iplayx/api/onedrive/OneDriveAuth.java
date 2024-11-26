package top.ourfor.app.iplayx.api.onedrive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveAuth {
    String refresh_token;
    String scope;
    Integer ext_expires_in;
    String token_type;
    Integer expires_in;
    String access_token;
    Integer expires_at;
}
