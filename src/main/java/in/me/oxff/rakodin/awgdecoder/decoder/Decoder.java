package in.me.oxff.rakodin.awgdecoder.decoder;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.me.oxff.rakodin.awgdecoder.Processor;
import lombok.Getter;
import in.me.oxff.rakodin.awgdecoder.args.DecodeSettings;
import in.me.oxff.rakodin.awgdecoder.awg.AwgContainerHead;
import in.me.oxff.rakodin.awgdecoder.awg.AwgLastConfig;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.InflaterOutputStream;

@Getter
public class Decoder extends Processor<DecodeSettings> {

    public Decoder(DecodeSettings props) {
        super(props);
    }

    @Override
    public String process() {
        try (var input = new FileInputStream(getSettings().getFile())) {
            //strip 'vpn://' and replace special chars
            var url = new String(input.readAllBytes(), Charset.defaultCharset())
                    .replaceFirst("vpn://", "")
                    .replaceAll("-", "+")
                    .replaceAll("_", "/");
            //add mime
            if (url.length() % 4 == 3) {
                url += "=";
            } else if (url.length() % 4 == 2) {
                url += "==";
            }
            var mimeBytes = Base64.getMimeDecoder().decode(url);
            //strip first 4 bytes
            var out = new byte[mimeBytes.length - 4];
            System.arraycopy(mimeBytes, 4, out, 0, out.length);
            var byteStream = new ByteArrayOutputStream();
            var zlibStream = new InflaterOutputStream(byteStream);
            zlibStream.write(out);
            zlibStream.flush();
            zlibStream.close();
            byteStream.flush();
            byteStream.close();
            return getOutput(byteStream.toString(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getOutput(String res) {
        if (getSettings().isDump()) {
            return res;
        }
        var sb = new StringBuilder();
        var om = getObjectMapper();
        var valStr = new String(res.getBytes(StandardCharsets.UTF_8));
        var result = om.fromJson(valStr, AwgContainerHead.class);
        result.getContainers().forEach(c -> {
            var lastConf = om.fromJson(c.getAwg().getLastConfig(), AwgLastConfig.class);
            sb.append(lastConf.getConfig());

        });
        return sb.toString();
    }


    public static Gson getObjectMapper() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .setPrettyPrinting()
                .create();
    }
}
