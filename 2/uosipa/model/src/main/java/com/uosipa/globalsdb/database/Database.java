package com.uosipa.globalsdb.database;

import com.intersys.globals.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class Database {
    private static final Properties properties = new Properties();

    private static final String GLOBALS_VERSION = ProductInfo.getProductVersion();

    private static final Connection connection;

    static {
        try {
            properties.load(Database.class.getResourceAsStream("/database.properties"));
        } catch (IOException e) {
            throw new IllegalStateException("Can't load database properties.", e);
        }

        connection = ConnectionContext.getConnection();

        if (!connection.isConnected()) {
            connection.connect(
                    properties.getProperty("globals.namespace"),
                    properties.getProperty("globals.user"),
                    properties.getProperty("globals.password")
            );
        }
    }

    public static void write(String key, String value) {
        NodeReference nodeReference = connection.createNodeReference(key);
        nodeReference.set(value);
    }

    public static String read(String key) {
        return connection.createNodeReference(key).getString();
    }

    public static String getVersion() {
        return GLOBALS_VERSION;
    }

    public static boolean isNodeExist(String key) {
        return connection.createNodeReference(key).exists();
    }

    public static void addToNode(String value, Object... subscripts) {
        NodeReference nodeReference = connection.createNodeReference("logs");

        for (Object subscript : subscripts) {
            if (subscript instanceof Long) {
                nodeReference.appendSubscript((Long) subscript);
            } else {
                nodeReference.appendSubscript((String) subscript);
            }
        }

        //connection.releaseAllLocks();
        connection.startTransaction();
        try {
            //nodeReference.acquireLock(NodeReference.EXCLUSIVE_LOCK, NodeReference.LOCK_INCREMENTALLY);

            ValueList valueList;
            if (nodeReference.exists()) {
                valueList = nodeReference.getList();
            } else {
                valueList = connection.createList();
            }

            System.out.println(valueList.length());
            for (int i = 0; i < valueList.length(); ++i) {
                System.out.println(valueList.getNextString());
            }
            System.out.println("NEW VALUE = " + value);

            valueList.append(value);

            System.out.println("AFTER APPENDING");
            nodeReference.set(valueList);

            //nodeReference.releaseLock(NodeReference.EXCLUSIVE_LOCK, NodeReference.RELEASE_AT_TRANSACTION_END);

            connection.commit();
        } catch (GlobalsException e) {
            e.printStackTrace();
            connection.rollback();
        }
    }

    public static ValueList getNodeValue(Object... subscripts) {
        NodeReference nodeReference = connection.createNodeReference("logs");
        for (Object subscript : subscripts) {
            if (subscript instanceof Long) {
                nodeReference.appendSubscript((Long) subscript);
            } else {
                nodeReference.appendSubscript((String) subscript);
            }
        }

        return nodeReference.getList();
    }

    public static List<String> getAllSubscripts(String... subscripts) {
        NodeReference nodeReference = connection.createNodeReference("logs");

        List<String> result = new ArrayList<String>();
        for (String subscript : subscripts) {
            nodeReference.appendSubscript(subscript);
        }

        String subscript = "";
        do {
            subscript = nodeReference.nextSubscript(subscript);
            if (subscript.length() > 0)
                result.add(subscript);
        } while (subscript.length() > 0);

        return result;
    }
}
