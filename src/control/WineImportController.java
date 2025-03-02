package control;

import control.WineProducerDAO;
import model.Wine;
import model.WineProducer;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;	
import java.util.List;

public class WineImportController {

    private final WineProducerDAO wineProducerDAO = new WineProducerDAO();

    public void importFromXml(String filePath) {
        try {
            // Initialize XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            // Parse WineProducers
            NodeList producerNodes = doc.getElementsByTagName("Producer");
            for (int i = 0; i < producerNodes.getLength(); i++) {
                Element producerElement = (Element) producerNodes.item(i);

                WineProducer producer = new WineProducer();
                producer.setManufacturerId(producerElement.getAttribute("id"));
                producer.setFullName(getElementText(producerElement, "Name"));
                producer.setContactPhone(getElementText(producerElement, "ContactPhone"));
                producer.setAddress(getElementText(producerElement, "Address"));
                producer.setEmail(getElementText(producerElement, "Email"));

                // Parse Wines for this producer
                List<Wine> wines = new ArrayList<>();
                NodeList wineNodes = producerElement.getElementsByTagName("Wine");
                for (int j = 0; j < wineNodes.getLength(); j++) {
                    Element wineElement = (Element) wineNodes.item(j);

                    Wine wine = new Wine();
                    wine.setWineId(wineElement.getAttribute("id"));
                    wine.setName(getElementText(wineElement, "Name"));
                    wine.setYear(Integer.parseInt(getElementText(wineElement, "Year")));

                    wines.add(wine);
                }
                producer.setWines(wines);

                // Insert producer and wines into the database
                wineProducerDAO.insertWineProducer(producer);
            }

            System.out.println("XML data imported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to get text content of an element
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }


    public void exportToXml(List<WineProducer> producers, String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("WineProducers");
            doc.appendChild(rootElement);

            for (WineProducer producer : producers) {
                Element producerElement = doc.createElement("Producer");
                producerElement.setAttribute("id", producer.getManufacturerId());

                Element nameElement = doc.createElement("Name");
                nameElement.appendChild(doc.createTextNode(producer.getFullName()));
                producerElement.appendChild(nameElement);

                Element phoneElement = doc.createElement("ContactPhone");
                phoneElement.appendChild(doc.createTextNode(producer.getContactPhone()));
                producerElement.appendChild(phoneElement);

                Element addressElement = doc.createElement("Address");
                addressElement.appendChild(doc.createTextNode(producer.getAddress()));
                producerElement.appendChild(addressElement);

                Element emailElement = doc.createElement("Email");
                emailElement.appendChild(doc.createTextNode(producer.getEmail()));
                producerElement.appendChild(emailElement);

                Element winesElement = doc.createElement("Wines");
                for (Wine wine : producer.getWines()) {
                    Element wineElement = doc.createElement("Wine");
                    wineElement.setAttribute("id", wine.getWineId());

                    Element wineNameElement = doc.createElement("Name");
                    wineNameElement.appendChild(doc.createTextNode(wine.getName()));
                    wineElement.appendChild(wineNameElement);

                    Element yearElement = doc.createElement("Year");
                    yearElement.appendChild(doc.createTextNode(String.valueOf(wine.getYear())));
                    wineElement.appendChild(yearElement);

                    winesElement.appendChild(wineElement);
                }
                producerElement.appendChild(winesElement);
                rootElement.appendChild(producerElement);
            }
            File outputFile = new File(filePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // Create the directory if it doesn't exist
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);
            System.out.println("XML file created successfully: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}