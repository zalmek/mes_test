package Server;

import org.sqlite.SQLiteDataSource;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;

public class Main {
    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
    }

    public static void main(String[] args) {
        out.println("Server started!");
        SQLiteDataSource dataSource = new SQLiteDataSource();
        //String address = "46.242.9.139";
        int port = 23456;
        dataSource.setUrl("jdbc:sqlite:pebw_messeges.sqlite");
        try (Connection con  = dataSource.getConnection()){
            if (!con.isValid(5))
                out.println("Connection is invalid.");
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS Messages(" +
                        "id INTEGER PRIMARY KEY," +
                        "from_id INTEGER NOT NULL," +
                        "to_id INTEGER NOT NULL," +
                        "message TEXT DEFAULT ' ')");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS Staff(" +
                        "id INTEGER PRIMARY KEY," +
                        "uid INTEGER," +
                        "group_member TEXT NOT NULL," +
                        "status INTEGER NOT NULL," +
                        "FSP TEXT DEFAULT ' ')");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Enumeration<NetworkInterface> nets = null;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        for (NetworkInterface netint : Collections.list(nets)) {
            try {
                displayInterfaceInformation(netint);
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
        }


        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName("46.242.9.139"));) {
            while (true){

                Session session = new Session(server.accept());
                session.start();
                session.join();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        out.println("Success");
    }
}
