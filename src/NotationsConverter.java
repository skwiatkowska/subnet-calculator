import java.net.InetAddress;
import java.net.UnknownHostException;

public class NotationsConverter {

    //e.g 24 -> 11111111.11111111.11111111 .00000000
    public static String convertMaskFromCidrNotationToBinary(String maskInCidrNotation) {
        int numberOfZeros = 32 - Integer.parseInt(maskInCidrNotation);
        String maskAsBinary = "";
        int i = 0;
        while (i < 32) {
            if (i % 8 == 0)
                maskAsBinary += " ";
            if (i < 32 - numberOfZeros)
                maskAsBinary += "1";
            else
                maskAsBinary += "0";
            i++;
        }

        return maskAsBinary;
    }


    //e.g 192.168.123.1 to long number
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }


    //e.g 24 -> 255.255.255.0
    public static String convertMaskFromCidrNotationToDottedDecimal(String maskInDottedNotation) throws UnknownHostException {
        int maskAsInt = Integer.parseInt(maskInDottedNotation);
        int _mask = 0xffffffff << (32 - maskAsInt);


        byte[] bytes = new byte[]{
                (byte) (_mask >>> 24), (byte) (_mask >> 16 & 0xff), (byte) (_mask >> 8 & 0xff), (byte) (_mask & 0xff)};
        InetAddress netAddr = InetAddress.getByAddress(bytes);
        return netAddr.getHostAddress();
    }


    //e.g 205.16.37.39 -> 11001101.00010000.00100101.00100111
    public static String convertDottedDecimalToBinary(String address) {
        String[] octetArray = address.split("\\.");
        String binaryAddress = "";
        String[] binaryOctet = new String[4];
        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(octetArray[i]);
            binaryOctet[i] = Integer.toBinaryString(octet);
            while (binaryOctet[i].length() < 8)
                binaryOctet[i] = "0" + binaryOctet[i];
            binaryAddress += binaryOctet[i];
            binaryAddress += " ";

        }
        binaryAddress = binaryAddress.substring(0, binaryAddress.length() - 1);
        return binaryAddress;
    }


    //e.g 11001101.00010000.00100101.00100111 -> 205.16.37.39
    public static String convertBinaryToDottedDecimal(String address) {
        String[] octetArray = address.split("\\s+");
        String dottedDecAddress = "";
        Integer[] decimalOctet = new Integer[4];
        for (int i = 0; i < 4; i++) {
            decimalOctet[i] = Integer.parseInt(octetArray[i], 2);
            dottedDecAddress += decimalOctet[i];
            dottedDecAddress += ".";

        }
        dottedDecAddress = dottedDecAddress.substring(0, dottedDecAddress.length() - 1);
        return dottedDecAddress;
    }
}
