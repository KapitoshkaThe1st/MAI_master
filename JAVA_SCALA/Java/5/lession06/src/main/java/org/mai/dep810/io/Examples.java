package org.mai.dep810.io;

import org.apache.log4j.Logger;
import org.mai.dep810.io.rest.Book;

import java.io.*;


public class Examples {

    private static Logger log = Logger.getLogger(Examples.class);

    public static void main(String[] args) throws IOException {

        Book book = new Book("111", "222", new String[] {"3", "33", "333"}, "444", "555");

        boolean succeeded = true;
        try(ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream("proba"))){
            ostream.writeObject(book);
        }
        catch (Exception ex){
            log.error("Error while serializing object", ex);
            succeeded = false;
        }

        Book book1 = new Book("11", "22", new String[] {"3", "33"}, "44", "55");

        try(ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream("proba1"))){
            ostream.writeObject(book1);
        }
        catch (Exception ex){
            log.error("Error while serializing object", ex);
            succeeded = false;
        }

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        String path = "books/1491901632";

        File f = new File("books");
        System.out.println(f.isDirectory());
        File[] files = f.listFiles();

        for (File file : files)
            System.out.println(file.getName());

        Book result = null;
        try(ObjectInputStream istream = new ObjectInputStream(new FileInputStream(path))){
            result = (Book)istream.readObject();
        }
        catch (Exception ex){
            log.error("Error while deserializing object", ex);
        }

        System.out.println(result.getAuthors());

//        try(InputStream in = new FileInputStream("pom.xml")) {
//            int b;
//            while ((b = in.read()) > 0) {
//                System.out.print((char)b);
//            }
//        }

//        try(InputStream is = new FileInputStream("pom.xml")) {
//            byte[] bytes = new byte[512];
//            while(is.read(bytes) >= 0) {
//                System.out.println(new String(bytes));
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        try(FileReader r = new FileReader("pom.xml")) {
//            int b;
//            while ((b = r.read()) >= 0) {
//                System.out.print((char) b);
//            }
//        }


//        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("pom.xml")), 1024*1024)) {
//            String line = null;
//            while((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//        }

//        try (
//                OutputStream os = new FileOutputStream("pom_copy.xml");
//                InputStream is = new FileInputStream("pom.xml");
//        ) {
//                byte[] bytes = new byte[512];
//                int length = 0;
//                while((length = is.read(bytes)) >= 0) {
//                    os.write(bytes, 0, length);
//                }
//        }


//        StringBuffer target = new StringBuffer("");
//        try (Reader r = new FileReader("pom.xml")) {
//            char[] chars = new char[512];
//            int length = 0;
//            while ( (length = r.read(chars)) >= 0) {
//                target.append(chars, 0, length);
//            }
//        }
//        System.out.println(target);
//
//        try (Writer w = new FileWriter("pom_copy.xml")) {
//            w.append(target);
//        }

//        List<String> lines = Files.readAllLines(FileSystems.getDefault().getPath("pom.xml"));
//        lines.stream().forEach(System.out::println);

//        Scanner s = new Scanner(System.in);
//        s.useDelimiter("\\s+");
//        int i = s.nextInt();
//        double d = s.nextDouble();
//        String str = s.nextLine();
//        System.out.println("" + i + " / " + d + " / " + str);

//        URL url = new URL("http://google.com");
//        URLConnection connection = url.openConnection();
//        try (InputStream yandex = url.openStream()) {
//            int b;
//            while ((b = yandex.read()) >= 0) {
//                System.out.print((char) b);
//            }
//        }



        //string to input stream
//        String test = "One of the greatest strength of the Jackson library is the highly customizable serialization and deserialization process.";
//
//        InputStream in1 = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
//
//
//        //read string from input stream using reader
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in1));
//        String redFromStream = reader.readLine();
//        log.info(redFromStream);
    }
}
