package com.astra.sirius;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SiriusClient {

    public String sendTask(
            String host,
            int port,
            SiriusTask task
    ) {

        try {

            Socket socket = new Socket(host, port);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            writer.println(task.encode());

            String response = reader.readLine();

            socket.close();

            if (response == null) {
                return "ERROR:NO_RESPONSE";
            }

            return response;

        } catch (Exception e) {

            return "ERROR:" + e.getMessage();
        }
    }
}
