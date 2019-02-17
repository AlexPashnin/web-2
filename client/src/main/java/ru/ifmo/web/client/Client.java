package ru.ifmo.web.client;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Client {
    public static void main(String... args) throws SQLException_Exception, IOException {
        URL url = new URL("http://localhost:8080/astartes?wsdl");
        Astartes_Service astartesService = new Astartes_Service(url);
        AstartesService astartesPort = astartesService.getAstartesServicePort();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int currentState = 0;

        while (true) {
            switch (currentState) {
                case 0:
                    System.out.println("\nВыберите один из пунктов:");
                    System.out.println("1. Вывести всех астартес");
                    System.out.println("2. Применить фильтры");
                    System.out.println("3. Создать");
                    System.out.println("4. Изменить");
                    System.out.println("5. Удалить");
                    System.out.println("6. Выйти");
                    currentState = readState(currentState, reader);
                    break;
                case 1:
                    System.out.println("Найдено:");
                    astartesPort.findAll().stream().map(Client::astartesToString).forEach(System.out::println);
                    currentState = 0;
                    break;
                case 2:
                    System.out.println("\nЧтобы не применять фильтр, оставьте значение пустым");
                    System.out.println("id:");
                    Long id = readLong(reader);
                    System.out.println("name:");
                    String name = readString(reader);
                    System.out.println("title:");
                    String title = readString(reader);
                    System.out.println("position:");
                    String position = readString(reader);
                    System.out.println("planet:");
                    String planet = readString(reader);
                    System.out.println("birthdate(yyyy-mm-dd):");
                    XMLGregorianCalendar birthdate = readDate(reader);
                    System.out.println("Найдено:");
                    astartesPort.findWithFilters(id, name, title, position, planet, birthdate).stream().map(Client::astartesToString).forEach(System.out::println);
                    currentState = 0;
                    break;
                case 3:
                    System.out.println("\nЗаполните все поля");
                    String createName;
                    do {
                        System.out.println("name:");
                        createName = readString(reader);
                    } while (createName == null);
                    String createTitle;
                    do {
                        System.out.println("title:");
                        createTitle = readString(reader);
                    } while (createTitle == null);
                    String createPosition;
                    do {
                        System.out.println("position:");
                        createPosition = readString(reader);
                    } while (createPosition == null);
                    String createPlanet;
                    do {
                        System.out.println("planet:");
                        createPlanet = readString(reader);
                    } while (createPlanet == null);
                    XMLGregorianCalendar createBirthdate;
                    do {
                        System.out.println("birthdate(yyyy-mm-dd):");
                        createBirthdate = readDate(reader);
                    } while (createBirthdate == null);
                    Long createdId = 0L;
                    System.out.println("ID новой записи: " + createdId);
                    currentState = 0;
                    break;
                case 4:
                    Long updateId;
                    do {
                        System.out.println("id изменяемой записи (0 для отмены операции):");
                        updateId = readLong(reader);
                    } while (updateId == null);

                    if (updateId == 0L) {
                        currentState = 0;
                        break;
                    }
                    System.out.println("name:");
                    String updateName = readString(reader);
                    System.out.println("title:");
                    String updateTitle = readString(reader);
                    System.out.println("position:");
                    String updatePosition = readString(reader);
                    System.out.println("planet:");
                    String updatePlanet = readString(reader);
                    System.out.println("birthdate(yyyy-mm-dd):");
                    XMLGregorianCalendar updateBirthdate = readDate(reader);
                    System.out.println("Значение изменено (или нет. Об этом мы узнаем в следующей лабе)");
                    currentState = 0;
                    break;
                case 5:
                    Long deleteId;
                    do {
                        System.out.println("id удаляемой записи (0 для отмены операции):");
                        deleteId = readLong(reader);
                    } while (deleteId == null);
                    if (deleteId == 0L) {
                        currentState = 0;
                        break;
                    }
                    System.out.println("Значение удалено (или нет. Об этом мы узнаем в следующей лабе)");
                    currentState = 0;
                    break;
                case 6:
                    return;
                default:
                    currentState = 0;
                    break;
            }
        }
    }

    private static String readString(BufferedReader reader) throws IOException {
        String trim = reader.readLine().trim();
        if (trim.isEmpty()) {
            return null;
        }
        return trim;
    }

    private static XMLGregorianCalendar readDate(BufferedReader reader) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date rd = sdf.parse(reader.readLine());
            XMLGregorianCalendar birthdate;

            GregorianCalendar c = new GregorianCalendar();

            if (rd != null) {
                c.setTime(rd);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } else {
                return null;
            }
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static Long readLong(BufferedReader reader) {
        try {
            return Long.parseLong(reader.readLine());
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static int readState(int current, BufferedReader reader) {
        try {
            return Integer.parseInt(reader.readLine());
        } catch (java.lang.Exception e) {
            return current;
        }
    }

    private static String astartesToString(Astartes astartes) {
        return "Astartes(" +
                "name=" + astartes.getName() +
                ", title=" + astartes.getTitle() +
                ", position=" + astartes.getPosition() +
                ", planet=" + astartes.getPlanet() +
                ", birthdate=" + astartes.getBirthdate() +
                ")";
    }

}
