import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class XMLParser {
    private Map<Integer, NodeInfo> nodes = new HashMap<>();
    private Map<Integer, List<Arc>> graph = new HashMap<>();

    public void parseXML(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);


        // Parsarea nodurilor
        NodeList nodeList = doc.getElementsByTagName("node");
        IntStream.range(0, nodeList.getLength()).parallel().forEach(i -> {
            Element element = (Element) nodeList.item(i);
            int id = Integer.parseInt(element.getAttribute("id"));
            int lon = Integer.parseInt(element.getAttribute("longitude"));
            int lat = Integer.parseInt(element.getAttribute("latitude"));
            synchronized (nodes) {
                nodes.put(id, new NodeInfo(id, lon, lat));
            }
        });

        // Parsarea arcelor
        NodeList arcList = doc.getElementsByTagName("arc");
        for (int i = 0; i < arcList.getLength(); i++) {
            Element element = (Element) arcList.item(i);
            int from = Integer.parseInt(element.getAttribute("from"));
            int to = Integer.parseInt(element.getAttribute("to"));
            int length= Integer.parseInt(element.getAttribute("length"));
            // Adăugare arce în graf
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(new Arc(from, to, length));

        }
    }

    public Map<Integer, NodeInfo> getNodes() {
        return nodes;
    }

    public Map<Integer, List<Arc>> getGraph() {
        return graph;
    }
}