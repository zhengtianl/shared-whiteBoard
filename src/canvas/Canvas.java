/**
 * Class for user interface of the canvas.
 */

package canvas;

import server.InterFaceBoardMgr ;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;

public class Canvas extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String user;
    private final boolean isBoardManager;
    private String paintType = Utils.free;
    private Color color = Color.black;
    private Point startPoint, end;
    private String text = "";
    private final InterFaceBoardMgr  boardManager;
    private Graphics2D D2;
    private BufferedImage canvasFrame;
    private BufferedImage savedcanvasFrame;


    public Canvas(InterFaceBoardMgr  boardManager, String user, boolean isBoardManager) {
        this.boardManager = boardManager;
        this.user = user;
        this.isBoardManager = isBoardManager;

        // Mouse pressed => startPoint position
        addMouseListener(startPointListener);
        // Monitor motion of the mouse
        addMouseMotionListener(motionListener);
        // Mouse released => end position
        addMouseListener(endListener);

        setDoubleBuffered(false);
    }


    // Render the canvas for a newly joined client
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvasFrame == null) {
            // Render a blank for the first joined client (manager)
            if (isBoardManager) {
                canvasFrame = new BufferedImage(Utils.canvasWidth, Utils.canvasHeight, BufferedImage.TYPE_INT_RGB);
                D2 = (Graphics2D) canvasFrame.getGraphics();
                D2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                D2.setPaint(this.color);
                D2.setStroke(Utils.defaultStroke);
                cleanCanvas();
            } else {
                // Render the current canvas to the newly joined client
                try {
                    byte[] image = boardManager.sendCurrentCanvas();
                    canvasFrame = ImageIO.read(new ByteArrayInputStream(image));
                    D2 = (Graphics2D) canvasFrame.getGraphics();
                    D2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    D2.setPaint(this.color);
                    D2.setStroke(Utils.defaultStroke);
                } catch (Exception e) {
                    System.out.println("Render error");
                }
            }
        }
        g.drawImage(canvasFrame, 0, 0, null);
    }


    public Color getColor() {
        return color;
    }

    public Graphics2D getD2() {
        return D2;
    }

    public BufferedImage getcanvasFrame() {
        return canvasFrame;
    }

    public void rendercanvasFrame(BufferedImage f) {
        D2.drawImage(f, 0, 0, null);
        repaint();
    }

    // Clean up the canvas
    public void cleanCanvas() {
        D2.setPaint(Color.white);
        D2.fillRect(0, 0, Utils.canvasWidth, Utils.canvasHeight);
        D2.setPaint(color);
        repaint();
    }

    // Save the canvas as an image
    public void saveCanvas() {
        ColorModel cm = canvasFrame.getColorModel();
        WritableRaster raster = canvasFrame.copyData(null);
        savedcanvasFrame = new BufferedImage(cm, raster, false, null);
    }

    // Get image of the current canvas
    public BufferedImage getCanvasImage() {
        saveCanvas();
        return savedcanvasFrame;
    }

/***********************************************Mouse Listeners********************************************************/
    private final MouseListener startPointListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                startPoint = event.getPoint();
                saveCanvas();
                try {
                    InterFaceCanvasMsg msg = new CanvasMessage(Utils.paintStart, paintType, color, startPoint, text, user);
                    boardManager.broadcastMsg(msg);
                } catch (RemoteException e) {
                    JOptionPane.showMessageDialog(null, "Unable to draw, server is shut down!");
                }
            }
        }
    };

    private final MouseMotionAdapter motionListener = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent event) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                end = event.getPoint();
                Shape shape = null;
                if (D2 != null) {
                    // Generate different shapes according to types of drawings
                    switch (paintType) {
                        case Utils.line:
                            rendercanvasFrame(savedcanvasFrame);
                            shape = drawLine(startPoint, end);
                            break;
                        case Utils.circle:
                            rendercanvasFrame(savedcanvasFrame);
                            shape = drawCircle(startPoint, end);
                            break;
                        case Utils.Oval:
                            rendercanvasFrame(savedcanvasFrame);
                            shape = drawOval(startPoint, end);
                            break;
                        case Utils.rectangle:
                            rendercanvasFrame(savedcanvasFrame);
                            shape = drawRectangle(startPoint, end);
                            break;
                        case Utils.free:
                            shape = drawLine(startPoint, end);
                            startPoint = end;
                            try {
                                InterFaceCanvasMsg msg = new CanvasMessage(Utils.painting, paintType, color, end, text, user);
                                boardManager.broadcastMsg(msg);
                            } catch (RemoteException e) {
                                JOptionPane.showMessageDialog(null, "Unable to connect to server!");
                            }
                            break;
                        case Utils.text:
                            rendercanvasFrame(savedcanvasFrame);
                            D2.setFont(Utils.defaultFont);
                            D2.drawString("Text", end.x, end.y);
                            // shape = drawText(startPoint);
                            break;
                        case Utils.eraser:
                            shape = drawLine(startPoint, end);
                            startPoint = end;
                            D2.setPaint(Color.white);
                            D2.setStroke(Utils.thickStroke);
                            try {
                                InterFaceCanvasMsg msg = new CanvasMessage(Utils.painting, paintType, Color.white, end, text, user);
                                boardManager.broadcastMsg(msg);
                            } catch (RemoteException e) {
                                JOptionPane.showMessageDialog(null, "Unable to connect to server!");
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + paintType);
                    }
                    if (!paintType.equals(Utils.text)) {
                        D2.draw(shape);
                    }
                    repaint();
                }
            }
        }
    };

    private final MouseListener endListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                end = event.getPoint();
                Shape shape = null;
                if (D2 != null) {
                    // Generate different shapes according to types of drawings
                    switch (paintType) {
                        case Utils.line:
                        case Utils.free:
                        case Utils.eraser:
                            shape = drawLine(startPoint, end);
                            break;
                        case Utils.circle:
                            shape = drawCircle(startPoint, end);
                            break;
                        case Utils.Oval:
                            shape = drawOval(startPoint, end);
                            break;
                        case Utils.rectangle:
                            shape = drawRectangle(startPoint, end);
                            break;
                        case Utils.text:
                            // Ask for text input
                            text = JOptionPane.showInputDialog("Type your text here");
                            if (text == null) text = "";
                            rendercanvasFrame(savedcanvasFrame);
                            D2.setFont(Utils.defaultFont);
                            D2.drawString(text, end.x, end.y);
                            break;
                    }
                    // Broadcast changes to all clients
                    try {
                        InterFaceCanvasMsg msg;
                        if (paintType.equals(Utils.eraser)) {
                            msg = new CanvasMessage(Utils.paintEnd, paintType, Color.white, end, text, user);
                        } else {
                            msg = new CanvasMessage(Utils.paintEnd, paintType, color, end, text, user);
                        }
                        boardManager.broadcastMsg(msg);
                    } catch (RemoteException e) {
                        JOptionPane.showMessageDialog(null, "Unable to connect to server!");
                    }
                    // Draw on the canvas if it is not a text input
                    if (!paintType.equals(Utils.text)) {
                        try {
                            D2.draw(shape);
                        } catch (NullPointerException e) {
                            System.out.println("Drawing error!");
                        }
                    }
                    repaint();
                    // Restore the original color and stroke
                    D2.setPaint(color);
                    D2.setStroke(Utils.defaultStroke);
                }
            }
        }
    };

