package com.intersys.globals.samples;
import com.intersys.globals.*;
/**
 * Sample provides examples of the usage of the classes and interfaces of the
 * globals API: {@link ConnectionContext}, {@link Connection},
 * {@link NodeReference}, {@link ValueList}, {@link GlobalsDirectory}, and
 * {@link GlobalsException}.
 * <p>
 * Refer to {@link NodeReference} for a general introduction to the globals persistence
 * model, and operations that can be performed on globals.
 * <p>
 * Prerequisites for running applications which use the globals API:
 * <p>
 * 1. Download and install the Globals product from www.globalsdb.org
 * <p>
 * 2. Globals requires Java version 1.6 or above.
 * <p>
 * 3. Set the following environment variables in the environment from which the apps
 * will be run:
 * <p>
 * <code>GLOBALS_HOME</code> - Pathname of the root of the Globals installation.
 * <p>
 * <code>PATH</code> (Windows), <code>LD_LIBRARY_PATH</code> (Unix) or
 * <code>DYLD_LIBRARY_PATH</code> - Must include <code>$GLOBALS_HOME</code>/bin</code> .
 * <p>
 * <code>CLASSPATH</code> - Must include
 * <code>$GLOBALS_HOME/dev/java/lib/JDK16/globalsdb.jar</code> .
 * <p>
 * On Unix only: <code>LD_PRELOAD</code> should be set to the pathname of the file
 * libjsig.so within your Java installation.  This is not required for simple applications
 * such as this Sample application, but may be required for applications which
 * incorporate 3rd party software that sets up signal handlers.  It is safest to always
 * set it when running applications that use the globals API.
 * <p>
 * To extract the source code for this sample application:
 * <p>
 * On Windows:
 * <p>
 * <code>jar -xf %GLOBALS_HOME%\dev\java\lib\JDK16\globalsdb.jar com\intersys\globals\samples\Sample.java</code>
 * <p>
 * On Unix:
 * <p>
 * <code>jar -xf $GLOBALS_HOME/dev/java/lib/JDK16/globalsdb.jar com/intersys/globals/samples/Sample.java</code>
 * <p>
 * To run this application from the command line:
 * <p>
 * <code>java -Xss1024k com.intersys.globals.samples.Sample -pause</code>
 * <p>
 * Where:
 * <p>
 * <code>-Xss1024k</code> causes Java to use a larger than default stack size.  This is not
 * always needed, but if Java throws a stack overflow exception without this argument,
 * add it to the command line.
 * <p>
 * <code>-pause</code> causes the Sample application to pause after each section, so you
 * can examine the output, and press return when ready to continue.  By default,
 * Sample runs to completion without pausing, and some output will scroll off the screen.
 *
 */
/*
 * Copyright (c) 2011 InterSystems, Corp. Cambridge, Massachusetts, U.S.A.  All
 * rights reserved. Confidential, unpublished property of InterSystems.
 */

