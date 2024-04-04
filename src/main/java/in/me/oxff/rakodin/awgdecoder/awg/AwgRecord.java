package in.me.oxff.rakodin.awgdecoder.awg;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AwgRecord extends AwgRecordBase {
    @SerializedName("last_config")
    private String lastConfig;
    @SerializedName("transport_proto")
    private String transportProto;
    private String port;
}
