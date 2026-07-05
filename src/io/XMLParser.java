package src.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import src.gameObjects.Constants;

/** Alternativa XML mantenida por compatibilidad con el último episodio del repositorio base. */
public final class XMLParser {
    private static final Path SCORE_FILE = Path.of(Constants.SCORE_XML_PATH);

    private XMLParser() { }

    public static ArrayList<ScoreData> readFile() {
        ArrayList<ScoreData> dataList = new ArrayList<>();
        if (!Files.isRegularFile(SCORE_FILE)) return dataList;
        try (FileInputStream input = new FileInputStream(SCORE_FILE.toFile())) {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
            ScoreData current = null;
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    String name = start.getName().getLocalPart();
                    if (Constants.PLAYER.equals(name)) current = new ScoreData();
                    else if ("name".equalsIgnoreCase(name) && current != null && reader.hasNext()) {
                        current.setName(reader.nextEvent().asCharacters().getData());
                    } else if (Constants.DATE.equals(name) && current != null && reader.hasNext()) {
                        current.setDate(reader.nextEvent().asCharacters().getData());
                    } else if (Constants.SCORE.equals(name) && current != null && reader.hasNext()) {
                        current.setScore(Integer.parseInt(reader.nextEvent().asCharacters().getData()));
                    }
                } else if (event.isEndElement()) {
                    EndElement end = event.asEndElement();
                    if (Constants.PLAYER.equals(end.getName().getLocalPart()) && current != null) {
                        dataList.add(current);
                        current = null;
                    }
                }
            }
        } catch (Exception ignored) {
            // El JSON sigue siendo el formato principal; un XML inválido no detiene el juego.
        }
        return dataList;
    }

    public static void writeFile(ArrayList<ScoreData> dataList) throws XMLStreamException, IOException {
        Files.createDirectories(SCORE_FILE.getParent());
        try (FileOutputStream output = new FileOutputStream(SCORE_FILE.toFile())) {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement(Constants.PLAYERS);
            for (ScoreData data : dataList) {
                writer.writeStartElement(Constants.PLAYER);
                writer.writeStartElement("name");
                writer.writeCharacters(data.getName());
                writer.writeEndElement();
                writer.writeStartElement(Constants.DATE);
                writer.writeCharacters(data.getDate());
                writer.writeEndElement();
                writer.writeStartElement(Constants.SCORE);
                writer.writeCharacters(Integer.toString(data.getScore()));
                writer.writeEndElement();
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        }
    }
}
