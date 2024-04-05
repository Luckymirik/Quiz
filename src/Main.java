import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static int curQuestion,allQuestion,trueAnswers;
    private static String curTrue;
    private static Document document;
    private static JLabel label;
    private static JTextArea textArea;
    private static final JFrame frame = new JFrame("Викторина");
    private static final JPanel panel1 = new JPanel(new GridBagLayout()), panel2 = new JPanel(new GridBagLayout()),
            panel3 = new JPanel(new GridBagLayout());
    private static final GridBagConstraints gbc = new GridBagConstraints();
    private static void nextQuestion(){
        curQuestion++;
        label.setText("Вопрос " + curQuestion + " из " + allQuestion);

        Color greenColor = new Color(0,250,0);
        Color yellowColor = new Color(240,230,50);
        Color orangeColor = new Color(250,150,0);
        Color redColor = new Color(250,50,50);

        Node question = document.getElementsByTagName("name").item(curQuestion - 1);
        textArea.setText(question.getTextContent());
        curTrue = question.getAttributes().getNamedItem("true").getTextContent();
        Element answerElement = (Element) document.getElementsByTagName("answer");
        NodeList answers = answerElement.getElementsByTagName("answer");

        var buttons = new ArrayList<JButton>();
        Font fontButton = new Font("Roboto",Font.BOLD,40);

        panel3.removeAll();

        for (int i = 0; i < answers.getLength(); i++) {
            buttons.add(new JButton(String.valueOf(i)));
            JButton newButton = buttons.get(i);
            newButton.setFont(fontButton);
            newButton.setText(answers.item(i).getTextContent());
            panel3.add(newButton,gbc);

            if(String.valueOf(i+1).equals(curTrue)) {
                curTrue = answers.item(i).getTextContent();
            }

            newButton.addActionListener(e -> {
                if(newButton.getText().equals(curTrue)){
                    trueAnswers++;
                    newButton.setBackground(greenColor);
                }else {
                    newButton.setBackground(redColor);
                }
                for (JButton b: buttons) {
                    b.setEnabled(false);
                }
                frame.setVisible(true);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (curQuestion<allQuestion) {
                            nextQuestion();
                        }else {
                            int percent = trueAnswers*100/allQuestion;
                            panel2.removeAll();
                            frame.remove(panel1);
                            frame.remove(panel3);
                            if (percent>74) {
                                panel2.setBackground(greenColor);
                            } else if (percent<25) {
                                panel2.setBackground(redColor);
                            } else if (percent<50) {
                                panel2.setBackground(orangeColor);
                            }else {
                                panel2.setBackground(yellowColor);
                            }
                            JLabel labelResult = new JLabel("Правильных ответов: " + trueAnswers + " из "
                            + allQuestion + " или " + percent + "%",SwingConstants.CENTER);
                            labelResult.setFont(fontButton);
                            panel2.add(labelResult,gbc);
                            frame.setVisible(true);
                        }
                    }
                },1000);

            });
            
        }
        frame.pack();
        frame.setVisible(true);
    }
    static void openXML() throws ParserConfigurationException, IOException, SAXException{
        File file = new File("victorina.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        allQuestion = document.getElementsByTagName("question").getLength();
    }

    public static void main(String[] args) throws ParserConfigurationException,IOException,SAXException {
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        panel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel3.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        label = new JLabel();
        panel1.add(label,gbc);

        textArea = new JTextArea();
        textArea.setEnabled(false);
        textArea.setLineWrap(true);
        textArea.setColumns(30);
        textArea.setFont(new Font("Roboto",Font.BOLD,20));
        textArea.setDisabledTextColor(Color.BLACK);
        panel2.add(textArea,gbc);

//        String[] questions = new String[]{
//                "Кто является родоначальником дома Старков?",
//                "Какое имя носит дракон у Дайнерис Таргариен?",
//                "Кто является королем Железных островов?",
//                "Кто убил Джоффри Баратеона на его свадьбе?",
//                "Кто из перечисленных персонажей является отцом Джейми Ланнистера?",
//                "Кто является создателем книжной серии \"Песнь льда и пламени\" " +
//                        "на основе которой был снят сериал \"Игра престолов\" ?",
//                "Что такое \"красная свадьба\"?",
//                "Какое прозвище имеет Питер Бейлиш?",
//                "Какое имя носит замок на севере?"
//                "Кто из перечисленных перечисленных персонажей чаще всего произносит фразу \"Зима близко\"?",
//                "Кто убил Роберта Баратеона?"
//
//        };
//        String[][] nameButtons = new String[][]{"Эддард Старк","Роб Старк","Кейтлин Старк","Бран Старк"},
//        {"kkkk",};


        frame.add(panel1,BorderLayout.NORTH);
        frame.add(panel2,BorderLayout.CENTER);
        frame.add(panel3,BorderLayout.SOUTH);

        openXML();
        nextQuestion();

    }
}