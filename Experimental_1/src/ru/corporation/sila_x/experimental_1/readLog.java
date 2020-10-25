package ru.corporation.sila_x.experimental_1;

// Добавляем библиотеку jssc.
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class readLog extends Frame    // Класс чтения из порта.
{
    public static void main(String[] args)
    {
        readLog log = new readLog();
        log.setVisible(true);

        log.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    static Label myLabel = new Label("Данные логгера");

    private static SerialPort serialPort;   //Создаем обьект типа SerialPort.

    public readLog()     // Точка входа в программу.
    {
        super("Логгер");
        setLocation(650, 250);
        setSize(600, 400);
        Button myButton = new Button("Мониторинг");
        add(myLabel, BorderLayout.NORTH);
        add(myButton, BorderLayout.SOUTH);

        myButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                serialPort = new SerialPort("COM5");     // Передаем в конструктор суперкласса имя порта с которым будем работать.
                try
                {
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
                    serialPort.addEventListener(new EventListener());

                }
                catch (SerialPortException ex)
                {
                    System.out.println(ex);
                }
            }
        });
    }

    private static class EventListener implements SerialPortEventListener
    {
        public void serialEvent(SerialPortEvent event)
        {
            if (event.isRXCHAR() && event.getEventValue() > 0)
            {
                try
                {
                    String data = serialPort.readString(event.getEventValue());
                    myLabel.setAlignment(Label.CENTER);
                    myLabel.setText(data);
                    System.out.print(data);
                }
                catch (SerialPortException ex)
                {
                    System.out.println(ex);
                }

            }
        }
    }
}


