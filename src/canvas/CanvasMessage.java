package canvas;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CanvasMessage extends UnicastRemoteObject implements InterFaceCanvasMsg {

    private static final long serialVersionUID = 1L;
    private final String drawState;
    private final String paintType;
    private final Color color;
    private final Point point;
    private final String text;
    private final String username;

    public CanvasMessage(String state, String msgType, Color color, Point point, String text, String username) throws RemoteException {
        this.drawState = state == null ? "" : state;
        this.paintType = msgType == null ? "" : msgType;
        this.color = color == null ? Color.BLACK : color;
        this.point = point == null ? new Point(0,0) : point;
        this.text = text == null ? "" : text;
        this.username = username == null ? "" : username;
    }

    @Override
    public String getPaintState() throws RemoteException {
        return this.drawState;
    }

    @Override
    public String getPaintType() throws RemoteException {
        return this.paintType;
    }

    @Override
    public Color getColor() throws RemoteException {
        return this.color;
    }

    @Override
    public Point getPoint() throws RemoteException {
        return this.point;
    }

    @Override
    public String getText() throws RemoteException {
        return this.text;
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public String toString() {
        return "CanvasMessage{" +
                "drawState='" + drawState + '\'' +
                ", paintType='" + paintType + '\'' +
                ", color=" + color +
                ", point=" + point +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
