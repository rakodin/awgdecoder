package in.me.oxff.rakodin.awgdecoder.decoder;

import in.me.oxff.rakodin.awgdecoder.Processor;
import in.me.oxff.rakodin.awgdecoder.awg.AwgContainer;
import in.me.oxff.rakodin.awgdecoder.awg.tools.ConfigReader;
import lombok.Getter;
import lombok.SneakyThrows;
import in.me.oxff.rakodin.awgdecoder.args.EncodeSettings;
import in.me.oxff.rakodin.awgdecoder.awg.AwgContainerHead;
import in.me.oxff.rakodin.awgdecoder.awg.AwgRecord;
import org.apache.commons.beanutils.BeanUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;

@Getter
public class Encoder extends Processor<EncodeSettings> {
    public Encoder(EncodeSettings settings) {
        super(settings);
    }

    @Override
    @SneakyThrows({IOException.class, IllegalAccessException.class, InvocationTargetException.class})
    public String process() {
        var ret = new AwgContainerHead();
        ret.setDescription(getSettings().getConfName());
        ret.setDefaultContainer("amnezia-awg");
        var dns = getSettings().getDns();
        ret.setDns1(dns.get(0).getHostAddress());
        if (dns.size() > 1) {
            ret.setDns2(dns.get(1).getHostAddress());
        } else {
            ret.setDns2(dns.get(0).getHostAddress());
        }
        var containers = new ArrayList<AwgContainer>();
        ret.setContainers(containers);
        var confStr = "";
        try (var fis = new FileInputStream(getSettings().getFile())) {
            confStr = new String(fis.readAllBytes(), Charset.defaultCharset());
        }
        //don't check empty string
        var om = Decoder.getObjectMapper();

        var lastConfig = ConfigReader.convert(confStr);
        var aContainer = new AwgContainer();
        aContainer.setContainer(ret.getDefaultContainer());
        containers.add(aContainer);
        var awgRecord = new AwgRecord();
        aContainer.setAwg(awgRecord);
        awgRecord.setTransportProto("udp");
        awgRecord.setPort(Integer.toString(lastConfig.getPort()));
        awgRecord.setLastConfig(om.toJson(lastConfig));
        BeanUtils.copyProperties(awgRecord, lastConfig);
        ret.setHostName(lastConfig.getHostName());
        var result = om.toJson(ret);
        return getSettings().isDump()? result: encodeAsVpn(result);
    }

    @SneakyThrows({IOException.class})
    private String encodeAsVpn(String in) {
        //encode source
        var byteStream = new ByteArrayOutputStream();
        try (var zlibStream = new DeflaterOutputStream(byteStream, true)) {
            zlibStream.write(in.getBytes(Charset.defaultCharset()));
            byteStream.flush();
            byteStream.close();
        }
        var bytes = byteStream.toByteArray();
        var out = new byte[bytes.length + 4];
        System.arraycopy(bytes, 0, out, 4, bytes.length);
        //encode bytes to mime
        var mimeOut = new String(Base64.getMimeEncoder().encode(out))
                .replaceAll("\r","")
                .replaceAll("\n","")
                .replaceAll("\\+", "-")
                .replaceAll("/", "_")
                .replaceAll("=","");
        //next some kind of magic

        return String.format("vpn://%s",mimeOut);
    }
}
