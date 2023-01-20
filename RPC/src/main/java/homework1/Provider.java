package homework1;
import javax.xml.ws.Endpoint;

public class Provider {
    private static final String URL = "http://localhost:5050/server";

    public static void main(String[] args) {

        server cloud = new server();
        System.out.println("Publishing Cloud Service...");
        Endpoint.publish(URL, cloud);
        System.out.println("Cloud Service Published");
    }
}
