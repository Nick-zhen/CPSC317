import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.*;

public class DictConnection {
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    private String setDict;

    private boolean debugMode;

    public DictConnection(String server, int port, boolean debugOn) {
        try {

            clientSocket = new Socket();
            // set connect to socket timeout for 5 seconds
            clientSocket.connect(new InetSocketAddress(server, port), 5000);

            debugMode = debugOn;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            setDict = "*";


            // create timer for the case, connect successfully but no response (error 999)
            ExecutorService service = Executors.newSingleThreadExecutor();

            try {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String fromServer = in.readLine();

                            if (debugMode) {
                                System.out.print("<-- ");
                                System.out.println(fromServer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Future f = service.submit(r);
                // get out if there is no response for 10 seconds
                f.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                in = null;
                out = null;
                clientSocket = null;
                System.err.println("999 Processing error. Timed out while waiting for a response.");
            }


        } catch (UnknownHostException e) {
            System.err.println("999 Not a valid host name: " + server);
        } catch (ConnectException e) {
            System.err.println("920 Control connection to " + server + " on port " + port + " failed to open.");
        } catch (SocketTimeoutException e) {
            clientSocket = null;
            System.err.println("920 Control connection to " + server + " on port " + port + " failed to open.");
        } catch (IOException e) {
            clientSocket = null;
            System.err.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    public void dict() throws NullPointerException {

        out.println("SHOW DB");
        try {
            String fromServer = in.readLine();

            // if fromServer lost the connection
            if (fromServer == null) throw new IOException();

            if (debugMode) System.out.println("> SHOW DB");
            if (debugMode) {
                System.out.print("<-- ");
                System.out.println(fromServer);
            }

            printFromServerUntil250ok();
        } catch (IOException e) {
            in = null;
            clientSocket = null;
            System.err.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    public void setDict(String dictionary) {
        this.setDict = dictionary;
    }

    public String getDict() { return this.setDict; }

    public void defineWord(String word) throws NullPointerException {
        out.println("DEFINE " + this.setDict + " " + word);

        try {
            String fromServer = in.readLine();

            // if fromServer lost the connection
            if (fromServer == null) throw new IOException();

            if (fromServer.startsWith("550")) {
                // 550: invalid database
                System.out.println("999 Processing error. Invalid dictionary.");
            } else if (fromServer.startsWith("552")) {
                // 552: no match words
                System.out.println("****No definition found****");
            } else if (fromServer.startsWith("150")) {
                // 150: find words successfully
                if (debugMode) {
                    System.out.println("> DEFINE " + this.setDict + " " + word);
                    System.out.print("<-- ");
                    System.out.println(fromServer);
                }

                printFromServerUntil250ok();
            }
        } catch (IOException e) {
            in = null;
            clientSocket = null;
            System.out.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    public void matchWord(String word) throws NullPointerException {

        out.println("MATCH " + this.setDict +  " exact " + word);

        try {
            String fromServer = in.readLine();

            // if fromServer lost the connection
            if (fromServer == null) throw new IOException();

            if (fromServer.startsWith("550")) {
                // 550: invalid database
                System.out.println("999 Processing error. Invalid dictionary.");
            } else if (fromServer.startsWith("552")) {
                // 552: no match words
                System.out.println("****No matching word(s) found****");
            } else if (fromServer.startsWith("152")) {
                // 152: find words successfully
                if (debugMode) {
                    System.out.println("> MATCH " + this.setDict +  " exact " + word);
                    System.out.print("<-- ");
                    System.out.println(fromServer);
                }

                printFromServerUntil250ok();
            }
        } catch (IOException e) {
            in = null;
            clientSocket = null;
            System.out.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    // match wn prefix air
    public void prefixWord(String word) throws NullPointerException {
        out.println("MATCH " + this.setDict + " prefix " + word);

        try {

            String fromServer = in.readLine();

            // if fromServer lost the connection
            if (fromServer == null) throw new IOException();

            if (fromServer.startsWith("550")) {
                // 550: invalid database
                System.out.println("999 Processing error. Invalid dictionary.");
            } else if (fromServer.startsWith("552")) {
                // 552: no match words
                System.out.println("****No matching word(s) found****");
            } else if (fromServer.startsWith("152")) {
                // 152: find prefix words successfully
                if (debugMode) {
                    System.out.println("> MATCH " + this.setDict +  " prefix " + word);
                    System.out.print("<-- ");
                    System.out.println(fromServer);
                }

                printFromServerUntil250ok();
            }
        } catch (IOException e) {
            in = null;
            clientSocket = null;
            System.out.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    public void close() throws NullPointerException {
        try {

            out.println("QUIT");

            String fromServer = in.readLine();
            // if server wait too much time, it will just return null and will not connect with the clientSocket
            if (fromServer == null) throw new IOException();

            if (debugMode) System.out.println("> QUIT");

            if (debugMode) {
                System.out.print("<-- ");
                System.out.println(fromServer.trim());
            }

            in.close();
            out.close();
            clientSocket.close();
            clientSocket = null;

        } catch (IOException e) {
            in = null;
            clientSocket = null;
            System.err.println("925 Couldn't get I/O for the connection to the host.");
        }
    }

    public void quit() throws NullPointerException {

//        System.out.println("clientSocket.isClosed(): " + clientSocket.isClosed());
//        System.out.println("clientSocket.isConnected(): " + clientSocket.isConnected());
        if (clientSocket.isConnected()) this.close();

    }

    private void printFromServerUntil250ok() throws NullPointerException {
        try {
            String fromServer;
            while (!(fromServer = in.readLine()).startsWith("250")) {
                if (fromServer.startsWith("151")){
                    if (debugMode) {
                        System.out.print("<-- ");
                        System.out.println(fromServer.trim());
                    }

                    // @ db "NAME"    eg. @ wn "WordNet (r) 3.0 (2006)"
                    String[] line = fromServer.trim().split("\"");
                    System.out.println("@ " + line[2].trim() + " \"" + line[3]  + "\"");
                } else {
                    System.out.println(fromServer.trim());
                }
            }
            // print 250 ok
            if (debugMode) {
                System.out.print("<-- ");
                System.out.println(fromServer);
            }
        } catch (IOException e) {
            System.err.println("925 Couldn't get I/O for the connection to the host.");
        }
    }
}