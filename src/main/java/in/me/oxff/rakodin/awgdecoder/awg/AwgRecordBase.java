package in.me.oxff.rakodin.awgdecoder.awg;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AwgRecordBase {
    @SerializedName("H1")
    private String H1;
    @SerializedName("H2")
    private String H2;
    @SerializedName("H3")
    private String H3;
    @SerializedName("H4")
    private String H4;
    @SerializedName("Jc")
    private String Jc;
    @SerializedName("Jmax")
    private String Jmax;
    @SerializedName("Jmin")
    private String Jmin;
    @SerializedName("S1")
    private String S1;
    @SerializedName("S2")
    private String S2;
}
