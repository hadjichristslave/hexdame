import javax.swing.*;
import java.applet.*;
import java.awt.*;
public class CheckerAppletz extends Applet
{
	public static int rows = 8;
	public static int columns = 8;
	public static Color col1 = Color.WHITE;
	public static Color col2 = Color.WHITE;
	public void init()
	{
            
		setLayout(new BoxLayout(this, rows));
		Color temp;
		for (int i = 0; i < rows; i++)
		{
			if (i%2 == 0)
			{
				temp = col1;
			}
			else
			{
				temp = col2;
			}
			for (int j = 0; j < columns; j++)
			{
				JPanel panel = new JPanel();
				//panel.setBackground(temp);
				if (temp.equals(col1))
				{
					temp = col2;
				}
				else
				{
					temp = col1;
				}
                                Polygon p = new Polygon();
                                
                                System.out.println("YELLOW");
                                panel.add(new DrawPolyPanel());
                                
				//add(panel);
			}
		}
	}
    public class DrawPolyPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Polygon p = new Polygon();
        for (int i = 0; i < 5; i++)
          p.addPoint((int) (100 + 50 * Math.cos(i * 2 * Math.PI / 5)),
              (int) (100 + 50 * Math.sin(i * 2 * Math.PI / 5)));

        g.drawPolygon(p);
        }
    }
}