/**************************************************Types of Drawings***************************************************/
    public void line() {
        this.paintType = Utils.line;
    }

    public void circle() {
        this.paintType = Utils.circle;
    }

    public void Oval() {
        this.paintType = Utils.Oval;
    }

    public void rectangle() {
        this.paintType = Utils.rectangle;
    }

    public void text() {
        this.paintType = Utils.text;
    }

    public void free() {
        this.paintType = Utils.free;
    }

    public void eraser() {
        this.paintType = Utils.eraser;
    }


    public Shape drawLine(Point startPoint, Point end) {
        return new Line2D.Double(startPoint.x, startPoint.y, end.x, end.y);
    }

    public Shape drawCircle(Point startPoint, Point end) {
        int width = Math.abs(startPoint.x - end.x);
        int height = Math.abs(startPoint.y - end.y);
        int x = Math.min(startPoint.x, end.x);
        int y = Math.min(startPoint.y, end.y);
        return new Ellipse2D.Double(x, y, Math.max(width, height), Math.max(width, height));
    }

    public Shape drawOval(Point startPoint, Point end) {
        int minX = Math.min(startPoint.x, end.x);
        int minY = Math.min(startPoint.y, end.y);
        int width = Math.abs(end.x - startPoint.x);
        int height = Math.abs(end.y - startPoint.y);
        return new Ellipse2D.Double(minX, minY, width, height);
    }

    public Shape drawRectangle(Point startPoint, Point end) {
        int width = Math.abs(startPoint.x - end.x);
        int height = Math.abs(startPoint.y - end.y);
        int x = Math.min(startPoint.x, end.x);
        int y = Math.min(startPoint.y, end.y);
        return new Rectangle2D.Double(x, y, width, height);
    }

/*********************************************The Sixteen Named Colors*************************************************/
    public void black() {
        this.color = Color.black;
        this.D2.setPaint(this.color);
    }

    public void white() {
        this.color = Color.white;
        this.D2.setPaint(this.color);
    }

    public void gray() {
        this.color = Color.gray;
        this.D2.setPaint(this.color);
    }

    public void silver() {
        this.color = Utils.aqua;
        this.D2.setPaint(this.color);
    }

    public void maroon() {
        this.color = Utils.aqua;
        this.D2.setPaint(this.color);
    }

    public void red() {
        this.color = Color.red;
        this.D2.setPaint(this.color);
    }

    public void purple() {
        this.color = Utils.aqua;
        this.D2.setPaint(this.color);
    }

    public void fuchsia() {
        this.color = Utils.fuchsia;
        this.D2.setPaint(this.color);
    }

    public void green() {
        this.color = Utils.bgColor;
        this.D2.setPaint(this.color);
    }

    public void lime() {
        this.color = Utils.fuchsia;
        this.D2.setPaint(this.color);
    }

    public void olive() {
        this.color = Utils.bgColor;
        this.D2.setPaint(this.color);
    }

    public void yellow() {
        this.color = Color.yellow;
        this.D2.setPaint(this.color);
    }

    public void navy() {
        this.color = Utils.fuchsia;
        this.D2.setPaint(this.color);
    }

    public void blue() {
        this.color = Color.blue;
        this.D2.setPaint(this.color);
    }

    public void teal() {
        this.color = Utils.teal;
        this.D2.setPaint(this.color);
    }

    public void aqua() {
        this.color = Utils.aqua;
        this.D2.setPaint(this.color);
    }

}