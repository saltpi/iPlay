package top.ourfor.app.iplay.api.webdav;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebDAVResponseParser {
    public static List<WebDAVResource> parseWebDAVResponse(String xmlResponse) {
        List<WebDAVResource> resources = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList responseList = document.getElementsByTagName("D:response");
            if (responseList.getLength() == 0) {
                responseList = document.getElementsByTagName("d:response");
            }
            for (int i = 0; i < responseList.getLength(); i++) {
                Node response = responseList.item(i);
                if (response.getNodeType() == Node.ELEMENT_NODE) {
                    Element responseElement = (Element) response;

                    var hrefEle = responseElement.getElementsByTagName("D:href");
                    if (hrefEle.getLength() == 0) {
                        hrefEle = responseElement.getElementsByTagName("d:href");
                    }
                    String href = hrefEle.item(0).getTextContent();
                    var displayEle = responseElement.getElementsByTagName("D:displayname");
                    if (displayEle.getLength() == 0) {
                        displayEle = responseElement.getElementsByTagName("d:displayname");
                    }
                    String displayName = displayEle.item(0).getTextContent();
                    var modifyEle = responseElement.getElementsByTagName("D:getlastmodified");
                    if (modifyEle.getLength() == 0) {
                        modifyEle = responseElement.getElementsByTagName("d:getlastmodified");
                    }
                    NodeList lastModifiedList = modifyEle;
                    String lastModified = lastModifiedList.getLength() > 0 ? lastModifiedList.item(0).getTextContent() : null;
                    var collectionEle = responseElement.getElementsByTagName("D:collection");
                    if (collectionEle.getLength() == 0) {
                        collectionEle = responseElement.getElementsByTagName("d:collection");
                    }
                    boolean isCollection = collectionEle.getLength() > 0;
                    resources.add(new WebDAVResource(href, displayName, lastModified, isCollection));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return resources;
    }
}