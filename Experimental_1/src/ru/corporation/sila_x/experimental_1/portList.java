package ru.corporation.sila_x.experimental_1;

import jssc.SerialPortList;

public class portList
{

        public static void main (String [] args)
        {
            String[] portNames = SerialPortList.getPortNames();
            for(int i = 0; i < portNames.length; i++)
            {
                System.out.println(portNames[i]);
            }
        }
}
