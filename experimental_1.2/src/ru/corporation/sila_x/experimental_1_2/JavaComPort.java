package ru.corporation.sila_x.experimental_1_2;

// все необходимые импорты.
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

    // Основной класс.
    public class JavaComPort
    {

        // Емкость буфера приема.
        public static final int  BUFSIZE_RD = 4096;
        // Строка приема.
        private String readData = null;
        // Флаг открытия порта.
        private boolean pOpen = false;
        // Главный счетчик принимаемых байт.
        private short comAllCount = 0;
        // Программный приемный буфер.
        private byte [] bufrd = new byte [BUFSIZE_RD];
        // Локальный счетчик принимаемых байт.
        private int bufrdCount = 0;
        // Счетчик циклов приема.
        private int countCom = 0;

//----------------------------------------------------------------------
// создание объектов.

        private String comPort = "COM6";
        private SerialPort serialPort = new SerialPort(comPort);
        private PortReader portReader = new PortReader();
        private JButton stop = new JButton("Stop");
        private JButton start = new JButton("Start");
        private JTextArea input = new JTextArea("input");
        private JScrollPane scrollPaneInput = new JScrollPane(input);
        private JLabel label1 = new JLabel("nbCom");
        private JLabel label2 = new JLabel("Test1");
        private JLabel label3 = new JLabel("Test2");
        private JLabel label4 = new JLabel("Test3");

//----------------------------------------------------------------------
// Inner классы.

        // расширение класса JFrame.
        // Десктопная форма.
        public class Form extends JFrame
        {

            // конструктор класса Form (должен иметь тоже имя Form).
            public Form()
            {
                // инициализация компонентов.
                initComponents();
            }

            // метод инициализации компонентов формы.
            private void initComponents()
            {
                // положение на экране.
                setBounds(150,130,800,600);
                // размер формы.
                setSize(830, 600);
                // Закрытие формы.
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // Контейнер для размещения компонентов формы.
                Container container = getContentPane();
                // установить разметку.
                container.setLayout(null);
                container.setBounds(5,5,800,600);

                //----------------------------------------------------------------------
                // JTextArea.

                // Добавление JTextArea input.
                input.setLineWrap(true);
                input.setColumns(20);
                input.setRows(5);
                input.setBounds(10,220,790,300);
                container.add(input);
                // Добавление скрола.
                scrollPaneInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPaneInput.setBounds(10,220,800,300);
                container.add(scrollPaneInput);
                scrollPaneInput.setViewportView(input);

                //----------------------------------------------------------------------
                // JLabel

                // добавление метки состояния COM порта.
                label1.setBounds(10,5,120,20);
                label1.setText(comPort + " Is close");
                // прозрачный фон.
                label1.setOpaque(true);
                label1.setForeground(Color.red);
                container.add(label1);
                //
                label2.setBounds(10,190,70,20);
                label2.setText("BufRd");
                container.add(label2);
                //
                label3.setBounds(600,530,150,20);
                label3.setText("Jast read bytes");
                container.add(label3);
                //
                label4.setBounds(20,530,250,20);
                label4.setText("The number of info bytes in a packet");
                container.add(label4);

                //----------------------------------------------------------------------
                // JButton.

                // зарегистрировать экземпляр класса обработчика события start.
                start.addActionListener(new startEventListener());
                // добавить кнопку и ее положение.
                start.setBounds(440,155,80,25);
                container.add(start);

                // зарегистрировать экземпляр класса обработчика события stop.
                stop.addActionListener(new stopEventListener());
                stop.setBounds(730,155,80,25);
                container.add(stop);
            }
        }

        // клас имплементации события нажатия start.
        class startEventListener implements ActionListener
        {
            @Override
            // обработка события нажатия на button start.
            public void actionPerformed(ActionEvent e)
            {
                if(serialPort.isOpened() == false)
                {
                    try {
                        serialPort = new SerialPort(comPort);
                        label1.setText(comPort + " Is open");
                        label1.setForeground(Color.green);
                        //Открываем порт.
                        serialPort.openPort();
                        pOpen = true;
                        //Выставляем параметры.
                        serialPort.setParams(SerialPort.BAUDRATE_115200,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        //Устанавливаем ивент лисенер и маску.
                        serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);
                    }
                    catch (SerialPortException ex)
                    {
                        System.out.println(ex);
                        pOpen = false;
                    }
                }
            }
        }

        // клас имплементации события нажатия stop.
        class stopEventListener implements ActionListener
        {
            @Override
            // обработка события нажатия на button stop.
            public void actionPerformed(ActionEvent e)
            {

                // закрытие COM порта
                if(serialPort.isOpened() == true)
                {
                    try {
                        serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
                        serialPort.closePort();
                        label1.setText(comPort + " Is close");
                        label1.setForeground(Color.red);
                        System.out.println("COM остановлен");
                        readData = "";
                    } catch (SerialPortException ex) {
                        Logger.getLogger(JavaComPort.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        // клас имплементации события приема данных в com порт
        class PortReader implements SerialPortEventListener
        {

            short comCount;
            short comLenght = 0;
            private byte[] arrReadComByte = new byte [BUFSIZE_RD];

            @Override
            public void serialEvent(SerialPortEvent event) {

                if(pOpen == true){
                    if(event.isRXCHAR() && event.getEventValue() > 0)
                    {
                        // Получаем ответ от устройства, обрабатываем данные и т.д.
                        try {
                            // Количество принятых байт в одном событии
                            comCount = (short)serialPort.getInputBufferBytesCount();
                            // Счетчик-накопитель принятых байт
                            comAllCount = (short) (comAllCount + comCount);
                            // Байты из буфера COM в матрицу arrReadComByte
                            arrReadComByte = serialPort.readBytes(comCount);
                            // Вход если в буфере COM имеются данные
                            if(comCount > 1){
                                // Поиск синхросимволов и данных о длине пакета
                                for(int i = 0; i < comCount; i++)
                                {
                                    if(arrReadComByte[i] == (byte)0xa1 && arrReadComByte[i + 1] == (byte)0xa2) {
                                        comLenght = (short) (((arrReadComByte[i + 2] & 0x00ff) << 8 | (arrReadComByte[i + 3] & 0x00ff)) + 4);
                                        System.out.println("comLenght " + comLenght);
                                    }
                                    else break;
                                }
                            }
                            // Заполнение приемного буфера и строки readData
                            for (int i = 0; i < comCount; i++)
                            {
                                bufrd[bufrdCount] = arrReadComByte[i];
                                readData = readData + String.format("%02X ", bufrd[bufrdCount]);
                                bufrdCount++;
                            }
                            // Принять весь пакет
                            if(comAllCount >= comLenght)
                            {
                                countCom++;
                                label2.setText(String.format("%d ", countCom));
                                // всего считано байт
                                label3.setText("Jast read bytes  " + Integer.toString(comAllCount));
                                label4.setText("The number of info bytes in a packet  " + Integer.toString(comAllCount - 4));
                                // обнулить глобальный счетчик байт
                                comAllCount = 0;
                                input.setText(null);
                                // вывод принятых данных в окно input
                                onDataReceived(readData);
                                // очистка строки приема
                                readData = "";
                                bufrdCount = 0;
                            }
                        }
                        catch (SerialPortException ex)
                        {
                            System.out.println(ex);
                        }
                    }
                }
            }
            // метод отображения данных в input
            public void onDataReceived(String readData)
            {
                input.append(readData);
            }
        }

//----------------------------------------------------------------------
// Методы

        // вывод списка портов
        public static void nbport ()
        {
            String[] portNames = SerialPortList.getPortNames();
            for(int i = 0; i < portNames.length; i++)
            {
                System.out.println(portNames[i]);
            }
        }

//----------------------------------------------------------------------
// Метод Main

        public static void main(String[] args)
        {

            // Создание объектов
            JavaComPort javaComPort = new JavaComPort();
            JavaComPort.Form form = javaComPort.new Form();

            // Вывод списка COM портов
            nbport();
            // Запуск формы
            form.setVisible(true);

        }
}



