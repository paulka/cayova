package com.paul.scratch;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class RSSFeedWriter {

    private String outputFile;
    private Feed rssfeed;

    public RSSFeedWriter(Feed rssfeed, String outputFile) {
        this.rssfeed = rssfeed;
        this.outputFile = outputFile;
    }


    public void write() throws Exception {

        // Create a XMLOutputFactory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        // Create XMLEventWriter
        XMLEventWriter eventWriter = outputFactory
                .createXMLEventWriter(new FileOutputStream(outputFile));

        // Create a EventFactory

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");

        // Create and write Start Tag

        StartDocument startDocument = eventFactory.createStartDocument();

        eventWriter.add(startDocument);

        // Create open tag
        eventWriter.add(end);

        StartElement rssStart = eventFactory.createStartElement("", "", "rss");

        eventWriter.add(rssStart);
        eventWriter.add(eventFactory.createAttribute("version", "2.0"));
        eventWriter.add(eventFactory.createNamespace("content", "http://purl.org/rss/1.0/modules/content/"));
        eventWriter.add(eventFactory.createNamespace("dc", "http://purl.org/dc/elements/1.1/"));
        eventWriter.add(end);

        eventWriter.add(eventFactory.createStartElement("", "", "channel"));
        eventWriter.add(end);

        // Write the different nodes

        createNode(eventWriter, "title", rssfeed.getTitle());

        createNode(eventWriter, "link", rssfeed.getLink());

        createNode(eventWriter, "description", rssfeed.getDescription());

        createNode(eventWriter, "language", rssfeed.getLanguage());

        createNode(eventWriter, "copyright", rssfeed.getCopyright());

        createNode(eventWriter, "pubDate", rssfeed.getPubDate());

        eventWriter.add(eventFactory.createStartElement("", "", "image"));
        eventWriter.add(end);
        createNode(eventWriter, "url", rssfeed.getImage());
        createNode(eventWriter, "title", rssfeed.getTitle());
        createNode(eventWriter, "link", rssfeed.getLink());
        eventWriter.add(eventFactory.createEndElement("", "", "image"));
        eventWriter.add(end);


        for (FeedMessage entry : rssfeed.getMessages()) {
            eventWriter.add(eventFactory.createStartElement("", "", "item"));
            eventWriter.add(end);
            createNode(eventWriter, "title", entry.getTitle());
            createNode(eventWriter, "description", entry.getDescription());
            createNode(eventWriter, "content:encoded", "<p>" + entry.getDescription() + "</p>", true);
            createNode(eventWriter, "link", entry.getLink());
            createNode(eventWriter, "dc:creator", entry.getAuthor());
            //createNode(eventWriter, "author", entry.getAuthor());
            createNode(eventWriter, "guid", entry.getGuid());
            createNode(eventWriter, "pubDate", entry.getModified());
            for (Enclosure enc : entry.getEnclosures()) {
                //TODO: WTF? There has to be a better XML model in java than this
                Attribute length = eventFactory.createAttribute("length", "0");
                Attribute type = eventFactory.createAttribute("type", enc.getType());
                Attribute url = eventFactory.createAttribute("url", enc.getUrl());
                List attributeList = Arrays.asList(length, type, url);
                List nsList = Arrays.asList();
                StartElement sElement = eventFactory.createStartElement("", "", "enclosure", attributeList.iterator(), nsList.iterator());
                eventWriter.add(sElement);
                // Create End node
                EndElement eElement = eventFactory.createEndElement("", "", "enclosure");
                eventWriter.add(eElement);
                eventWriter.add(end);


            }
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndElement("", "", "item"));
            eventWriter.add(end);

        }

        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement("", "", "channel"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement("", "", "rss"));

        eventWriter.add(end);

        eventWriter.add(eventFactory.createEndDocument());

        eventWriter.close();
    }

    private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        createNode(eventWriter, name, value, false);
    }

    private void createNode(XMLEventWriter eventWriter, String name, String value, Boolean CDATA) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // Create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // Create Content
        Characters characters;
        characters = CDATA ? eventFactory.createCData(value) : eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // Create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
}
