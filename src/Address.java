import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Address {
    private String ip; //e.g 205.16.37.39
    private String mask; //e.g 24 (CIDR notation)


    public Address(String _ip, String _mask) {
        ip = _ip;
        mask = _mask;
    }


    //convert 205.16.37.39/24 to: ip=205.16.37.39, mask=24
    public Address(String[] args) throws Exception {
        String ipmask = "";
        if (args.length != 0) {
            ipmask = args[0];
        }
        else {
            ipmask = getLocalHostAutomatically();

        }
        boolean isCorrectIP = checkAddressCorrectness(ipmask);
        if (isCorrectIP) {
            String[] arr = ipmask.split("/");
            ip = arr[0];
            mask = arr[1];
        }
        else throw new Exception("Bad IP address");

    }

    public boolean checkAddressCorrectness(String str) {
        try {
            String[] arr = str.split("/");
            String _ip = arr[0];
            String _mask = arr[1];

        //ip
        String[] octets = _ip.split("\\.");
        if (octets.length != 4)
            return false;
        for (String s : octets) {
            int i = Integer.parseInt(s);
            if ((i < 0) || (i > 255))
                return false;
        }
        if (_ip.endsWith("."))
            return false;

        //mask
        int maskAsInt = Integer.parseInt(_mask);
        if (maskAsInt < 0 || maskAsInt > 32)
            return false;

        //both ip and mask ok
            return true;
        }catch (Exception e){
            System.err.println("Bad IP address");
        }
        return true;
    }

    //get the previous ip address from the given one
    public static String getPreviousIp(String ip) {
        String[] nums = ip.split("\\.");
        int i = (Integer.parseInt(nums[0]) << 24 | Integer.parseInt(nums[2]) << 8
                | Integer.parseInt(nums[1]) << 16 | Integer.parseInt(nums[3])) - 1;

        if ((byte) i == -1) i++;

        return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
                i >> 8 & 0xFF, i >> 0 & 0xFF);
    }


    public String getIp() {
        return ip;
    }


    public String getMask() {
        return mask;
    }


    public String getCidrNotationIp() {
        return ip + "/" + mask;
    }


    //if there is no argument, use the ip of the current machine
    public String getLocalHostAutomatically() throws UnknownHostException, SocketException {
        InetAddress inAddress = InetAddress.getLocalHost();
        String _ip = inAddress.getHostAddress();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inAddress);
        String _mask = String.valueOf(networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());
        return _ip + "/" + _mask;
    }




    //retrieve the network address from ip and mask
    public String getNetworkAddress() throws UnknownHostException {
        String[] ipAddrParts = ip.split("\\.");
        String[] maskParts = NotationsConverter.convertMaskFromCidrNotationToDottedDecimal(mask).split("\\.");
        String networkAddr = "";

        for (int i = 0; i < 4; i++) {
            int x = Integer.parseInt(ipAddrParts[i]);
            int y = Integer.parseInt(maskParts[i]);
            int z = x & y;
            networkAddr += z + ".";
        }
        networkAddr = networkAddr.substring(0, networkAddr.length() - 1);
        return networkAddr;
    }


    //get wildcard - reversed mask
    public String getWildcardFromMask() {
        String binaryMask = NotationsConverter.convertMaskFromCidrNotationToBinary(mask);
        String wildcard = "";
        for (int i = 0; i < binaryMask.length(); i++) {
            if (binaryMask.charAt(i) == '0')
                wildcard += "1";
            else if (binaryMask.charAt(i) == '1')
                wildcard += "0";

        }
        List<String> list = new ArrayList<>();

        int index = 0;
        while (index < wildcard.length()) {
            list.add(wildcard.substring(index, Math.min(index + 8, wildcard.length())));
            index += 8;
        }
        wildcard = "";
        for (String s : list)
            wildcard += (s + " ");
        wildcard = wildcard.substring(0, wildcard.length() - 1);
        return wildcard;
    }


    //retrieve the broadcast address from ip and mask
    public String getBroadcastAddress() {
        String[] ipAddrParts = ip.split("\\.");
        String[] wildcardParts = NotationsConverter.convertBinaryToDottedDecimal(getWildcardFromMask()).split("\\.");

        String broadcastAddr = "";

        for (int i = 0; i < 4; i++) {
            int x = Integer.parseInt(ipAddrParts[i]);
            int y = Integer.parseInt(wildcardParts[i]);
            int z = x | y;
            broadcastAddr += z + ".";
        }
        broadcastAddr = broadcastAddr.substring(0, broadcastAddr.length() - 1);
        return broadcastAddr;

    }


    public String getFirstHostAddress() throws UnknownHostException {
        //take the next address after the network address
        String network = getNetworkAddress();
        return getNextIp(network);
    }


    public String getLastHostAddress() {
        //take the previous address after the broadcast address
        String network = getBroadcastAddress();
        return getPreviousIp(network);
    }


    //check if given ip is within the given range from ipStart to ipEnd
    public boolean isValidAddressClass(String ipStart, String ipEnd) {
        try {
            long ipLo = NotationsConverter.ipToLong(InetAddress.getByName(ipStart));
            long ipHi = NotationsConverter.ipToLong(InetAddress.getByName(ipEnd));
            long ipToTest = NotationsConverter.ipToLong(InetAddress.getByName(ip));
            return (ipToTest >= ipLo && ipToTest <= ipHi);
        } catch (UnknownHostException e) {
            return false;
        }
    }


    //source: https://pl.wikipedia.org/wiki/IPv4
    public String getIpClass() {
        if (isValidAddressClass("0.0.0.0", "127.255.255.255"))
            return "A";
        else if (isValidAddressClass("128.0.0.0", "191.255.255.255"))
            return "B";
        else if (isValidAddressClass("192.0.0.0", "223.255.255.255"))
            return "C";
        else if (isValidAddressClass("224.0.0.0", "239.255.255.255"))
            return "D";
        else if (isValidAddressClass("240.0.0.0", "255.255.255.255"))
            return "E";
        else
            throw new IllegalArgumentException("Given wrong IP address.");
    }


    //source: https://pl.wikipedia.org/wiki/IPv4
    public String isIpPublicOrPrivate() {
        String[] classesStr = new String[]{"A", "B", "C", "D", "E"};
        List<String> classes = Arrays.asList(classesStr);

        if (classes.contains(this.getIpClass())) {
            if (isValidAddressClass("10.0.0.0", "10.255.255.255") || isValidAddressClass("172.16.0.0", "172.31.255.255") || isValidAddressClass("192.168.0.0", "192.168.255.255"))
                return "private";
            else return "public";
        }
        else
            throw new IllegalArgumentException("Given wrong IP address.");
    }


    //get the previous ip address from the given one
    public String getNextIp(String ip) {
        String[] nums = ip.split("\\.");
        int i = (Integer.parseInt(nums[0]) << 24 | Integer.parseInt(nums[2]) << 8
                | Integer.parseInt(nums[1]) << 16 | Integer.parseInt(nums[3])) + 1;

        if ((byte) i == -1) i++;

        return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
                i >> 8 & 0xFF, i >> 0 & 0xFF);
    }


    public long getMaxHostsNumber() {
        int numberOfZeros = 32 - Integer.parseInt(mask);
        return (long) Math.pow(2, numberOfZeros) - 2;
    }


    //send ping
    public void sendPingRequest() throws IOException {
        InetAddress ipToPing = InetAddress.getByName(ip);
        System.out.println("Sending Ping Request to " + ip);
        if (ipToPing.isReachable(5000))
            System.out.println("Host is reachable");
        else
            System.out.println("Sorry ! We can't reach to this host");
    }


    //before pinging prompt
    public void ping() throws IOException {
        String network = getNetworkAddress();
        Scanner reader = new Scanner(System.in);
        if (!ip.equals(network))
            System.out.println("\nDo you want to ping given ip? If yes, enter Y: ");
        String s = reader.next();
        if (s.equals("Y") || s.equals("y"))
            sendPingRequest();
        else {
            System.out.println("Ping will not be executed.");
        }

    }
}
