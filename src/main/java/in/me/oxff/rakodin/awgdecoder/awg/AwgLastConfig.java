package in.me.oxff.rakodin.awgdecoder.awg;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AwgLastConfig extends AwgRecordBase {
    @SerializedName("client_ip")
    private String clientIp;
    @SerializedName("client_priv_key")
    private String clientPrivKey;
    @SerializedName("client_pub_key")
    private String clientPubKey;
    private String config;
    private String hostName;
    @SerializedName("psk_key")
    private String pskKey;
    @SerializedName("server_pub_key")
    private String serverPubKey;
    private Integer port;
}
