package pt.ua.sd.ropegame.common;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A XML parser to read data from configuration files.
 */
public class DOMParser {

    private Document doc;

    /**
     * Constructor for a XML Parser.
     * @param file XML File location.
     * @throws IOException File not found.
     * @throws SAXException Error while parsing XML File.
     */
    public DOMParser(String file) throws IOException, SAXException {
        File inputFile = new File(file);
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

    }

    /**
     * @return RMI registry host
     */
    public String getRMIHost() {
        NodeList nList = doc.getElementsByTagName("rmiRegistryHost");
        Node node = nList.item(0);

        return node.getTextContent();
    }

    /**
     * @return RMI registry port
     */
    public int getRMIPort() {
        NodeList nList = doc.getElementsByTagName("rmiRegistryPort");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return Max trials.
     */
    public int getMaxTrials() {
        NodeList nList = doc.getElementsByTagName("max_trials");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return Max games.
     */
    public int getMaxGames() {
        NodeList nList = doc.getElementsByTagName("max_games");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return number of teams.
     */
    public int getNTeams() {
        NodeList nList = doc.getElementsByTagName("nteams");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return number of coaches.
     */
    public int getNCoaches() {
        NodeList nList = doc.getElementsByTagName("ncoaches");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return number of contestants.
     */
    public int getNContestants() {
        NodeList nList = doc.getElementsByTagName("ncontestants");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return Max contestants in playground.
     */
    public int getMaxContsPlayground() {
        NodeList nList = doc.getElementsByTagName("max_contestants_playground");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return log file name.
     */
    public String getLogFileName() {
        NodeList nList = doc.getElementsByTagName("log");
        Node node = nList.item(0);

        return node.getTextContent();
    }


    /**
     * @return General Repository's hostname.
     */
    public String getGenRepHostname() {
        NodeList nList = doc.getElementsByTagName("genRepHostname");
        Node node = nList.item(0);

        return node.getTextContent();
    }

    /**
     * @return General Repository's port.
     */
    public int getGenRepPort() {
        NodeList nList = doc.getElementsByTagName("genRepPort");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }


    /**
     * @return Bench's hostname.
     */
    public String getBenchHostname() {
        NodeList nList = doc.getElementsByTagName("benchHostname");
        Node node = nList.item(0);

        return node.getTextContent();
    }

    /**
     * @return Bench's port.
     */
    public int getBenchPort() {
        NodeList nList = doc.getElementsByTagName("benchPort");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return Playground's hostname
     */
    public String getPlaygroundHostname() {
        NodeList nList = doc.getElementsByTagName("playgroundHostname");
        Node node = nList.item(0);

        return node.getTextContent();
    }

    /**
     * @return Playground's port.
     */
    public int getPlaygroundPort() {
        NodeList nList = doc.getElementsByTagName("playgroundPort");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }

    /**
     * @return Referee Site's Hostname.
     */
    public String getRefSiteHostName() {
        NodeList nList = doc.getElementsByTagName("refSiteHostname");
        Node node = nList.item(0);

        return node.getTextContent();
    }

    /**
     * @return Referee Site's port.
     */
    public int getRefSitePort() {
        NodeList nList = doc.getElementsByTagName("refSitePort");
        Node node = nList.item(0);

        return Integer.parseInt(node.getTextContent());
    }


}