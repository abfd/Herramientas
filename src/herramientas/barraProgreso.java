/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author Ana Belen de Frutos
 */

 import java.awt.*;
 import javax.swing.*;
 
public class barraProgreso 
{
    
    public static void main (String[] args)
    {
        final int max = 100;
        final JFrame frame = new JFrame("Barra de progreso");
        
        final JProgressBar pb = new JProgressBar();  
        pb.setMinimum(0);  
        pb.setMaximum(max);  
        pb.setStringPainted(true);       
        // add progress bar    
        frame.setLayout(new FlowLayout());
        frame.getContentPane().add(pb);   
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
        frame.setSize(300, 200);    
        frame.setVisible(true); 
        // update progressbar    
        for (int i = 0; i <= max; i++) 
        {            
            final int currentValue = i;           
            try 
            {           
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {     
                        pb.setValue(currentValue);        
                    }                });      
                java.lang.Thread.sleep(100);    
            } catch (InterruptedException e) 
            {               
                JOptionPane.showMessageDialog(frame, e.getMessage());            
            }        
        }     
    }
} 


