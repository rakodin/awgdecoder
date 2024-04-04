package in.me.oxff.rakodin.awgdecoder.args;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;

@Getter
@ToString(callSuper = true)
public class EncodeSettings extends ModeSettings {
    private File file;
    private boolean dump = false;
    private String confName;
    private final List<InetAddress> dns = new ArrayList<>(2) {{
       add(getInetAddress("1.1.1.1"));
       add(getInetAddress("1.0.0.1"));
    }};

    private final static Random random = new Random(System.currentTimeMillis());
    private final static Map<String, String> args =
            Map.of("-i", "Input file is not set",
                    "-d", "DNS servers is not set",
                    "-of","Invalid output format",
                    "-n", "Configuration name");
    public EncodeSettings(Mode mode) {
        super(mode);
    }

    @Override
    public void parse(String[] input) throws SettingsParseError {
        if (input.length < 3) {
            throw new SettingsParseError(
                    String.format("Invalid decode arguments: %s", String.join(", ", args.values())));
        }
        var out = PropsConverter.convert(input, args.keySet());
        var fileName = out.get("-i");
        if (fileName == null) {
            throw new SettingsParseError(String.format("Invalid decode arguments: %s", args.get("-i")));
        }
        file = new File(fileName);
        if (file.isFile() && file.canRead()) {
            //parse dns
            fillDns(file, out.get("-d"));
        } else {
            throw new SettingsParseError(String.format("Can't read file %s", fileName));
        }
        dump = out.getOrDefault("-of", "conf").equalsIgnoreCase("dump");
        confName = out.getOrDefault("-n", String.format("Conv-%s", Math.abs(random.nextInt())));
    }

    private void fillDns(File input, String dnsStr) {
        //first we get user input
        if (dnsStr != null && !dnsStr.isBlank()) {
            var userDns = Arrays.asList(dnsStr.split(":"));
            if (!userDns.isEmpty()) {
                dns.clear();
                userDns.forEach(ud -> dns.add(getInetAddress(ud)));
                return;
            }
        }
        //next try to read DNS from file
        var configDnsStr = readDNSFromConf(getAsConfig());
        if (configDnsStr != null && !configDnsStr.startsWith("$")) {
            dns.clear();
            Arrays.asList(configDnsStr.split(","))
                    .forEach(cd -> dns.add(getInetAddress(cd)));
        }
    }

    @SneakyThrows({IOException.class})
    public String getAsConfig() {
        try (var fis = new FileInputStream(file)) {
            return new String(fis.readAllBytes(), Charset.defaultCharset());
        }
    }

    public static String readDNSFromConf(String fContent) {
        var section = "Interface";
        var name = "DNS";
        var scanner = new Scanner(fContent);
        var inSection = false;
        while (scanner.hasNextLine()) {
            var l = scanner.nextLine();
            if (inSection && l.trim().startsWith("[")) {
                //actually end of section
                return null;
            }
            if (inSection && l.trim().startsWith(name)) {
                var nameWithSettings = l.split("=");
                return nameWithSettings[1].trim();
            }
            if (l.trim().startsWith("[" + section + "]") && !inSection) {
                inSection = true;
            }
        }
        return null;
    }

    @SneakyThrows({UnknownHostException.class})
    private static InetAddress getInetAddress(String addr) {
        return Inet4Address.getByName(addr);
    }
}
