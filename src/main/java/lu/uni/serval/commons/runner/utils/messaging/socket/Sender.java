package lu.uni.serval.commons.runner.utils.messaging.socket;

import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Sender {
    private Sender() {}

    public static void sendFrame(int port, Frame frame) throws IOException {
        sendFrame("localhost", port, frame);
    }

    private static void sendFrame(String host, int port, Frame frame) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            sendFrame(socket, frame);
        }
    }

    private static void sendFrame(Socket socket, Frame frame) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(frame);
        out.flush();
    }
}