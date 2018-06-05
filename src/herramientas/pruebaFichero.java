/*
 * pruebaFichero.java
 *
 * Created on 13 de noviembre de 2007, 13:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.lang.Object;
import org.apache.commons.codec.binary.Hex;


/**
 *
 * @author Administrador
 */
public class pruebaFichero {
    
    private String cadena1 = "";
    private String clave = "";
    
    public static void main(String [] args ) 
    {
        pruebaFichero pf = new pruebaFichero();                
        
    }//main
    
   
    
    /** Creates a new instance of pruebaFichero */
    public  pruebaFichero() 
    {
                
        //File fichero = new File("/data/informes/caixa/encargos/cuerpo_mail.xml");
        try
        {
            String Fichero = Utilidades.Archivos.readTextFile("/data/informes/caixa/encargos/","cuerpo_mail.xml");
            int inicio = Fichero.indexOf("<cabecera>");
            int fin    = Fichero.indexOf("</solicitudes");
            //cadena1 = Fichero.substring(inicio-1,fin + 14);
            
            //este es el ejemplo que nos enviaron por e-mail para el calculo del nodo signature.
            cadena1 = "<cabecera><aplicacion>TXC</aplicacion><numeroPeticiones>2</numeroPeticiones><fechaHora>02/10/2007 09:30:21</fechaHora><idOperacion>001</idOperacion><operacion>SOLICITUDES DE TASACION</operacion><empresaTasadora>01</empresaTasadora><version>1.0</version><error><codigoError>0</codigoError><descripcion>OK</descripcion></error></cabecera><solicitudes><solicitud><cabeceraSolicitud><idTasacion>464</idTasacion><codigoTasacion>0008-01-00074</codigoTasacion><idMotivo/><motivo/><observaciones/></cabeceraSolicitud><datosPeticion><empresaDatosPeticion>001</empresaDatosPeticion><tasadoraDatosPeticion>000001</tasadoraDatosPeticion><oficinaDatosPeticion>0008</oficinaDatosPeticion><fechaDatosPeticion>25/06/2007</fechaDatosPeticion><horaDatosPeticion>00:00:00</horaDatosPeticion><idTipoTasacionDatosPeticion>01</idTipoTasacionDatosPeticion><tipoTasacionDatosPeticion>Primera tasaci¿n</tipoTasacionDatosPeticion><idTipoOperacionDatosPeticion>01</idTipoOperacionDatosPeticion><tipoOperacionDatosPeticion>Hipoteca Finca Individual</tipoOperacionDatosPeticion><idTipoInmuebleDatosPeticion>10</idTipoInmuebleDatosPeticion><tipoInmuebleDatosPeticion>Aparhotel</tipoInmuebleDatosPeticion><idFinalidadDatosPeticion>01</idFinalidadDatosPeticion><finalidadDatosPeticion>Mercado hipotecario</finalidadDatosPeticion><idEntregaDocumentacionDatosPeticion>05</idEntregaDocumentacionDatosPeticion><entregaDocumentacionDatosPeticion>Fax a la sociedad de tasaci¿n</entregaDocumentacionDatosPeticion><idiomaDocumentacionDatosPeticion>CAS</idiomaDocumentacionDatosPeticion><numOrdenTasacionDatosPeticion/><numTasacionExternoDatosPeticion/></datosPeticion><datosInmueble><idTipoViaDatosInmueble>0</idTipoViaDatosInmueble><tipoViaDatosInmueble/><nombreDatosInmueble>aaaaaaaaaaaaaaaaaaaaaaaaa</nombreDatosInmueble><numeroDatosInmueble>aaaaa</numeroDatosInmueble><pisoDatosInmueble>21</pisoDatosInmueble><puertaDatosInmueble>22</puertaDatosInmueble><bloqueDatosInmueble>23</bloqueDatosInmueble><escaleraDatosInmueble>24</escaleraDatosInmueble><pKmDatosInmueble>25</pKmDatosInmueble><idProvinciaDatosInmueble/><provinciaDatosInmueble/><inePoblacionDatosInmueble>1234</inePoblacionDatosInmueble><poblacionDatosInmueble/><registroPropiedadDatosInmueble>a</registroPropiedadDatosInmueble><personaContactoDatosInmueble>Marimar</personaContactoDatosInmueble><telefono1DatosInmueble>976858585</telefono1DatosInmueble><telefono2DatosInmueble/><codigoPostalDatosInmueble>aaaaa</codigoPostalDatosInmueble></datosInmueble><datosCliente><tipoDocumentoDatosCliente>Física</tipoDocumentoDatosCliente><documentoDatosCliente>25188955E</documentoDatosCliente><nombreDatosCliente>Resolucion modificada</nombreDatosCliente><apellidosDatosCliente>Tipo 05Segundo Apellido</apellidosDatosCliente><empleadoCaixaDatosCliente>NO</empleadoCaixaDatosCliente><matriculaCaixaDatosCliente/><direccionDatosCliente>Luz</direccionDatosCliente><codigoPostalDatosCliente>50006</codigoPostalDatosCliente><poblacionDatosCliente>Zaragoza</poblacionDatosCliente><provinciaDatosCliente>Zaragoza</provinciaDatosCliente><telefonoFijoDatosCliente/><telefonoMovilDatosCliente/><depositoCargoDatosCliente>21009712420200428346</depositoCargoDatosCliente></datosCliente><datosOficina><nombreOficinaDatosOficina>VIC</nombreOficinaDatosOficina><matriculaEmpleadoSolicitanteDatosOficina>1235</matriculaEmpleadoSolicitanteDatosOficina><nombreEmpleadoDatosOficina>JUAN JOSE RIUTORT VIDAL</nombreEmpleadoDatosOficina><direccionOficinaDatosOficina>C. JACINT VERDAGUER, 7</direccionOficinaDatosOficina><codigoPostalOficinaDatosOficina>08500</codigoPostalOficinaDatosOficina><poblacionOficinaDatosOficina>VIC</poblacionOficinaDatosOficina><provinciaOficinaDatosOficina>Barcelona</provinciaOficinaDatosOficina><telefonoOficinaDatosOficina>938832696</telefonoOficinaDatosOficina></datosOficina></solicitud><solicitud><cabeceraSolicitud><idTasacion>71</idTasacion><codigoTasacion>0007-01-00124</codigoTasacion><idMotivo/><motivo/>" +
                      "<observaciones>Tasación que se ha hecho nueva desde previa dando al botón finalizar</observaciones></cabeceraSolicitud><datosPeticion><empresaDatosPeticion>001</empresaDatosPeticion><tasadoraDatosPeticion>000001</tasadoraDatosPeticion><oficinaDatosPeticion>0007</oficinaDatosPeticion><fechaDatosPeticion>01/10/2007</fechaDatosPeticion><horaDatosPeticion>00:00:00</horaDatosPeticion><idTipoTasacionDatosPeticion>02</idTipoTasacionDatosPeticion><tipoTasacionDatosPeticion>Valoraci¿n intermedia de obra</tipoTasacionDatosPeticion><idTipoOperacionDatosPeticion>01</idTipoOperacionDatosPeticion><tipoOperacionDatosPeticion>Hipoteca Finca Individual</tipoOperacionDatosPeticion><idTipoInmuebleDatosPeticion>00</idTipoInmuebleDatosPeticion><tipoInmuebleDatosPeticion>Piso Vivienda / Edificio Viviendas</tipoInmuebleDatosPeticion><idFinalidadDatosPeticion>01</idFinalidadDatosPeticion><finalidadDatosPeticion>Mercado hipotecario</finalidadDatosPeticion><idEntregaDocumentacionDatosPeticion>05</idEntregaDocumentacionDatosPeticion><entregaDocumentacionDatosPeticion>Fax a la sociedad de tasaci¿n</entregaDocumentacionDatosPeticion><idiomaDocumentacionDatosPeticion>CAT</idiomaDocumentacionDatosPeticion><numOrdenTasacionDatosPeticion/><numTasacionExternoDatosPeticion/></datosPeticion><datosInmueble><idTipoViaDatosInmueble>0</idTipoViaDatosInmueble><tipoViaDatosInmueble/><nombreDatosInmueble/><numeroDatosInmueble/><pisoDatosInmueble/><puertaDatosInmueble/><bloqueDatosInmueble/><escaleraDatosInmueble/><pKmDatosInmueble/><idProvinciaDatosInmueble/><provinciaDatosInmueble/><inePoblacionDatosInmueble>0</inePoblacionDatosInmueble><poblacionDatosInmueble/><registroPropiedadDatosInmueble/><personaContactoDatosInmueble/><telefono1DatosInmueble/><telefono2DatosInmueble/><codigoPostalDatosInmueble/></datosInmueble><datosCliente><tipoDocumentoDatosCliente>Física</tipoDocumentoDatosCliente><documentoDatosCliente>25188955E</documentoDatosCliente><nombreDatosCliente>Marimar</nombreDatosCliente><apellidosDatosCliente>Nueva normal</apellidosDatosCliente><empleadoCaixaDatosCliente>NO</empleadoCaixaDatosCliente><matriculaCaixaDatosCliente/><direccionDatosCliente>C/Prueba, 2</direccionDatosCliente><codigoPostalDatosCliente>50006</codigoPostalDatosCliente><poblacionDatosCliente>Zaragoza</poblacionDatosCliente><provinciaDatosCliente>Zaragoza</provinciaDatosCliente><telefonoFijoDatosCliente/><telefonoMovilDatosCliente/><depositoCargoDatosCliente>21009712420200428346</depositoCargoDatosCliente></datosCliente><datosOficina><nombreOficinaDatosOficina>VIC</nombreOficinaDatosOficina><matriculaEmpleadoSolicitanteDatosOficina>U0193895</matriculaEmpleadoSolicitanteDatosOficina><nombreEmpleadoDatosOficina>USUARI 1 PROVES EDS</nombreEmpleadoDatosOficina><direccionOficinaDatosOficina>C. JACINT VERDAGUER, 7</direccionOficinaDatosOficina><codigoPostalOficinaDatosOficina>08500</codigoPostalOficinaDatosOficina><poblacionOficinaDatosOficina>VIC</poblacionOficinaDatosOficina><provinciaOficinaDatosOficina>Barcelona</provinciaOficinaDatosOficina><telefonoOficinaDatosOficina>938832696</telefonoOficinaDatosOficina></datosOficina></solicitud></solicitudes>";
            
            cadena1 +=clave;
            
            cadena1 = cadena1.replace("\n","");    //quitamos los saltos de linea
            cadena1 = cadena1.replace("\t","");    //quitamos los tabuladores                        

                //algoritmo SHA1
                MessageDigest sha1 = MessageDigest.getInstance("SHA1");                
                byte[] b = sha1.digest(cadena1.getBytes());  

                //el resultado lo pasamos a Hexadecimal
                String resultado=""; 
                ByteArrayInputStream input = new ByteArrayInputStream(b); 
                String cadAux; 
                int leido = input.read(); 
                while(leido != -1) 
                {                  
                        cadAux = Integer.toHexString(leido);                   
                        if(cadAux.length() < 2) resultado += "0";        //Hay que añadir un 0                               
                        resultado += cadAux;                         
                        leido = input.read(); 
                } 
                System.out.println(resultado);                                                 
        }
        catch (NoSuchAlgorithmException nsae)
        {
            System.out.println(nsae.toString());
        }
        catch (IOException ioex)
        {
            System.out.println(ioex.toString());
        }
    }
    
    
}
