package utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;

/**
 * Created on 03.09.2015.
 */
public class XMLReader {
    private static final String pathToParams = "params.xml";
    private static final String port = "port";
    private static final String message = "messagelimit";
    private static final String thread = "threadcount";

    private static final String pathToUsers = "users.xml";
    private static final String user = "user";
    private static final String name = "name";
    private static final String password = "password";

    private static Element getRootElement(String path){
        Element root = null;

        try{
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            root = doc.getDocumentElement();

        } catch (Exception e){
            e.printStackTrace();
        }

        return root;
    }

    private static String getTextValue(Element doc, String tag){
        String value = null;
        NodeList nl = doc.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }

        return value;
    }

    public static Config readParams(){
        Config config = null;
        try{
            Element root = getRootElement(pathToParams);

            String portValue = getTextValue(root, port);
            String messagesLimitValue = getTextValue(root, message);
            String threadCountValue = getTextValue(root, thread);

            config = new Config(portValue, messagesLimitValue, threadCountValue);

        } catch (Exception e){
            e.printStackTrace();
        }

        return config;
    }

    public static HashMap<String,String> readUsers(){
        HashMap<String,String> usersMap = new HashMap<String,String>();

        try{
            Element root = getRootElement(pathToUsers);

            NodeList nList = root.getElementsByTagName(user);
            int nListLength = nList.getLength();

            for( int i = 0; i <nListLength; i++ ){
                Element nElement = (Element) nList.item(i);

                String nameValue = getTextValue(nElement, name);
                String passwordValue = getTextValue(nElement, password);

                usersMap.put(nameValue, passwordValue);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return usersMap;
    }
}
