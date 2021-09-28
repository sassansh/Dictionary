package ca.ubc.cs317.dict.net;

import ca.ubc.cs317.dict.model.Database;
import ca.ubc.cs317.dict.model.Definition;
import ca.ubc.cs317.dict.model.MatchingStrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Created by Jonatan on 2017-09-09.
 */
public class DictionaryConnection {

    private static final int DEFAULT_PORT = 2628;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /** Establishes a new connection with a DICT server using an explicit host and port number, and handles initial
     * welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @param port Port number used by the DICT server
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     * don't match their expected value.
     */
    public DictionaryConnection(String host, int port) throws DictConnectionException {
        try {
            // Create a new Socket
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Get welcome message from server
            Status status = Status.readStatus(in);
            if (status.getStatusCode() != 220) throw new DictConnectionException();
            System.out.printf("Connected to %s:%d%n", host, port);
            // Print server welcome response
            System.out.println(status.getDetails());
        } catch (Exception e) {
            System.out.printf("Failed to connect to %s:%d%n", host, port);
            throw new DictConnectionException();
        }
    }

    /** Establishes a new connection with a DICT server using an explicit host, with the default DICT port number, and
     * handles initial welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     * don't match their expected value.
     */
    public DictionaryConnection(String host) throws DictConnectionException {
        this(host, DEFAULT_PORT);
    }

    /** Sends the final QUIT message and closes the connection with the server. This function ignores any exception that
     * may happen while sending the message, receiving its reply, or closing the connection.
     */
    public synchronized void close() {
        try {
            // Clear all old input
            clearInputStream();
            // Send QUIT message to server
            out.println("QUIT");
            // Get quit response from server
            Status status = Status.readStatus(in);
            System.out.println(status.getDetails());
            // Close socket
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            in = null;
            out = null;
            socket = null;
            System.out.println("Connection has been closed!");
        } catch (Exception e) {
            // Do nothing
        }
    }

