import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/*
an ip address with a mask must be passed from the command line 
if no argument was passed, the current machine's ip address and a mask are taken

to run this program: javac Main.java
				java Main 192.168.123.1/24

*/
public class Main {

    public static void main(String[] args) throws IOException {

        try {
            Address address = new Address(args);

            // save original out and err stream
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;

            // create a new file output and error stream
            PrintStream fileOut = new PrintStream("./out.txt");
            PrintStream fileErr = new PrintStream("./err.txt");

            // Redirect standard out and err to file
            System.setOut(fileOut);
            System.setErr(fileErr);

            originalOut.println("*All results are saved in files: out.txt, err.txt*\n");
            originalOut.println("IP ADDRESS AND MASK: " + address.getCidrNotationIp());
            System.out.println("IP ADDRESS AND MASK: " + address.getCidrNotationIp());

            originalOut.println("\n\nNetwork address from a given ip and mask: " + address.getNetworkAddress());
            System.out.println("\n\nNetwork address from a given ip and mask: " + address.getNetworkAddress());

            originalOut.println("IP class: " + address.getIpClass());
            System.out.println("IP class: " + address.getIpClass());

            originalOut.println("This IP is: " + address.isIpPublicOrPrivate());
            System.out.println("This IP is: " + address.isIpPublicOrPrivate());

            originalOut.println("\nSubnet mask in dotted decimal notation: " + NotationsConverter.convertMaskFromCidrNotationToDottedDecimal(address.getMask()));
            System.out.println("\nSubnet mask in dotted decimal notation: " + NotationsConverter.convertMaskFromCidrNotationToDottedDecimal(address.getMask()));

            originalOut.println("Subnet mask in binary notation: " + NotationsConverter.convertMaskFromCidrNotationToBinary(address.getMask()));
            System.out.println("Subnet mask in binary notation: " + NotationsConverter.convertMaskFromCidrNotationToBinary(address.getMask()));

            originalOut.println("\nBroadcast address in dotted decimal notation: " + address.getBroadcastAddress());
            System.out.println("\nBroadcast address in dotted decimal notation: " + address.getBroadcastAddress());

            originalOut.println("Broadcast address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getBroadcastAddress()));
            System.out.println("Broadcast address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getBroadcastAddress()));

            originalOut.println("\nFirst host address in dotted decimal notation: " + address.getFirstHostAddress());
            System.out.println("\nFirst host address in dotted decimal notation: " + address.getFirstHostAddress());

            originalOut.println("First host address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getFirstHostAddress()));
            System.out.println("First host address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getFirstHostAddress()));

            originalOut.println("\nLast last address in dotted decimal notation: " + address.getLastHostAddress());
            System.out.println("Last last address in dotted decimal notation: " + address.getLastHostAddress());

            originalOut.println("Last last address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getLastHostAddress()));
            System.out.println("Last last address in binary notation: " + NotationsConverter.convertDottedDecimalToBinary(address.getLastHostAddress()));

            originalOut.println("\nMaximum number of hosts: " + address.getMaxHostsNumber());
            System.out.println("\nMaximum number of hosts: " + address.getMaxHostsNumber());


            //set original output and error stream back again
            System.setOut(originalOut);
            System.setErr(originalErr);

            //pinging without saving results to a file
            address.ping();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
