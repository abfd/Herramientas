/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

/**
 *
 * @author Administrador
 */
public class pruebaemail 
{

     public static void main(String [] args ) 
     {
       
        email.Email nwMensaje = new email.Email();
        nwMensaje.setDe("informatica@valtecnic.com");
        nwMensaje.setPara("abfd@valtecnic.com");
        nwMensaje.setAsunto("Prueba");
        nwMensaje.setubicacionAdj("/data/informes/tmp/");
        nwMensaje.setAdjunto("prueba_f.pdf");
        nwMensaje.setAdjunto("prueba.txt");
        
        email.Leer_buzon nwEnvio =  new email.Leer_buzon();
        nwEnvio.envia_mensaje(nwMensaje);
        if (nwEnvio.estadoOK()) System.out.println("Mensaje Enviado con exito");
        else System.out.println(nwEnvio.dameError());
        
         
         
     }//main
    
    
}