    /** Requests and retrieves all definitions for a specific word.
     *
     * @param word The word whose definition is to be retrieved.
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 definitions in the first database that has a definition for the word should be used
     *                 (database '!').
     * @return A collection of Definition objects containing all definitions returned by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Collection<Definition> getDefinitions(String word, Database database) throws DictConnectionException {
        Collection<Definition> set = new ArrayList<>();
        // Clear all old input
        clearInputStream();
        // Get database list from server
        String dbName = database.getName();
        out.println("DEFINE " + dbName + " " + "\"" + word + "\"");
        // Check response code from server
        Status status = Status.readStatus(in);
        System.out.println(status.getDetails());
        // Status code 550, Invalid Database (return empty list)
        if (status.getStatusCode() == 550) return set;
        // Status code 552, No Match (return empty list
        if (status.getStatusCode() == 552) return set;
        // Unexpected code, throw error
        if (status.getStatusCode() != 150) {
            throw new DictConnectionException();
        }
        // Parse definitions
        String response;
        try {
            while ((response = in.readLine()) != null && !DictStringParser.splitAtoms(response)[0].equals("250")) {
                String[] parsedResponse = DictStringParser.splitAtoms(response);
                Definition newDef;
                if (parsedResponse[0].equals("151")) {
                    newDef = new Definition(parsedResponse[1], parsedResponse[2]);
                    while ((response = in.readLine()) != null && !response.equals(".")) {
                        newDef.appendDefinition(response);
                    }
                    set.add(newDef);
                }
            }
        } catch (Exception e) {
            System.out.printf("Failed to get Definition list");
            throw new DictConnectionException();
        }

        return set;
    }

    /** Requests and retrieves a list of matches for a specific word pattern.
     *
     * @param word     The word whose definition is to be retrieved.
     * @param strategy The strategy to be used to retrieve the list of matches (e.g., prefix, exact).
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 matches in the first database that has a match for the word should be used (database '!').
     * @return A set of word matches returned by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Set<String> getMatchList(String word, MatchingStrategy strategy, Database database) throws DictConnectionException {
        Set<String> set = new LinkedHashSet<>();
        // Clear all old input
        clearInputStream();
        // Get database list from server
        String dbName = database.getName();
        String sName = strategy.getName();
        out.println("MATCH " + dbName + " " + sName + " " + "\"" + word + "\"");
        // Check response code from server
        Status status = Status.readStatus(in);
        System.out.println(status.getDetails());
        // Status code 550, Invalid Database (return empty list)
        if (status.getStatusCode() == 550) return set;
        // Status code 551, Invalid Strategy (return empty list)
        if (status.getStatusCode() == 551) return set;
        // Status code 552, No Match (return empty list)
        if (status.getStatusCode() == 552) return set;
        // Unexpected code, throw error
        if ((status.getStatusCode() != 152)) {
            throw new DictConnectionException();
        }
        // Parse response
        String response;
        try {
            while ((response = in.readLine()) != null && !DictStringParser.splitAtoms(response)[0].equals("250") && !response.equals(".")) {
                String[] parsedResponse = DictStringParser.splitAtoms(response);
                set.add(parsedResponse[1]);
            }
        } catch (Exception e) {
            System.out.printf("Failed to get Match list");
            throw new DictConnectionException();
        }

        return set;
    }

    /** Requests and retrieves a map of database name to an equivalent database object for all valid databases used in the server.
     *
     * @return A map of Database objects supported by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Map<String, Database> getDatabaseList() throws DictConnectionException {
        Map<String, Database> databaseMap = new HashMap<>();
        // Clear all old input
        clearInputStream();
        // Get database list from server
        out.println("SHOW DATABASES");
        // Check response code from server
        Status status = Status.readStatus(in);
        System.out.println(status.getDetails());
        // Status code 554, No databases present (return empty list)
        if (status.getStatusCode() == 554) return databaseMap;
        // Unexpected code, throw error
        if (status.getStatusCode() != 110) {
            throw new DictConnectionException();
        }
        // Parse databases
        String response;
        try {
            while ((response = in.readLine()) != null && !response.equals(".")) {
                String[] dbParsed = DictStringParser.splitAtoms(response);
                Database newDB = new Database(dbParsed[0], dbParsed[1]);
                databaseMap.put(dbParsed[0], newDB);
            }
        } catch (Exception e) {
            System.out.printf("Failed to get database list");
            throw new DictConnectionException();
        }
        return databaseMap;
    }

    /** Requests and retrieves a list of all valid matching strategies supported by the server.
     *
     * @return A set of MatchingStrategy objects supported by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Set<MatchingStrategy> getStrategyList() throws DictConnectionException {
        Set<MatchingStrategy> set = new LinkedHashSet<>();
        // Clear all old input
        clearInputStream();
        // Get strategy list from server
        out.println("SHOW STRATEGIES");
        // Check response code from server
        Status status = Status.readStatus(in);
        System.out.println(status.getDetails());
        // Status code 555, No strategies available (return empty list)
        if (status.getStatusCode() == 555) return set;
        // Unexpected code, throw error
        if (status.getStatusCode() != 111) {
            throw new DictConnectionException();
        }
        // Parse strategies
        String response;
        try {
            while ((response = in.readLine()) != null && !response.equals(".")) {
                String[] strategyParsed = DictStringParser.splitAtoms(response);
                MatchingStrategy newStrategy = new MatchingStrategy(strategyParsed[0], strategyParsed[1]);
                set.add(newStrategy);
            }
        } catch (Exception e) {
            System.out.printf("Failed to get strategy list");
            throw new DictConnectionException();
        }
        return set;
    }

    private void clearInputStream() throws DictConnectionException {
        // Clear all old input
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.printf("Failed to clear input stream");
            throw new DictConnectionException();
        }
    }
}
