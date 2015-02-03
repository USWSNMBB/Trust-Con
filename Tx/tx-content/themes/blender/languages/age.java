import java.util.*;
import javax.swing.*;

class age_calculator {

Boolean reply = true;

	public void display()
	{

    	GregorianCalendar now = new GregorianCalendar();

	while(reply) {

		int now_year=0,birth_year=0,years_old=0;
		String output;

		output = "In what year you are born?";

try
        {
		birth_year = Integer.parseInt
		    (JOptionPane.showInputDialog(null,output));
	 }

catch (final NumberFormatException e)
	         {

	            output = "Please type in a numerical value." + "\n"
	                  + "Try Again ";

	 		 	 JOptionPane.showMessageDialog(null,output,
	 		 	  "Error Report",JOptionPane.INFORMATION_MESSAGE);
	 		      System.exit(0);
   	      }

		now_year = now.get(GregorianCalendar.YEAR);
		years_old = now_year - birth_year;

		output = "This is the year you become "
		         + years_old + " years old.";
		JOptionPane.showMessageDialog(null,output);

       output = "Do you want to continue";
       int n = JOptionPane.showConfirmDialog(null,output,
               "Yes/No", JOptionPane.YES_NO_OPTION);
	             if (n == JOptionPane.NO_OPTION)
	             {
	          reply = false;
              System.out.print("\t Thank you for using this Program");
              System.out.print("\n\n");
              System.exit(0);
                }
        }
     }
}

	class age {

		public static void main(String[] args)
		{

        age_calculator person = new age_calculator();

        person.display();
   }
}
