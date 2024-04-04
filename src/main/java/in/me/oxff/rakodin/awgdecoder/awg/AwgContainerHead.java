package in.me.oxff.rakodin.awgdecoder.awg;

import lombok.Data;

import java.util.List;

@Data
public class AwgContainerHead {
    private List<AwgContainer> containers;
    private String defaultContainer;
    private String description;
    private String dns1;
    private String dns2;
    private String hostName;
}