public class Sample {
   private static boolean pause = false;
   public static void main(String[] args) {
      for (int i = 0; i < args.length; i++) {
         if (args[i].equals("-pause")) {
            pause = true;
         }
      }
      /* Get a Connection instance.  Since this is the first time
       * ConnectionContext.getConnection() is called during execution of this
       * application, a new Connection instance is created, not yet connected
       * to the Globals database.  If getConnection() is called again, the same
       * Connection instance is returned.
       */
      Connection connection = ConnectionContext.getConnection();
      try {
         /* Connect to Globals database. Environment variable GLOBALS_HOME must be
          * set to specify the path of the database installation's root directory.
          * Since, in general, the Connection instance might already be connected,
          * it is good practice to check by calling Connection.isConnected(), to
          * avoid Connection.connect throwing an exception if called when already
          * connected.
          */
         if (!connection.isConnected()) {
            connection.connect();
         }

         /* Set some unsubscripted globals to various types.
          *
          * Create a node reference for a global called "myInt1".
          * Note: The global name passed as a parameter to CreateNodeReference(String)
          * does not need to be the same as the name of the NodeReference variable,
          * but we use this in these examples as a convenient convention.
          */
         NodeReference myInt1 = connection.createNodeReference("myInt1");
         /* Set its value to 86.  This stores the integer value 86 in the
          * root node of the global "myInt1" in the Globals database.
          */
         myInt1.set(86);
         /* Create another node reference, no name specified yet.
          */
         NodeReference myInt2 = connection.createNodeReference();
         /* Make it refer to a global called "myInt2".
          */
         myInt2.setName("myInt2");
         /* Set its value to 100.
          */
         myInt2.set(100);
         /* Create a node reference for a global called "myLong".
          */
         NodeReference myLong = connection.createNodeReference("myLong");
         /* Set its value to 123456789012345678.
          */
         myLong.set(123456789012345678L);
         /* Create a node reference for a global called "myDouble".
          */
         NodeReference myDouble = connection.createNodeReference("myDouble");
         /* Set its value to 3.14159.
          */
         myDouble.set(3.14159);
         /* Create a node reference for a global called "myString".
          */
         NodeReference myString = connection.createNodeReference("myString");
         /* Set its value to "Hello world".
          */
         myString.set("Hello world");
         /* Create a node reference for a global called "myBytes".
          */
         NodeReference myBytes = connection.createNodeReference("myBytes");
         /* Set its value to the array of byte values 'h', 'e', 'l', 'l', 'o'.
          */
         byte[] bytesVal = new byte[5];
         bytesVal[0] = 'h';
         bytesVal[1] = 'e';
         bytesVal[2] = 'l';
         bytesVal[3] = 'l';
         bytesVal[4] = 'o';
         myBytes.set(bytesVal);

         System.out.println("\n*** Getting Global Values of Known Type ****************");

         /* Now retrieve the data we stored in the globals above, getting each
          * value using the appropriate type-specific get method.  This is the most
          * efficient way to retrieve data, if you know its type.
          */

         System.out.println("\nGetting values as the same type as which they were stored:\n");

         /* Get the value of the global "myInt1" as an int.
          */
         int intVal = myInt1.getInt();
         System.out.println("Value of myInt1: " + intVal);
         /* Note that the same NodeReference instance can be made to refer to a
          * different global by resetting its global name.
          */
         myInt1.setName("myInt2");
         /* Get and display the value of the global "myInt2" as an int, using
          * NodeReference instance myInt1.
          */
         System.out.println("Value of myInt2: " + myInt1.getInt());
         /* Get the value of the global "myLong" as a long.
          */
         long longVal = myLong.getLong();
         System.out.println("Value of myLong: " + longVal);
         /* Get and display the value of the global "myDouble" as a double.
          */
         System.out.println("Value of myDouble: " + myDouble.getDouble());
         /* Get and display the value of the global "myString" as a String.
          */
         System.out.println("Value of myString: " + myString.getString());
         /* Get the value of the global "myBytes" as an instance of byte[].
          */
         bytesVal = myBytes.getBytes();
         System.out.println("Value of myBytes retrieved as byte[]: " +
                            bytesVal[0] + " " + bytesVal[1] + " " + bytesVal[2] + " "
                            + bytesVal[3] + " " + bytesVal[4]);

         /* If type-specific get methods are used to retrieve values that were stored
          * as a different type, conversions are automatically performed, as in the
          * following examples:
          */
         System.out.println("\nGetting values as a different type, with automatic conversions:\n");
         /* Get the value of the global "myBytes" again, this time as a String.  Note that
          * each byte is automatically converted to a char within the string.
          */
         String bytesAsString = myBytes.getString();
         System.out.println("Value of myBytes retrieved as String: " + bytesAsString);
         /* Set the global name of node reference myInt1 back to "myInt1", since we
          * previously re-set it to "myInt2".
          */
         myInt1.setName("myInt1");
         /* Get the value of the global "myInt1" as a double.
          */
         System.out.println("Value of myInt1 retrieved as double: " + myInt1.getDouble());
         /* Get the value of the global "myDouble" as an int. Note that it is
          * automatically truncated.
          */
         System.out.println("Value of myDouble retrieved as int: " + myDouble.getInt());
         /* When a value stored as a String is retrieved as an int or long, it is
          * automatically converted to an integer by parsing the initial characters
          * until it finds a character that isn't part of a number (leading '+',
          * leading '-', a digit, or the first instance of decimal point).  If the
          * leading portion of the string doesn't look like a number, it is converted
          * to the integer value 0.
          */
         System.out.println("Value of \"Hello world\" as an int: " + myString.getInt());
         myString.set("-20 degrees");
         System.out.println("Value of \"-20 degrees\" as an int: " + myString.getInt());
         /* When alues stored as numeric types are retrieved as String, they are
          * converted into the String representation of the numeric value.
          */
         System.out.println("Value of myInt1 retrieved as String: " + myInt1.getString());
         System.out.println("Value of myLong retrieved as String: " + myLong.getString());
         /* Note that when a value stored as a double is retrieved as String, there is
          * a slight conversion error.  This is because Java double, and double as stored
          * in the Globals database, is binary floating point (IEEE double), but
          * the String representation is decimal, so there is inevitable binary-to-decimal
          * conversion error.
          */
         System.out.println("Value of myDouble (3.14159) retrieved as String: " + myDouble.getString());

         /* Some combinations of stored type / retrieved type produce meaningless
          * conversion results.  These cases should be avoided.  If you aren't sure
          * what type was stored in a global node, it is better to retrieve it as an
          * Object, and use the Java instanceof operator to determine its type,
          * as shown in the next section of this Sample.  Here are cases of
          * explicit conversions to avoid:
          */
         System.out.println("\nConversions to be avoided:\n");
         /* A value stored as long may be too large to fit in an int, producing a
          * meaningless conversion result.
          */
         System.out.println("Value of myLong (123456789012345678) retrieved as int: "
                            + myLong.getInt());
         /* A value stored as a String will produce a meaningless result when retrieved
          * as a double, even if the leading portion of the String looks like a number.
          */
         System.out.println("Value of myString (\"-20 degrees\") retrieved as double: "
         + myString.getDouble());

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Getting Global Values of Unknown Type **************\n");

         /* To retrieve global values of unknown type, use the method
          * NodeReference.getObject() to retrieve the value as an Object, and
          * use the Java instanceof operator to determine its type.
          * As a first simple example, get the value of the global "myString", which
          * was stored as a String.
          */
         Object objectVal = myString.getObject();
         if (objectVal instanceof java.lang.String)
            System.out.println("Value of myString is a String: " + objectVal);
         else
            System.out.println("Value of myString is not a String.");

         /* Go through the same exercise with an value that was stored as an int.
          */
         objectVal = myInt1.getObject();
         if (objectVal instanceof java.lang.Integer)
            System.out.println("Value of myInt1 is an Integer: " + objectVal);
         else
            System.out.println("Value of myInt1 is not an Integer.");

         /* Go through the same exercise with a value that was stored as a double.
          */
         objectVal = myDouble.getObject();
         if (objectVal instanceof java.lang.Double)
            System.out.println("Value of myDouble is a Double: " + objectVal);
         else
            System.out.println("Value of myDouble is not a Double.");

         /* When a value that was stored as a long is retrieved as an Object,
          * the Object will be an instance of Integer if the value fits within the
          * range of Integer, else it will be an instance of Long.
          */
         objectVal = myLong.getObject();
         if (objectVal instanceof java.lang.Long)
            System.out.println("Value of myLong is a Long: " + objectVal);
         else if (objectVal instanceof java.lang.Integer)
            System.out.println("Value of myLong is an Integer: " + objectVal);
         else
            System.out.println("Value of myLong is not a Long or an Int.");
         /* Set the global "myLong" as a long whose value is within the range of integer.
          */
         myLong.set((long)1942);
         /* Now retrieve it again as an Object, this time it will be an instance of
          * Integer.
          */
         objectVal = myLong.getObject();
         if (objectVal instanceof java.lang.Long)
            System.out.println("Value of myLong is a Long: " + objectVal);
         else if (objectVal instanceof java.lang.Integer)
            System.out.println("Value of myLong is an Integer: " + objectVal);
         else
            System.out.println("Value of myLong is not a Long or an Int.");

         /* When a value stored as byte[] is retrieved as an Object, the Object will
          * be an instance of String, not an instance of byte[].  This is the case
          * because the internal storage format for String and byte[] in the Globals
          * database is identical.
          */
         objectVal = myBytes.getObject();
         if (objectVal instanceof java.lang.String)
            System.out.println("Value of myBytes is a String: " + objectVal);
         else if (objectVal.getClass().getName().equals("[B"))
            System.out.println("Value of myBytes is an byte[]: " + objectVal);
         else
            System.out.println("Value of myBytes is not a String or byte[].");

         System.out.println("\nAttempting to get values from undefined global nodes:\n");
         /* If you attempt to get the value of a global node which is undefined
          * (that is, which does not contain any data), get methods which return
          * Object types return null, and get methods which return primitive types
          * throw UndefinedException.
          */
         NodeReference myUndefined = connection.createNodeReference("myUndefined");
         /* Attempt to get value of undefined node as an Object.
          */
         objectVal = myUndefined.getObject();
         if (objectVal == null)
            System.out.println("Value of myUndefined as Object is null.");
         else
            System.out.println("Value of myUndefined as Object is not null.");
         /* Attempt to get value of undefined node as a String.
          */
         String stringVal = myUndefined.getString();
         if (stringVal == null)
            System.out.println("Value of myUndefined as String is null.");
         else
            System.out.println("Value of myUndefined as String is not null.");
         /* Attempt to get value of undefined node as a String.
          */
         bytesVal = myUndefined.getBytes();
         if (bytesVal == null)
            System.out.println("Value of myUndefined as byte[] is null.");
         else
            System.out.println("Value of myUndefined as byte[] is not null.");
         /* Attempt to get value of undefined node as int.
          */
         try {
            intVal = myUndefined.getInt();
         } catch (UndefinedException e) {
            System.out.println(
               "Caught UndefinedException when attempting to get myUndefined as int");
         }
         /* Attempt to get value of undefined node as long.
          */
         try {
            longVal = myUndefined.getLong();
         } catch (UndefinedException e) {
            System.out.println(
               "Caught UndefinedException when attempting to get myUndefined as long");
         }
         /* Attempt to get value of undefined node as double.
          */
         double doubleVal;
         try {
            doubleVal = myUndefined.getDouble();
         } catch (UndefinedException e) {
            System.out.println(
               "Caught UndefinedException when attempting to get myUndefined as double");
         }

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Working with Subscripts ****************************\n");

         /* Up to this point, all of the examples have worked only with the root node
          * of each global.  By using subscripts, you can use use a node reference to
          * specify nodes which are descendants, or "subnodes", of the root node.  Each
          * additional subscript specifies a deeper level of subnodes.  Subscripts
          * can be specified as String, int, long, or double.  A node can contain
          * data and also have subnodes, or it can have subnodes without containing any
          * data.
          *
          * It is helpful to think of globals with subnodes as sparse multi-dimensional
          * arrays.  We will use pseudo-code notation with square brackets to represent
          * node references with subscripts.  For example:
          *
          *   addresses["Massachusetts", "Arlington", "Broadway", 113]
          *
          * is our preudo-code way of referring to the node of the global "addresses",
          * with 4 subscripts "Massachusetts", "Arlington", "Broadway", and 113.
          * Since this node has 4 subscripts, we can think of it as a 4th level node.
          * We number the subscripts from left to right, starting with 1, where
          * subscript 1 is the most significant (that is, nodes with 1 subscript are
          * at the highest level of subnode, directly under the root node of the global.
          *
          * There are two ways of specifying subscripts.  They can either be specified
          * as extra arguments (varargs) of a NodeReference method that operates on a
          * node, or they can be set as attributes of a NodeReference instance, so that
          * the NodeReference to refer to the node with those subscripts across multiple
          * method calls.  We'll start with an example using the first method.
          */

         /* Create a node reference for a global representing a collection of
          * people's addresses.
          */
         NodeReference addresses = connection.createNodeReference("addresses");
         /* Using a convention in which subscripts represent the component parts of
          * an address, and a person's name is the data of the node, store "John Smith"
          * as the value of addresses["Massachusetts", "Arlington", "Broadway", 113],
          * indicating that John Smith lives as 113 Broadway, Arlington, Massachusetts.
          */
         addresses.set("John Smith", "Massachusetts", "Arlington", "Broadway", 113);
         /* Get the name of the person who lives at 113 Broadway, Arlington, MA.
          */
         String person = addresses.getString("Massachusetts", "Arlington", "Broadway", 113);
         System.out.println("The person who lives at 113 Broadway, Arlington, MA is " +
                            person);

         /* Notice that using varargs to specify subscripts makes the code easy to
          * read and write, when you know exactly how many subscripts you need to
          * specify.  It is hard to use programmatically, though, if the number of
          * subscripts isn't known in advance.  It can also be less efficient, if
          * an application does several operations on a NodeReference instance with the
          * same set of subscripts, because for each method call the subscripts must
          * be passed through to the underlying implementation.
          *
          * Alternatively, subscripts can be specified one at a time, either
          * by calling appendSubscript to append an additional subscript to those
          * that have already been specified, or by calling setSubscript to set
          * the value of a subscript at a specified position.  You can determine
          * the current number of subscripts that have been set by calling
          * the getSubscriptCount() method, and you can remove trailing subscripts
          * by calling setSubscriptCount() to set the number of subscripts to a smaller
          * number.
          *
          * Continuing with our addresses example, let's use this approach to indicate
          * that Mary Jones lives at 235 Boylston Street, Apartment 4J, Boston,
          * Massachusetts, representing this address as:
          *  addresses["Massachusetts", "Boston", "Boylston Street", 235, "Apartment 4J"]
          */
         addresses.appendSubscript("Massachusetts");
         addresses.appendSubscript("Boston");
         addresses.appendSubscript("Boylston Street");
         addresses.appendSubscript(235);
         addresses.appendSubscript("Apartment 4J");
         addresses.set("Mary Jones");
         /* Get the name of the person who lives at 235 Boylston Street, Apartment 4J,
          * Boston, Massachusetts.  Note that we don't need to re-specify the subscripts.
          */
         person = addresses.getString();
         System.out.println(
            "The person who lives at 235 Boylston Street, Apartment 4J, Boston, MA is " +
            person + ".");
         System.out.println("addresses currently has " + addresses.getSubscriptCount() +
                            " subscripts.");

         /* We can mix the two approaches.  If we set some subscripts, and then
          * specify additional subscripts as varargs, the vararg subscripts identify
          * a subnode of the node that was specified by the pre-set subscripts.
          * This approach can be useful when performing a series of operations on
          * different subnodes of the same node, so that the left-most subscripts which
          * identify that parent node remain the same across the series of operations.
          *
          * Let's use that mixed approach to first re-set our NodeReference to
          * refer to Arlington, Massachusetts, and then use varargs to specifically
          * refer to 113 Broadway, in Arlington, Massachusetts.
          */
         addresses.setSubscriptCount(2);
         /* Subscript 1 is already set to Massachusett's, let's set subscript 2 to
          * "Arlington".
          */
         addresses.setSubscript(2, "Arlington");
         /* Get the name of the person who lives at 113 Broadway, Arlington, MA.
          */
         person = addresses.getString("Broadway", 113);
         System.out.println("The person who lives at 113 Broadway, Arlington, MA is " +
                            person + ".");
         /* Indicate that Linda Thompson lives at 32 Park Avenue, Arlington, MA.
          */
         addresses.set("Linda Thompson", "Park Avenue", 32);
         addresses.setSubscriptCount(0);
         person = addresses.getString("Massachusetts", "Arlington", "Park Avenue", 32);
         /* Remove all the pre-set subscripts from the NodeReference instance, and
          * then get the name of the person who lives at 32 Park Avenue, Arlington, MA,
          * specifying all 4 subscripts with varargs.
          */
         System.out.println("The person who lives at 32 Park Avenue, Arlington, MA. is " +
                            person + ".");

         System.out.println(
            "\nDetermining whether a node contains data, and whether it has subnodes:\n");

         /* The method exits() returns true if a node contains data, and false if it
          * does not.  For example, the nodes in which we stored people's names contain
          * data, but the nodes above them, representing intermediate components of
          * the addresses, do not.
          */
         System.out.println(
            "Does addresses[\"Massachusetts\", \"Arlington\", \"Park Avenue\", 32] contain data? " +
            addresses.exists("Massachusetts", "Arlington", "Park Avenue", 32));
         System.out.println(
            "Does addresses[\"Massachusetts\", \"Arlington\"] contain data? " +
            addresses.exists("Massachusetts", "Arlington"));

         /* The method hasSubnodes() returns true if a node has subnodes, and false
          * if it does not.  For example, the 1st, 2nd, and 3rd level nodes in our
          * address examples all have subnodes, but the nodes in which we stored the
          * people's names do not have subnodes:
          */
         System.out.println(
            "Does addresses[\"Massachusetts\", \"Arlington\", \"Park Avenue\", 32] have subnodes? " +
            addresses.hasSubnodes("Massachusetts", "Arlington", "Park Avenue", 32));
         System.out.println(
            "Does addresses[\"Massachusetts\", \"Arlington\"] have subnodes? " +
            addresses.hasSubnodes("Massachusetts", "Arlington"));

         /* A node which neither contains data nor has subnodes should return false
          * for both of these methods:
          */
         System.out.println("Does addresses[\"California\"] contain data? " +
                            addresses.exists("California"));
         System.out.println("Does addresses[\"California\"] have subnodes? " +
                            addresses.hasSubnodes("California"));

         System.out.println("\nGetting the current name and subscript values of a NodeReference:\n");
         /* You can determine the name of the global to which a NodeReference instance
          * refers by calling the method getName().
          */
         NodeReference myArray = connection.createNodeReference("myArray");
         System.out.println("NodeReference myArray refers to the global " +
                            myArray.getName());

         /* You can determine the current subscripts of a NodeReference instance
          * using the family of methods String get<type>Subscript(int subscriptNumber).
          *
          * Set subscripts of various types to demonstrate this:
          */
         myArray.appendSubscript("First subscript");
         myArray.appendSubscript(58);
         myArray.appendSubscript(3456789000L);
         myArray.appendSubscript(1.234);

         /* If you know which type was passed to set the subscript, you can use the
          * type-specific method:
          */
         String firstSubscript = myArray.getStringSubscript(1);
         System.out.println("First subscript is a string: " + firstSubscript);
         int secondSubscript = myArray.getIntSubscript(2);
         System.out.println("Second subscript is an int: " + secondSubscript);
         long thirdSubscript = myArray.getLongSubscript(3);
         System.out.println("Third subscript is a long: " + thirdSubscript);
         double fourthSubscript = myArray.getDoubleSubscript(4);
         System.out.println("Fourth subscript is a double: " + fourthSubscript);

         /* These methods thow a GlobalsException if called for a subscript
          * that was not set as the same type (except that it is ok to call
          * getLongSubscript to get subscript that was set as int):
          */
         System.out.println();
         try {
            System.out.println("Second subscript, set as int, gotten as long: " +
                               myArray.getLongSubscript(2));
            System.out.println("Seoncd subscript, set as int, gotten as String: " +
                               myArray.getStringSubscript(2));
         } catch (GlobalsException e) {
            System.out.println("Caught expected GlobalsException: " + e.getMessage());
         }
         System.out.println();

         /* If you don't know how many subscripts the NodeReference instance has,
          * or what their types are, you can use the methods getSubscriptCount()
          * and Object getObjectSubscript(int subscriptNumber):
          */
         Object unknownSubscript;
         for (int ii = 1; ii <= myArray.getSubscriptCount(); ii++) {
            unknownSubscript = myArray.getObjectSubscript(ii);
            System.out.print("Subscript " + ii + " is ");
            if (unknownSubscript instanceof String)
               System.out.print("a String");
            else if (unknownSubscript instanceof Integer)
               System.out.print("an Integer");
            else if (unknownSubscript instanceof Long)
               System.out.print("a Long");
            else if (unknownSubscript instanceof Double)
               System.out.print("a Double");
            else
               System.out.print(" unexpected type " + unknownSubscript.getClass().getName());
            System.out.println(", value: " + unknownSubscript);
         }

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** ValueList ******************************************\n");

         /* In addition to the basic Java types discussed above, NodeReference permits
          * storing and retrieving instances of ValueList.  This permits you to
          * efficiently and transparently serialize a list of values of any of the
          * types that can be directly stored in a global, and store the whole list
          * as the value of a global node.
          *
          * Create a ValueList instance:
          */
         ValueList myList1 = connection.createList();
         /* You can append items to a list one at a time, in individual calls to
          * append, which has overloadings for String, int, Integer, long, Long,
          * double, Double, and byte[].
          */
         System.out.println("Appending items to list one at a time:");
         myList1.append("Hello");
         myList1.append(86);
         myList1.append((long)3111222333L);
         myList1.append((double)3.14159);
         byte [] myByteArray = new byte[5];
         myByteArray[0] = 'h';
         myByteArray[1] = 'e';
         myByteArray[2] = 'l';
         myByteArray[3] = 'l';
         myByteArray[4] = 'o';
         myList1.append(myByteArray);
         /* Display the length (number of items) of the list, and store it
          * in myListGlobal["List 1"].
          */
         System.out.println("Storing first list, length " + myList1.length() +
                            ", in myListGlobal[\"List 1\"]");
         NodeReference myListGlobal = connection.createNodeReference("myListGlobal");
         myListGlobal.set(myList1, "List 1");

         /* You can also append multiple items to a list in a single method call.
          */
         System.out.println("Appending multiple items in one method call:");
         /* Clear the list, to start appending new items to an empty list.
          */
         myList1.clear();
         Integer myIntegerWrapper = new Integer(13);
         Long myLongWrapper = new Long(1984);
         Double myDoubleWrapper = new Double(1.2345);
         myList1.append("Goodbye", 57, (long)-4555666777L, (double)98.6, myIntegerWrapper, myLongWrapper, myDoubleWrapper, myByteArray);
         /* Display the length (number of items) of the second list, and store it
          * in myListGlobal["List 2"].
          */
         System.out.println("Storing first list, length " + myList1.length() +
                            ", in myListGlobal[\"List 2\"]");
         myListGlobal.set(myList1, "List 2");

         /* To get a ValueList instance from a global, call NodeReference.getList.  This
          * can return a new instance of ValueList:
          */
         System.out.println("Get the first stored list into a new list variable");
         ValueList myList2 = myListGlobal.getList("List 1");
         System.out.println("Length of retrieved first list: " + myList2.length());

         /* Or you can pass an existing ValueList instance as the first parameter
          * to getList, in which case the passed instance is reused, and is returned
          * containing the list of items retrieved from the global, replacing the
          * list of items that the reused instance contained previously:
          */
         System.out.println("Get the second stored list into a reused list variable");
         myListGlobal.getList(myList2, "List 2");
         System.out.println("Length of retrieved second list: " + myList2.length());

         /* The second approach can perform better in cases where you need to retrieve
          * many lists, but only use one of them at a time, because it avoids needing
          * to allocate a new underlying list buffer for each call to getList.
          * If you choose to use the first approach, returning a new instance of
          * ValueList, and if you are assigning the result to a variable which
          * already contained an instance of ValueList, best practice is to call
          * the previous instance's close() method before assigning the new instance,
          * to free its underlying resources and avoid a possible memory leak in
          * the underlying implementation.  For example, get myListGlobal["List 2"]
          * as a new instance of ValueList, after first calling close() on the previous
          * instance:
          */
         myList2.close();
         myList2 = myListGlobal.getList("List 2");

         /* Note that the caller must know that a ValueList was stored in the global
          * node in question, and must explicitly retrieve it by calling getList.
          * Internally in the Globals database's persistent storage, a ValueList
          * is stored as a string, so NodeReference.getObject will return an instance
          * of String.  If you call getList for a global node that was not stored
          * as a ValueList, a GlobalsException is thrown:
          */
         try {
            System.out.println(
               "Try calling getList for a global node that wasn't stored as ValueList...");
            myString.getList();
         } catch (GlobalsException e) {
            System.out.println("Caught expected GlobalsException: " + e.getMessage());
         }

         System.out.println("\nGetting items from a ValueList instance:\n");
         /* You can get individual items out of a ValueList instance either
          * individually or in a single call that returns Object[].  When getting
          * them individually, there is an implicit current read position, that
          * is initially before the first item in the list.  You call one of
          * the family of getNext<type> methods to get the next item, and this
          * advances the read position.
          *
          * If you know the types of the items, the most efficient way to get them
          * is to call a type-specific getNext<type> method, for example:
          */
         System.out.println("Get items of known primitive types from second retrieved list:");
         String outString1 = myList2.getNextString();
         if (outString1 == null)
            System.out.println("Item 1 is null");
         else
            System.out.println("Item 1: " + outString1);
         int outInt1 = myList2.getNextInt();
         System.out.println("Item 2: " + outInt1);
         long outLong1 = myList2.getNextLong();
         System.out.println("Item 3: " + outLong1);
         double outDouble1 = myList2.getNextDouble();
         System.out.println("Item 4: " + outDouble1);
         int outInt2 = myList2.getNextInt();
         System.out.println("Item 5: " + outInt2);
         long outLong2 = myList2.getNextLong();
         System.out.println("Item 6: " + outLong2);
         double outDouble2 = myList2.getNextDouble();
         System.out.println("Item 7: " + outDouble2);
         byte[] outBytes = myList2.getNextBytes();
         System.out.print("Item 8: ");
         for (int ii = 0; ii < outBytes.length; ii++) {
            System.out.print(outBytes[ii] + " ");
         }
         System.out.println();

         /* If you don't know the types of the items, call getNextObject():
          */
         System.out.println("\nGet items of unknown type from a list, individually:");
         for (int ii = 0; ii < myList1.length(); ii++) {
            Object obj = myList1.getNextObject();
            System.out.print("Item " + (ii + 1));
            if (obj == null) {
               System.out.println(" is null");
            }
            else {
               if (obj instanceof java.lang.String)
                  System.out.println(" is a String: " + (String)obj);
               else if (obj instanceof java.lang.Integer)
                  System.out.println(" is an Integer: " + ((Integer)obj).intValue());
               else if (obj instanceof java.lang.Long)
                  System.out.println(" is a Long: " + ((Long)obj).longValue());
               else if (obj instanceof java.lang.Double)
                  System.out.println(" is a Double: " + ((Double)obj).doubleValue());
               else {
                  System.out.println(" is of unexpected type " + obj.getClass().getName());
               }
            }
         }
         /* Or you can get all the items at Object[] in one method call, by calling
          * getAll():
          */
         System.out.println("\nGet items of unknown type from a list, as Object[]:");
         Object[] items = myList1.getAll();
         for (int ii = 0; ii < items.length; ii++) {
            System.out.print("Item " + (ii + 1));
            if (items[ii] == null) {
               System.out.println(" is null");
            }
            else {
               if (items[ii] instanceof java.lang.String)
                  System.out.println(" is a String: " + (String)items[ii]);
               else if (items[ii] instanceof java.lang.Integer)
                  System.out.println(" is an Integer: " + ((Integer)items[ii]).intValue());
               else if (items[ii] instanceof java.lang.Long)
                  System.out.println(" is a Long: " + ((Long)items[ii]).longValue());
               else if (items[ii] instanceof java.lang.Double)
                  System.out.println(" is a Double: " + ((Double)items[ii]).doubleValue());
               else {
                  System.out.println(" is of unexpected type " + items[ii].getClass().getName());
               }
            }
         }

         waitForInput("\nPress return when ready to continue...");

         /* Any item appended to a ValueList instance as either String or byte[] is
          * returned by getAll() as an instance of String (because in the internal
          * persistent storage, the two types are indistinguishable).  You can
          * choose to have strings returned as instances of byte[] instead, by
          * calling getAll(boolean), and passing true as the parameter value (if you
          * pass false, it will behave identically to getAll() with no parameter).
          * For example:
          */
         System.out.println("\nGet items of unknown type from a list, returning strings as byte[] rather than String:");
         items = myList1.getAll(true);
         for (int ii = 0; ii < items.length; ii++) {
            System.out.print("Item " + (ii + 1));
            if (items[ii] == null) {
               System.out.println(" is null");
            }
            else {
               if (items[ii] instanceof java.lang.String)
                  System.out.println(" is a String: " + (String)items[ii]);
               else if (items[ii] instanceof java.lang.Integer)
                  System.out.println(" is an Integer: " + ((Integer)items[ii]).intValue());
               else if (items[ii] instanceof java.lang.Long)
                  System.out.println(" is a Long: " + ((Long)items[ii]).longValue());
               else if (items[ii] instanceof java.lang.Double)
                  System.out.println(" is a Double: " + ((Double)items[ii]).doubleValue());
               else if (items[ii].getClass().getName().equals("[B")) {
                  System.out.print(" is byte[]: ");
                  for (int jj = 0; jj < ((byte[])(items[ii])).length; jj++)
                     System.out.print(((byte[])(items[ii]))[jj] + " ");
                  System.out.println("");
               }
               else {
                  System.out.println(" is of unexpected type " + items[ii].getClass().getName());
               }
            }
         }

         /* A GlobalsException is thrown if you attempt to get the next element beyond the
          * end of a ValueList instance.
          */
         System.out.println("\nAttempting to get next element when already at end of list should throw an exception:");
         try {
            myList1.getNextObject();
         } catch (GlobalsException e) {
            System.out.println("Caught expected GlobalsException: " + e.getMessage());
         }

         /* You can reset the read position to the beginning of a ValueList instance
          * by calling resetToFirst(), and you can skip past items you don't need
          * by calling skipNext(int count).  For example, here we reset to the start
          * of the first list, and skip past two items after each item we get.
          * The example shows that you can catch GlobalsException as a way of detecting
          * end of list, but you can also use ValueList.length() to limit your loop.
          */
         System.out.println("\nReset to getting items from start of list, and skip some items,");
         System.out.println("repeating until we run past end of list.");
         myList1.resetToFirst();
         try {
            for (int ii = 0; ; ii += 3) {
               Object obj = myList1.getNextObject();
               System.out.print("Item " + (ii + 1));
               if (obj == null) {
                  System.out.println(" is null");
               }
               else {
                  if (obj instanceof java.lang.String)
                     System.out.println(" is a String: " + (String)obj);
                  else if (obj instanceof java.lang.Integer)
                     System.out.println(" is an Integer: " + ((Integer)obj).intValue());
                  else if (obj instanceof java.lang.Long)
                     System.out.println(" is a Long: " + ((Long)obj).longValue());
                  else if (obj instanceof java.lang.Double)
                     System.out.println(" is a Double: " + ((Double)obj).doubleValue());
                  else {
                     System.out.println(" is of unexpected type " + obj.getClass().getName());
                  }
               }
               myList1.skipNext(2);
            }
         } catch (GlobalsException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
         }

         /* You can create nested lists, to arbitrary depth, by appending a ValueList
          * instance as an item in a ValueList.
          */
         myList1.clear();
         myList2.clear();
         myList2.append("Inner Item 1", "Inner Item 2");
         myList1.append("Outer Item 1", myList2, "Outer Item 3");
         myListGlobal.set(myList1, "Nested List");

         System.out.println("\nGet a ValueList containing a nested ValueList:");

         /* When getting a list item as a ValueList, the caller must know that a
          * ValueList was stored in that item, and must explicitly call getNextList.
          * node in question, and must explicitly retrieve it by calling getList.
          * Internally in the Globals database's persistent storage, a ValueList
          * is stored as a string, so ValueList.getNextObject will return an instance
          * of String.  If you call getNextList for a global node that was not stored
          * as a ValueList, a GlobalsException is thrown.
          */
         myList1 = myListGlobal.getList(myList1, "Nested List");
         System.out.println("Outer list item 1: " + myList1.getNextString());
         System.out.println("Outer list item 2:");
         /* Just as with NodeReference.getList, ValueList.getNextList allows you
          * to optionally pass an existing ValueList instance, which is reused to
          * hold the contents of the nested list.
          */
         myList2 = myList1.getNextList(myList2);
         System.out.println("   Inner list item 1: " + myList2.getNextString());
         System.out.println("   Inner list item 2: " + myList2.getNextString());
         System.out.println("Outer list item 3: " + myList1.getNextString());

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Iterating Over All Subnodes of a Global Node *******\n");

         /* You can iterate over all of the subnodes of a global node, at any level.
          * We will store some data in first level subnodes of the global
          * "myArray", and then iterate over the nodes.
          *
          * Store the String "Value of myArray["one"]" in myArray["one"].
          */
         myArray.set("Value of myArray[\"one\"]", "one");
         /* Store the String "Value of myArray[2]" in myArray[2].
          */
         myArray.set("Value of myArray[2]", 2);
         /* We'll cause myArray[30] to have a subnode, but to contain no data,
          * by storing data in myArray[30, 1].
          */
         myArray.set("Value of myArray[30,1]", 30, 1);

         /* To iterate over the subnodes of a given node, specify enough subscripts
          * to identify that node, plus one additional subscript for the level at
          * which you are iterating.  Start iterating at the beginning or end
          * of the sequence of subnodes by specifying subscript value "" (empty string).
          * Method nextSubscript returns (as a String) the subscript of the next
          * subnode in sequence.  Use the returned value as the final subscript, to
          * continue the iteration.  When there are no more subnodes in the sequence,
          * nextSubscript returns "" (empty string).
          *
          * For example, this loop iterates through the first-level subscripts of
          * myArray in ascending order:
          */
         System.out.println("First-level subscripts of myArray in ascending order:");
         String subscript = "";
         do {
            subscript = myArray.nextSubscript(subscript);
            if (subscript.length() > 0)
               System.out.println("  " + subscript);
         } while (subscript.length() > 0);

         /* This loop iterates through the first-level subscripts of myArray in
          * descending order.
          */
         System.out.println("First-level subscripts of myArray in descending order:");
         subscript = "";
         do {
               subscript = myArray.previousSubscript(subscript);
               if (subscript.length() > 0)
                  System.out.println("  " + subscript);
         } while (subscript.length() > 0);

         /* The methods nextSubscriptAndValue and prevousSubscriptAndValue permit
          * you to iterate over subnodes in ascending or descending order, and get
          * back the data values of the nodes, as well as the subscripts.
          *
          * This loop iterates throught the first-level subnodes of myArray in ascending
          * order, getting the value of each node as well as the subscript.
          */
         System.out.println(
            "First-level subscripts and values of myArray in ascending order:");
         subscript = "";
         Object nodeValue = null;
         do {
            subscript = myArray.nextSubscript(subscript);
            if (subscript.length() > 0) {
               nodeValue = myArray.getObject(subscript);
               System.out.print("  myArray[" + subscript + "] value: ");
               if (nodeValue == null)
                  System.out.println("<UNDEFINED>");
               else
                  System.out.println(nodeValue);
            }
         } while (subscript.length() > 0);
         /* This loop iterates throught the first-level subnodes of myArray in descending
          * order, getting the value of each node as well as the subscript.
          */
         System.out.println(
            "First-level subscripts and values of myArray in descending order:");
         subscript = "";
         do {
            subscript = myArray.previousSubscript(subscript);
            if (subscript.length() > 0) {
               nodeValue = myArray.getObject(subscript);
               System.out.print("  myArray[" + subscript + "] value: ");
               if (nodeValue == null)
                  System.out.println("<UNDEFINED>");
               else
                  System.out.println(nodeValue);
            }
         } while (subscript.length() > 0);

         try {
            /* At least one level of subscripts must be specified when iterating,
             * or an exception is thrown.
             */
            myArray.setSubscriptCount(0);
            subscript = myArray.nextSubscript();
            System.out.println("Results of iterating without specifying any subscripts: "
                               + subscript);
         } catch (GlobalsException e) {
            System.out.println("Caught expected GlobalsException: " + e.getMessage());
         }

         /* NOTE: You may have noticed that nextSubscript, previousSubscript,
          * nextSubscriptAndValue, and prevoiusSubscriptAndValue
          * always returns subscripts as String, unlike the get<type>Subscript family
          * of methods discussed earlier.  This is because int the persistent
          * storage of a Globals database, all subscripts are normalized as strings.
          * The get<type>Subscript methods are getting the subscript value from the
          * NodeReference instance in memory, where the value is of whatever type was
          * passed to set it.)
          */

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Deleting Data and Subnodes *************************\n");

         /* You can delete a node and all of its subnodes by calling kill().
          * You can delete just the data in a node, without deleting its subnodes,
          * by calling killNode().
          *
          * We'll set up an example with three levels of subnodes, each with a
          * data value.
          */
         myArray.setSubscriptCount(0);
         myArray.set("one", 1);
         myArray.set("two", 1, 2);
         myArray.set("three", 1, 2, 3);

         System.out.println("Before myArray.killNode(1), myArray.exists(1) returns " +
                            myArray.exists(1) + ",\n myArray.hasSubnodes(1) returns " +
                            myArray.hasSubnodes(1) + ".");
         /* Delete just the data in myArray[1].
          */
         myArray.killNode(1);
         System.out.println("After myArray.killNode(1), myArray.exists(1) returns " +
                            myArray.exists(1) + ",\n myArray.hasSubnodes(1) returns " +
                            myArray.hasSubnodes(1) + ".");

         System.out.println("\nBefore myArray.kill(1, 2), myArray.exists(1, 2) returns " +
                            myArray.exists(1, 2) + ",\n myArray.hasSubnodes(1, 2) returns " +
                            myArray.hasSubnodes(1, 2) + ".");
         /* Delete myArray[1,2], and all its subnodes.
          */
         myArray.kill(1,2);
         System.out.println("After myArray.kill(1, 2), myArray.exists(1, 2) returns " +
                            myArray.exists(1, 2) + ",\n myArray.hasSubnodes(1, 2) returns " +
                            myArray.hasSubnodes(1, 2) + ".");

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Atomically Incrementing a Global Node's Value *****\n");

         /* You can atomically increment the value of a global node by a specified
          * integral amount, by using the method long increment(int amount).  It is
          * guaranteed that no other process can set or get the node during the
          * increment operation (unlike what might happen if you get the node's value,
          * increment the retrieved value, and then set the node to the incremented
          * value; in that case, another process might have set the node's value in
          * between your get and set calls).
          *
          * Here are simple examples of incrementing a node by 1 and then by 10:
          */
         System.out.println("Value of the global \"myInt\" before incrementing: " +
                            myInt1.getInt());
         long incrementedValue = myInt1.increment(1);
         System.out.println("Value of the global \"myInt1\" after incrementing by 1: " +
                            myInt1.getInt());
         System.out.println("Value of the global \"myInt1\" after incrementing by 10: " +
                            myInt1.increment(10));

         /* This can be used to efficiently generate an increasing sequence of
          * unique subscripts, for a global that is being shared by multiple
          * users.  If a node had no previous data value, increment(N) is
          * equivalent to set(N), to you don't have to worry about knowing whether
          * your process is incrementing the global for the first time.
          * For example:
          *
          * Kill any previous data and subnodes in the global "myArray", to guarantee
          * we're starting this example with a global that doesn't exist.
          */
         myArray.setSubscriptCount(0);
         myArray.kill();

         /* Create an array of 5 nodes with increasing subscript values N, and
          * data values "Data value N":
          */
         long uniqueId = 0;
         String data;
         while (uniqueId <= 5) {
            uniqueId = myArray.increment(1);
            data = "Data value " + uniqueId;
            myArray.set(data, uniqueId);
         }
         for (int ii = 1; ii < uniqueId; ii++) {
            System.out.println("Value of myArray[" + ii + "]: " +
                               myArray.getString(ii));
         }

         /* It is not usually useful to use increment on nodes whose value was
          * set asa type other than int or long, but if you do so, the results
          * are predictable, as follows:
          *
          * If you call increment for a node whose value was set as a double,
          * the node's value is incremented by the specified amount, and remains a
          * double, but the value returned by increment is truncated to a long.
          * For example:
          */
         System.out.println("\nValue of global \"myDouble\" before incrementing: " +
            myDouble.getObject());
         long incrementResult = myDouble.increment(1);
         System.out.println("myDouble.increment(1) returned " + incrementResult);
         System.out.println("Value of global \"myDouble\" after incrementing by 1: " +
                            myDouble.getObject());

         /* If you call increment for a node whose value was set as a String,
          * the value is interpreted as a number by parsing it as an integer up to
          * the first character that is not a leading '+' or '-', or a digit or
          * the first decimal point (if there is no such leading portion, it is
          & converted to 0).  This number is incremented by the specified amount,
          * the result becomes the new value of the node.  Any portion of the
          * original string value beyond the leading numeric portion is discarded.
          * If the resulting number has a fractional component, the result returned
          * by increment is truncated.
          *
          * For example, this string is interpreted as an integer and incremented
          * accordingly:
          */
         System.out.println("\nValue of global \"myString\" before incrementing: " +
            myString.getObject());
         incrementResult = myString.increment(1);
         System.out.println("myString.increment(1) returned " + incrementResult);
         System.out.println("Value of global \"myString\" after incrementing by 1: " +
                            myString.getObject());

         /* This string is interpreted as a number with a fractional component and
          * incremented accordingly:
          */
         myString.set("3.14 is the approximate value of Pi.");
         System.out.println("\nNew value of global \"myString\" before incrementing: " +
            myString.getObject());
         incrementResult = myString.increment(1);
         System.out.println("myString.increment(1) returned " + incrementResult);
         System.out.println("Value of global \"myString\" after incrementing by 1: " +
                            myString.getObject());

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Finding All Globals in a Globals Database **********\n");

         /* You can get and iterate over a directory of all the globals in a Globals
          * database using GlobalsDirectory.
          *
          * Create a GlobalsDirectory instance:
          */
         GlobalsDirectory myDirectory = connection.createGlobalsDirectory();

         /* Use nextGlobalName() to iterate over the directory in ascending
          * collating order, getting and displaying the name of each global.
          */
         System.out.println("All globals in ascending order:");
         String globalName;
         do {
            globalName = myDirectory.nextGlobalName();
            if (globalName.length() > 0)
               System.out.println(globalName);
         } while (globalName.length() > 0);

         /* Use previousGlobalName() to iterate over the directory in descending
          * collating order, getting and displaying the name of each global,
          * and the data value of its root node, if it has one.
          */
         System.out.println("\nAll globals in descending order:");
         globalName = myDirectory.previousGlobalName("");
         while (globalName.length() > 0) {
            System.out.println(globalName);
            globalName = myDirectory.previousGlobalName();
         }

         /* Searching for a global using a prefix
          */
         System.out.println("\nGlobals in ascending order, starting from \"myBrandNewGlo\":");
         /* Create a global to search for in this example.
          */
         NodeReference myBrandNewGlobal = connection.createNodeReference("myBrandNewGlobal");
         myBrandNewGlobal.set("Hello");
         /* Call refresh() to cause a GlobalsDirectory instance to show any
          * globals names that have been added, and not show names of any
          * globals that have been killed, since the instance created or last
          * refreshed,
          */
         myDirectory.refresh();
         /* Calling nextGlobalName(String name) or previousGlobalName(String name)
          * causes returns the new (or previous) global name in collating order after
          * the specified name.  In this example, the next name after "myBrandNewGlo"
          * is "myBrandNewGlobal", so that name is returned.
          */
         globalName = myDirectory.nextGlobalName("myBrandNewGlo");
         while (globalName.length() > 0) {
            /* The returned name can be used to set the name of a NodeReference
             * instance, in order to access the global's data.
             */
            myBrandNewGlobal.setName(globalName);
            Object value = myBrandNewGlobal.getObject();
            System.out.print("Global " + globalName);
            if (value == null)
               System.out.println(" is undefined.");
            else
               System.out.println(" has value " + value + ".");
            globalName = myDirectory.nextGlobalName();
         }
         /* Kill myBrandNewGlobal, so it will be brand new the next time you run
          * this application.
          */
         myBrandNewGlobal.setName("myBrandNewGlobal");
         myBrandNewGlobal.kill();

         waitForInput("\nPress return when ready to continue...");

         System.out.println("\n********************************************************");
         System.out.println("*** Transactions ***************************************\n");

         /* The connection class provides methods to start, commit, and roll back
          * transactions, enabling you to ensure that a series of operations which save
          * persistent data either all succeed or are all rolled back.
          * Nested transactions are supported, which can be rolled back individually
          * without rolling back the surrounding transaction, or can be committed,
          * merging them with the surrounding transaction.  No change to persistent
          * data is permanently committed until the outermost transaction is committed.
          *
          * In this example, we start two levels of nested transaction, storing a
          * value in a global in each level.  We then roll back the inner level,
          * commit the outer level, and verify that the global set in the inner
          * transaction is now undefined, but the global set in the outer transaction
          * still has its value.
          */
         connection.startTransaction();
         System.out.println("After first startTransaction(), transactinLevel() is "
                            + connection.transactionLevel());
         NodeReference setInOuter = connection.createNodeReference("setInOuter");
         setInOuter.set(1);
         connection.startTransaction();
         System.out.println("After second startTransaction(), transactionLevel() is "
                            + connection.transactionLevel());
         NodeReference setInInner = connection.createNodeReference("setInInner");
         setInInner.set(2);
         /* Roll back 1 level of transaction.
          */
         connection.rollback(1);
         System.out.println("After rollback(1), transactionLevel() is "
                            + connection.transactionLevel());
         try {
            System.out.println("After rollback(1), value of the global \"setInInner\":");
            System.out.println(setInInner.getInt());
         } catch (UndefinedException e) {
            System.out.println("Caught expected UndefinedException: " + e.getMessage());
         }
         connection.commit();
         System.out.println("After commit(), transactionLevel() is "
                            + connection.transactionLevel());
         System.out.println("After commit(), value of the global \"setInOuter\": " +
                               setInOuter.getInt());

         /* In a second example, we start three levels of nested transaction.  We then
          * verify that commit() commits just one level, rollback with arguments <= 0
          * does nothing, and rollback() with no argument commits all the remaining levels.
          */
          connection.startTransaction();
          connection.startTransaction();
          connection.startTransaction();
          System.out.println("\ntransactionLevel() after calling startTransaction() 3 times: "
                             + connection.transactionLevel());
          connection.commit();
          System.out.println("transactionLevel() after calling commit(): " +
                             connection.transactionLevel());
          connection.rollback(-100);
          connection.rollback(0);
          System.out.println(
             "transactionLevel() after calling tRollback() with arguments <= 0: " +
             connection.transactionLevel());
          connection.rollback();
          System.out.println("transactionLevel() after calling tRollback() no argument: " +
                             connection.transactionLevel());

          waitForInput("\nPress return when ready to continue...");

          System.out.println("\n********************************************************");
          System.out.println("*** Locking ********************************************\n");

          /* Connection provides methods for acquiring and releasing shared and
           * exclusive locks on node references.  Locks are advisory; that is, they don't
           * prevent another process from doing anything except acquire a lock on the
           * same node reference, so it is up to application developers to agree to
           * acquire appropriate locks before modifying globals, in order to ensure
           * that they can run concurrently updating the same globals, without
           * stepping on each other's updates.
           *
           * Any number of processes can acquire a shared lock on the same node
           * reference, but only one process at a time can acquire an exclusive lock on
           * a given node reference.  An exclusive lock cannot be acquired if any
           * process holds a shared lock on the same node reference.  Best practice
           * is to use shared locks as read locks, and exclusive locks as write locks.
           *
           * The globals API provides basic locking operations.  It is the responsibility
           * of the application developer to use these in a way that achieves
           * the concurrency control and transaction consistency goals of the
           * application.
           *
           * Make sure to release any locks acquired by your application, either
           * individually, or by calling Connection.releaseAllLocks(), or
           * by calling Connection.close() when finished using the connection.
           *
           * You can experiment with the locking examples below by running two instances
           * of this Sample application with the -pause command line argument, and see
           * what happens when you let one or the other instance acquire a given lock
           * first.
           */
          try {
             NodeReference lockedRef1 = connection.createNodeReference("lockedRef1");
             NodeReference lockedRef2 = connection.createNodeReference("lockedRef2");
             lockedRef1.appendSubscript(1);

             /* We first demonstrate incremental locking within a transaction.  Locking
              * incrementally means acquiring a new lock, while continuing to hold any
              * locks already held by this process.  Locking non-incrementally means
              * that when a new lock is acquired, any previously held locks are released.
              */
             connection.startTransaction();
             /* Acquire an exclusive lock on lockedRef1[1], locking incrementally.
              */
             lockedRef1.acquireLock(NodeReference.EXCLUSIVE_LOCK,
                                    NodeReference.LOCK_INCREMENTALLY);
             /* Acquire a shared lock on lockedRef2, locking incrementally.
              */
             lockedRef2.acquireLock(NodeReference.SHARED_LOCK, NodeReference.LOCK_INCREMENTALLY);
             System.out.println(
                "Holding exclusive lock on lockedRef1[1] and shared lock on lockedRef2");
             System.out.println("Will now release locks individually (one immediate, the other not)...");
             waitForInput("\nPress return to release locks individually and continue...");
             /* Release the exclusive lock on lockedRef1[1], at the end of the current
              * transaction.  This means the lock isn't actually released until you
              * commit or roll back the transaction.  This mode of release lets you
              * ensure that all resources that need to be locked to ensure a consistent
              * transaction remain locked until the end of the transaction, and are then
              * released.
              */
             lockedRef1.releaseLock(NodeReference.EXCLUSIVE_LOCK, NodeReference.RELEASE_AT_TRANSACTION_END);
             /* Release the shared lock on lockedRef2 immediately, without waiting until
              * the end of the transaction.  This mode of release is appropriate if
              * the locked resource doesn't need to remain locked until the end of the
              * transaction to ensure consistent transaction results.  The choice
              * is an application design decision.  Refer to a good text book on
              * locking and transactions for general principles and best practice.
              */
             lockedRef2.releaseLock(NodeReference.SHARED_LOCK, NodeReference.RELEASE_IMMEDIATELY);
             System.out.println("Will now commit transaction...");
             waitForInput("\nPress return again to commit transaction and continue...");
             connection.commit();

             /* We now demonstrate non-incremental locking, in which all previously-
              * held locks are automatically released when a new lock is acquired.
              *
              * Acquire a lock on lockedRef1[1], non-incrementally.
              */
             lockedRef1.acquireLock(NodeReference.EXCLUSIVE_LOCK,
                                    NodeReference.LOCK_NON_INCREMENTALLY);
             System.out.println("Holding exclusive lock on lockedRef1[1]");
             System.out.println("Will now acquire shared lock on lockedRef2...");
             waitForInput("\nPress return to acquire lock on lockedRef2 and continue.");
             /* Acquire a shared lock on lockedRef2, non-incrementally.
              */
             lockedRef2.acquireLock(NodeReference.SHARED_LOCK,
                                    NodeReference.LOCK_NON_INCREMENTALLY);
             System.out.println("Exclusive lock on lockedRef1[1] has now been released,");
             System.out.println("shared lock is now held on lockedRef1lockedRef2.");
             waitForInput("\nPress return to continue...");

             /* We now demonstrate releasing all incremental locks:
              *
              * Acquire a shared lock on lockedRef1[1], incrementally.
              */
             lockedRef1.acquireLock(NodeReference.SHARED_LOCK,
                                    NodeReference.LOCK_INCREMENTALLY);
             /* Acquire an exclusive lock on lockedRef2, incrementally.
              */
             lockedRef2.acquireLock(NodeReference.EXCLUSIVE_LOCK,
                                    NodeReference.LOCK_INCREMENTALLY);
             System.out.println("Two locks are currently held (one with lock count 2).");
             waitForInput("\nPress reutnr to continue...");
             connection.releaseAllLocks();
             System.out.println("All locks have now been released.");
             waitForInput("\nPress reutnr to continue...");
          }
          catch (LockException e) {
             /* Catching LockException means that a call to acquireLock timed out,
              * due to another process holding the lock.  You can experiment
              * with this by running two instances of this Sample application
              * with the -pause command line argument, and see what happens when
              * you let one or the other instance acquire a given lock first.
              */
             System.out.println("Caught LockException: " + e.getMessage());
          }
      }
      /* It is best practice to be prepared to catch GlobalsException any time
       * you call methods of the globals API.  Since GlobalsException extends
       * RuntimeException (and is thus not a checked exception), you are not
       * required to catch it in each method in which you call globals API methods,
       * nor declare your methods to throw it, but you are advised to catch it at
       * whatever level works best for your application design.
       */
      catch (GlobalsException e) {
         System.out.println("Caught GlobalsException: " + e.getMessage());
      }
      finally {
         /* Always design your applications to ensure that if you have an open
          * Connection, you call its close() method before terminating.
          * This ensures that any locks are released, and frees resources in the
          * underlying implementation.
          *
          * Note: After calling close() on an instance of Connection,
          * you can call connect to re-connect this same instance, if you need
          * to use it again.  This differs from other classes of the Globals
          * API, instances of which cannot be used once their close() method has
          * called.
          */
         System.out.println("Closing connection.");
         try {
            connection.close();
         } catch (GlobalsException e) {
            System.out.println("Caught GlobalsException: " + e.getMessage());
         }
      }
   }
   private static void waitForInput(String s) {
      if (pause) {
         System.out.println(s);
         try {
            byte[] data = new byte[30];
            System.in.read(data);
         } catch (java.io.IOException e) {
         }
      }
   }
}
