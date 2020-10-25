package ru.corporation.sila_x.experimental_1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class loggerWindow extends Frame //Создаем подкласс класса Frame.
{

    public loggerWindow()  // Конструктор класса.
    {
    super("Логгер");  // Вызываем конструктор суперкласса и передаем ему параметр, в данном случае имя программы.
    setLocation(650, 250);      // Указываем координаты в пикселях того места где появится окно на экране.
    setSize(600,400); // Метод суперкласса для установки размеров окна.
    Button myButton = new Button("Мониторинг"); // Создаем кнопку и надпись на ней.
    Label myLabel = new Label("Данные логгера"); // Создаем текстовое поле и надпись в нем.
    add(myLabel, BorderLayout.NORTH); // Распологаем текстовое поле в северной части окна.
    add(myButton, BorderLayout.SOUTH); // Создаем кнопку в юной части окна.
        // Для кнопки выбираем событие слушателя, и создаем новое событие в скобках.
    myButton.addActionListener(new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent event)

        {
            myLabel.setText("Мониторинг"); // Выполняется действие, т.е. при нажатии на кнопку в поле выводится сообщение "Мониторинг".
        }
      });
    }

    public static void main(String[] args)   // Точка входа программы.
    {
    loggerWindow log = new loggerWindow(); // Создаем обьект класса.
    log.setVisible(true);                  // Устанавливаем видимость окна.
        // Наше окно запускается и отображается, при нажатии на кнопку меняется надпись в текстовом поле.
        // Что-бы закрыть окно необходимо добавить код обработки события, который работает следующим образом:
        // мы вызываем для объекта log метод addWindowListener для того, чтобы назначить слушателя оконных событий.
        // В качестве параметра создаем объект абстрактного класса WindowAdapter,
        // в котором создаем класс и переопределяем метод для обработки события закрытия окна -  dispose.
        log.addWindowListener(new WindowAdapter()
        {
            public void windowClosing (WindowEvent event) // В качестве аргумента передаем событие.
            {
                event.getWindow().dispose();         // уничтожает обьект Frame.
            }
        });
    }
}
