package server;

import com.j.client.TagTrackingClient;
import com.j.server.TagTrackingServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class TagTrackingServerTest {
    private static int port;
    private static final String LOCAL_HOST = "127.0.0.1";

    @BeforeClass
    public static void start() throws InterruptedException, IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();

        Executors.newSingleThreadExecutor().submit(() -> new TagTrackingServer().start(port));
        Thread.sleep(500);
    }

    @Test
    public void givenClientOneTest() {
        TagTrackingClient tagTrackingClient = new TagTrackingClient();
        tagTrackingClient.startConnection(LOCAL_HOST, port);

        String msg1 = tagTrackingClient.sendMessage("hello");
        String msg2 = tagTrackingClient.sendMessage("world");
        String terminate = tagTrackingClient.sendMessage(".");

        assertEquals(msg1, "hello");
        assertEquals(msg2, "world");
        assertEquals(terminate, "bye");
        tagTrackingClient.stopConnection();
    }

    @Test
    public void givenTwoClientsTest() {
        TagTrackingClient c1 = new TagTrackingClient();
        c1.startConnection(LOCAL_HOST, port);

        TagTrackingClient c2= new TagTrackingClient();
        c2.startConnection(LOCAL_HOST, port);

        String msgC1 = c1.sendMessage("hello");
        String msgC2 = c2.sendMessage("world");
        String c1terminate = c1.sendMessage(".");
        String c2terminate = c2.sendMessage(".");

        assertEquals(msgC1, "hello");
        assertEquals(msgC2, "world");
        assertEquals(c1terminate, "bye");
        assertEquals(c2terminate, "bye");

        c1.stopConnection();
        c2.stopConnection();
    }


}