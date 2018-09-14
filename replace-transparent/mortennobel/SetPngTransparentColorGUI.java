import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Replace pixels with full transparency with a certain color to fix issues with resampling images
 */
public class SetPngTransparentColorGUI extends JFrame implements DropTargetListener {

    private DropTarget dropTarget;

    private JPanel colorPanel = new JPanel();

    public SetPngTransparentColorGUI() throws HeadlessException {
        super("Set Png Transparent Color");

        if (dropTarget == null) {
            dropTarget = new DropTarget(this, this);
        }

        getContentPane().setLayout(new BorderLayout());

        colorPanel.setBackground(Color.white);
        colorPanel.setMinimumSize(new Dimension(50,50));
        colorPanel.setPreferredSize(new Dimension(50, 50));
        colorPanel.setBorder(new LineBorder(Color.black));
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Color color = JColorChooser.showDialog(SetPngTransparentColorGUI.this, "Select color", colorPanel.getBackground());
                if (color != null){
                    colorPanel.setBackground(color);
                }
            }
        });

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(new JLabel("Replacement color"), BorderLayout.CENTER);
        northPanel.add(colorPanel, BorderLayout.EAST);

        getContentPane().add(northPanel , BorderLayout.NORTH);

        getContentPane().add(new JLabel("Drop files here"), BorderLayout.CENTER);

    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragExit(DropTargetEvent dropTargetEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void drop(DropTargetDropEvent evt) {
        final ArrayList result = new ArrayList();
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            DataFlavor flavors[] = data.getTransferDataFlavors();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                java.util.List<File> list = (java.util.List<File>) data.getTransferData(
                        DataFlavor.javaFileListFlavor);
                processFiles(list);
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            evt.dropComplete(true);
            repaint();
        }
    }

    private void processFiles(java.util.List<File> files) throws IOException {
        for (File f : files){
            try {
                System.out.println("Processing file "+f.getName());
                SetPngTransparentColor(f, colorPanel.getBackground());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SetPngTransparentColorGUI f = new SetPngTransparentColorGUI();
        f.setVisible(true);
        f.setSize(500,500);
        f.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public static void SetPngTransparentColor(File file, Color color) throws Exception{
        BufferedImage bufferedImage = ImageIO.read(file);
        int count = 0;
        int countNot = 0;
        BufferedImage bi = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int x=0;x<bufferedImage.getWidth();x++){
            for (int y=0;y<bufferedImage.getHeight();y++){
                int rgba = bufferedImage.getRGB(x,y);
                boolean isTrans = (rgba & 0xff000000) == 0;
                if (isTrans){
                    bi.setRGB(x,y, (color.getRGB()&0x00ffffff));
                    count ++;
                } else {
                    countNot++;
                    bi.setRGB(x,y,rgba);
                }
            }
        }
        System.out.println("Count "+count+"/"+countNot);
        ImageIO.write(bi, "png", file);
    }
}
