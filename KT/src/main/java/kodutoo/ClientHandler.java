package kodutoo;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;


public class ClientHandler extends HttpServlet {

    private boolean isSealed = false;
    private BigInteger outputNumber = BigInteger.ZERO;
    private Enumeration<String> input;
    private ArrayList<String> threads = new ArrayList<String>();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        input = request.getParameterNames();
        String value = input.nextElement();

        if (input.hasMoreElements()) {
            writeOutput(response, "Enter only one parameter");
        } else if (validateInt(value)){
            if(checkSeal()) {
                waitMethod();
            }

            threads.add("run");
            outputNumber = outputNumber.add(BigInteger.valueOf(Long.parseLong(value)));

            waitMethod();
            setSeal(true);

            writeOutput(response, String.valueOf(outputNumber));

            if (threads.isEmpty()){
                outputNumber = BigInteger.ZERO;
                setSeal(false);
                wakeMethod();
            }
        } else if (value.equals("end")) {
            wakeMethod();
            writeOutput(response, String.valueOf(outputNumber));

        } else {
            writeOutput(response, "Enter an integer or end");
        }
    }

    private void writeOutput(HttpServletResponse response, String output) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.write(output);
    }

    private synchronized void waitMethod(){
        try {
            this.wait();
            threads.remove(0);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void wakeMethod(){
        this.notifyAll();
    }

    private boolean validateInt(String value){
        try {
            BigInteger.valueOf(Long.parseLong(value));
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkSeal(){
        return isSealed;
    }

    private void setSeal(boolean seal){
        isSealed = seal;
    }
}