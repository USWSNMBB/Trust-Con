import java.awt.*;
import java.awt.event.*;

public class Warning extends Frame 
{
	GridLayout g;
	Button b1;
	Label l;
	 Warning()
	{
		g = new GridLayout(2,1,10,40);
		setLayout(g);
		l = new Label("Incorrect username or password",Label.CENTER);
		b1 = new Button("Ok");
		add(l);
		add(b1);
		
	b1.addActionListener(new Y());
	addWindowListener(new X());	
	}
	
	class Y implements ActionListener
	{

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource()==b1)
			{
				//dispose();
				System.exit(0);
			}
		}
	}

	class X extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			setVisible(false);
			dispose();
		}
	}

	public Insets getInsets()
	{
		return new Insets(40,40,40,40);
	}
	
	/*public static void main(String args[])
	 {
	 Warning m = new Warning();
	 m.setTitle("Message Box");
	 m.setSize(300,200);
	 m.setVisible(true);


	 }*/



}




	


