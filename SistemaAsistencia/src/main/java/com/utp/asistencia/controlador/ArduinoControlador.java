package com.utp.asistencia.controlador;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.nio.charset.StandardCharsets;

public class ArduinoControlador {

    private SerialPort puertoSerial;
    private StringBuilder buffer = new StringBuilder();

    public interface ArduinoListener {
        void onMessageReceived(String message);
        void onConnectionLost();
    }

    private ArduinoListener listener;

    public boolean conectar(String puerto, ArduinoListener listener) {
        this.listener = listener;
        puertoSerial = SerialPort.getCommPort(puerto);
        puertoSerial.setBaudRate(9600);
        puertoSerial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (puertoSerial.openPort()) {
            puertoSerial.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
                    
                    byte[] newData = new byte[puertoSerial.bytesAvailable()];
                    int numRead = puertoSerial.readBytes(newData, newData.length);
                    String data = new String(newData, 0, numRead, StandardCharsets.UTF_8);
                    
                    for (char c : data.toCharArray()) {
                        if (c == '\n' || c == '\r') {
                            String msg = buffer.toString().trim();
                            if (!msg.isEmpty()) {
                                listener.onMessageReceived(msg);
                            }
                            buffer.setLength(0);
                        } else {
                            buffer.append(c);
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    public void enviarComando(String comando) {
        if (puertoSerial != null && puertoSerial.isOpen()) {
            byte[] bytes = comando.getBytes(StandardCharsets.UTF_8);
            puertoSerial.writeBytes(bytes, bytes.length);
        }
    }

    public void desconectar() {
        if (puertoSerial != null && puertoSerial.isOpen()) {
            puertoSerial.removeDataListener();
            puertoSerial.closePort();
        }
    }

    public boolean estaConectado() {
        return puertoSerial != null && puertoSerial.isOpen();
    }
}
