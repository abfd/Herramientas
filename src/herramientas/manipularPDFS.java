/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Administrador
 */
public class manipularPDFS                         
{
    public static void main(String[] args) 
    {
       manipularPDFS manipulaPDFS = new manipularPDFS();
    }
    
    public  manipularPDFS ()
    {
        try
        {
            File portada = new File ("/data/informes/WSClientSegipsa20/pruebas/portada.pdf");
            File resumen = new File ("/data/informes/WSClientSegipsa20/pruebas/resumen.pdf");
            com.lowagie.text.pdf.PdfReader readerpdf = new PdfReader("/data/informes/WSClientSegipsa20/pruebas/INFORME COMPETO.pdf");        
            int longitud = readerpdf.getNumberOfPages();
            PdfImportedPage page;            
            Document documentpdf = new Document(readerpdf.getPageSizeWithRotation(1));
            PdfCopy portadapdf = new PdfCopy(documentpdf, new FileOutputStream(portada));    
            PdfCopy resumenpdf = new PdfCopy(documentpdf, new FileOutputStream(resumen));    
            documentpdf.open();
            for (int i=1;i<=longitud;i++)
            {
                
                if (i == 1)
                {
                    page = portadapdf.getImportedPage(readerpdf, i);
                    portadapdf.addPage(page);
                }
                else
                {
                    page = portadapdf.getImportedPage(readerpdf, i);
                    resumenpdf.addPage(page);
                }
            }
            portadapdf.close();
            resumenpdf.close();
            documentpdf.close();
        }
        catch (IOException ioe)
        {
            
        }
        catch (DocumentException doce)
        {
            
        }

    }
}
