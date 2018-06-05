/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author cmlc
 */
public class Exptcaract
{
    public String numexp = null;   //parametro
    public Integer idnumero = null;  //parametro
    public Integer codcaract = null;
    public String valor = null;
    
    public Integer tipo = null;
   //*******************************************
   //*******************************************
    public static final String TABLA="EXPTCARACT";
    
   //*******************************************
   //*******************************************
    public static final Integer CODCARACT_PISINA = 42;
    public static final Integer CODCARACT_ANIO_ULTIMA_REFORMA = 8;
    
    
 //*****************************************************************************    
 //*****************************************************************************   
    public void clear() throws java.lang.Exception
    {    
            numexp = null;
            idnumero = null;
            codcaract = null;
            valor = null;    
            tipo = null;
    } //clear 

//******************************************************************************
//******************************************************************************  
      /**
     * Metodo que carga los datos de la base de datos en un objeto.
     * @param numexp Identificador del expediente.    
     * @param idnumero Identificador.    
     * @param codcaract Codigo de caracteres.
     * @param conexion Conexión a la base de datos.
     * @return Devuelve true si se ha conseguido cargar, false en caso contrario.
     * @throws java.lang.Exception Excepción general capturada.
     */
  public boolean load(String numexp, Integer idnumero, Integer codcaract, java.sql.Connection conexion) throws java.lang.Exception
  {
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsDatos = null;
        boolean bExiste = false;
        if(numexp!=null && !numexp.trim().equals("")  && idnumero!=null && codcaract!=null && conexion!=null)
        {
           try
           {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("*");
                oConsulta.from(TABLA);               
                oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);    
                oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);    
                oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);
                rsDatos = Utilidades.Conexion.select(oConsulta.getSql(), conexion);
                bExiste = rsDatos.next();
                if(bExiste)
                {                      
                      this.numexp = Utilidades.Cadenas.getValorSinBlancos(rsDatos, "numexp");        
                      this.idnumero = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"idnumero");      
                      this.codcaract = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"codcaract");  
                      valor = Utilidades.Cadenas.getValorSinBlancos(rsDatos, "valor");
                      tipo = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"tipo");  
                }//if
           }//try
           catch(java.lang.Exception e)
           {
               throw e;
           }//catch
           finally
           {                
               if(rsDatos!=null) rsDatos.close();
               rsDatos = null;
               oConsulta = null;
           }//finally
        }//if
        return bExiste;
  }//load por numexp-idnumero-codcaract
 //******************************************************************************
//******************************************************************************  
      /**
     * Metodo que carga los datos de la base de datos en un objeto.
     * @param numexp Identificador del expediente.    
     * @param idnumero Identificador.    
     * @param codcaract Codigo de caracteres.
     * @param conexion Conexión a la base de datos.
     * @return Devuelve true si se ha conseguido cargar, false en caso contrario.
     * @throws java.lang.Exception Excepción general capturada.
     */
  public boolean load(String numexp, Integer idnumero, java.sql.Connection conexion) throws java.lang.Exception
  {
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsDatos = null;
        boolean bExiste = false;
        if(numexp!=null && !numexp.trim().equals("")  && idnumero!=null  && conexion!=null)
        {
           try
           {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("*");
                oConsulta.from(TABLA);               
                oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);    
                oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);     
                
                rsDatos = Utilidades.Conexion.select(oConsulta.getSql(), conexion);
                bExiste = rsDatos.next();
                if(bExiste)
                {                      
                      this.numexp = Utilidades.Cadenas.getValorSinBlancos(rsDatos, "numexp");        
                      this.idnumero = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"idnumero");      
                      codcaract = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"codcaract");  
                      valor = Utilidades.Cadenas.getValorSinBlancos(rsDatos, "valor");
                      tipo = Utilidades.Cadenas.getValorTipoEnteroMostrarWeb(rsDatos,"tipo");  
                }//if
           }//try
           catch(java.lang.Exception e)
           {
               throw e;
           }//catch
           finally
           {                
               if(rsDatos!=null) rsDatos.close();
               rsDatos = null;
               oConsulta = null;
           }//finally
        }//if
        return bExiste;
  }//load por numexp-idnumero
                                   
//******************************************************************************
//******************************************************************************
   /**
     * Metodo que carga los datos de los campos en un objeto, con request.
     * @param request Objeto Request con los campos a cargar en el objeto.
     * @return Numeros de registros afectados.
     */
    public int load(javax.servlet.http.HttpServletRequest request)
    {
        int iNumeroRegistroAfectados = 0;
        if (Utilidades.Request.getString(request,"numexp")!=null && !Utilidades.Request.getString(request,"numexp").equals("") && Utilidades.Request.getInteger(request, "idnumero")!=null && Utilidades.Request.getInteger(request, "codcaract")!=null)  
        {         
                 numexp = Utilidades.Request.getString(request, "numexp");
                 idnumero = Utilidades.Request.getInteger(request,"idnumero");                                                 
                 codcaract = Utilidades.Request.getInteger(request,"codcaract");               
                 valor = Utilidades.Request.getString(request, "valor");
                 tipo = Utilidades.Request.getInteger(request,"tipo"); 
                 
                 iNumeroRegistroAfectados = 1;
        }//if
        return iNumeroRegistroAfectados;
    }//load con request
 
//******************************************************************************
//******************************************************************************
   /**
   * Método que carga los datos de la base de datos en un ResultSet.
   * @param conexion Conexión a la base de datos.
   * @return rsDatos Estructura con los datos cargados.
   * @throws java.lang.Exception Excepción general capturada.
   */
    public static java.sql.ResultSet get(String numexp, Integer idnumero, Integer codcaract, java.sql.Connection conexion) throws java.lang.Exception
    {
       java.sql.ResultSet rsDatos = null;
       Utilidades.Consultas oConsulta = null;
       if(conexion!=null)
       {
            oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
            oConsulta.select("*");
            oConsulta.from(TABLA);
            oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);    
            oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);  
            oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);  
            
            oConsulta.orderBy("codcaract",Utilidades.Consultas.ASC);            
            rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
            oConsulta = null;
       }//if       
       return rsDatos;
    }//get por numexp, idnumero, codcaract  
        
//******************************************************************************
//******************************************************************************
   /**
   * Método que carga los datos de la base de datos en un ResultSet.
   * @param conexion Conexión a la base de datos.
   * @return rsDatos Estructura con los datos cargados.
   * @throws java.lang.Exception Excepción general capturada.
   */
    public static java.sql.ResultSet get(String numexp, Integer idnumero, java.sql.Connection conexion) throws java.lang.Exception
    {
       java.sql.ResultSet rsDatos = null;
       Utilidades.Consultas oConsulta = null;
       if(conexion!=null)
       {
            oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
            oConsulta.select("*");
            oConsulta.from(TABLA);
            oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);    
            oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);   
            oConsulta.orderBy("codcaract",Utilidades.Consultas.ASC);            
            rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
            oConsulta = null;
       }//if       
       return rsDatos;
    }//get por numexp, idnumero  
    
 //*****************************************************************************              
//******************************************************************************
    public static String getValor(Integer idnumero,Integer codcaract,java.sql.Connection conexion) throws java.lang.Exception
    {
       java.sql.ResultSet oResultSet = null;
       Utilidades.Consultas oConsulta = null;
       String oString = null;
       try
       {
           if(idnumero!=null && codcaract!=null && conexion!=null)
           {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("VALOR");
                oConsulta.from(TABLA);   
                oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);  
                oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);           
                oResultSet = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
                if(oResultSet!=null && oResultSet.next()) oString = oResultSet.getString("VALOR");
           }//if       
       }//try
       catch(Exception e)
       {
           throw e;
       }//catch
       finally
       {
           if(oResultSet!=null) oResultSet.close();
           oResultSet = null;
           oConsulta = null;
       }//finally
       return oString;
    }//getValor  
    
 //*****************************************************************************              
//******************************************************************************
   /**
   * Método que carga los datos de la base de datos en un ResultSet.
   * @param conexion Conexión a la base de datos.
   * @return rsDatos Estructura con los datos cargados.
   * @throws java.lang.Exception Excepción general capturada.
   */
    public static java.sql.ResultSet get(String numexp, java.sql.Connection conexion) throws java.lang.Exception
    {
       java.sql.ResultSet rsDatos = null;
       Utilidades.Consultas oConsulta = null;
       if(conexion!=null)
       {
            oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
            oConsulta.select("*");
            oConsulta.from(TABLA);
            oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);                
            oConsulta.orderBy("idnumero",Utilidades.Consultas.ASC);            
            rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
            oConsulta = null;
       }//if       
       return rsDatos;
    }//get por numexp       
    
//******************************************************************************
//******************************************************************************
   
  /**
     * * Método que borra los datos de la base de datos.
     * @param idproyecto Identificador del proyecto.
     * @param conexion Conexión a la base de datos.
     * @return Devuelve numeros de registros afectados.
     * @throws java.lang.Exception Excepción general capturada.
    */
    public static int delete(String numexp, Integer idnumero, Integer codcaract, java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && idnumero!=null && codcaract!=null)
        {
             oConsulta = new Utilidades.Consultas(Utilidades.Consultas.DELETE);
             oConsulta.from(TABLA);             
             oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);	 
             oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);	 
             oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);	
             iNumeroRegistrosAfectados = Utilidades.Conexion.delete(oConsulta.getSql(),conexion);
             oConsulta = null;
        }//if       
        return iNumeroRegistrosAfectados;
    }//delete    
    
//******************************************************************************
//******************************************************************************
   
  /**
     * * Método que borra los datos de la base de datos.
     * @param idproyecto Identificador del proyecto.
     * @param conexion Conexión a la base de datos.
     * @return Devuelve numeros de registros afectados.
     * @throws java.lang.Exception Excepción general capturada.
    */
    public static int delete(String numexp, Integer idnumero, java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && idnumero!=null)
        {
             oConsulta = new Utilidades.Consultas(Utilidades.Consultas.DELETE);
             oConsulta.from(TABLA);             
             oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);	 
             oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);	              
             iNumeroRegistrosAfectados = Utilidades.Conexion.delete(oConsulta.getSql(),conexion);
             oConsulta = null;
        }//if       
        return iNumeroRegistrosAfectados;
    }//delete        
    
//****************************************************************************** 
//******************************************************************************  
 /**
     * Método que devuelve si existe un registro en la tabla.
     * @param numexp Numero de expediente.
     * @param idnumero Identificador.
     * @param codcaract Identificador del campo.
     * @param conexion Conexión a la base de datos.
     * @return Devuelve verdadero si existe el registro y falso si no existe.
     * @throws java.lang.Exception Excepción general capturada.
     */
    public static boolean exists(String numexp, Integer idnumero, Integer codcaract, java.sql.Connection conexion) throws java.lang.Exception
    {
        boolean bDatos = false;
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsDatos = null;
        if(numexp!=null && !numexp.trim().equals("") && idnumero!=null && codcaract!=null && conexion!=null)
        {
            try
            {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("*");
                oConsulta.from(TABLA);
                oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);		
                oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);		
                oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);		
                rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
                bDatos = rsDatos.next();
            }//try
            catch(java.lang.Exception e)
            {
                throw e;
            }//catch
            finally
            {
                if(rsDatos!=null) rsDatos.close();
                rsDatos = null;
                oConsulta = null;
            }//finally
        }//if
        return bDatos;
    }//exists por numexp, idnumero, codcaract 
   
//****************************************************************************** 
//******************************************************************************  
 /**
     * Método que devuelve si existe un registro en la tabla.
     * @param numexp Numero de expediente.
     * @param idnumero Identificador datosreg.    
     * @param conexion Conexión a la base de datos.
     * @return Devuelve verdadero si existe el registro y falso si no existe.
     * @throws java.lang.Exception Excepción general capturada.
     */
    public static boolean exists(String numexp, Integer idnumero, java.sql.Connection conexion) throws java.lang.Exception
    {
        boolean bDatos = false;
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsDatos = null;
        if(numexp!=null && !numexp.trim().equals("") && idnumero!=null && conexion!=null)
        {
            try
            {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("*");
                oConsulta.from(TABLA);
                oConsulta.where("numexp",numexp,Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);		
                oConsulta.where("idnumero",Integer.toString(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);			
               	
                rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
                bDatos = rsDatos.next();
            }//try
            catch(java.lang.Exception e)
            {
                throw e;
            }//catch
            finally
            {
                if(rsDatos!=null) rsDatos.close();
                rsDatos = null;
                oConsulta = null;
            }//finally
        }//if
        return bDatos;
    }//exists por numexp, idnumero 
   
 //****************************************************************************** 
//******************************************************************************  
 /**
     * Método que devuelve si existe un registro en la tabla.
     * @param numexp Numero de expediente.
     * @param idnumero Identificador datosreg.    
     * @param conexion Conexión a la base de datos.
     * @return Devuelve verdadero si existe el registro y falso si no existe.
     * @throws java.lang.Exception Excepción general capturada.
     */
    public static boolean exists( Integer codcaract, java.sql.Connection conexion) throws java.lang.Exception
    {
        boolean bDatos = false;
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsDatos = null;
        if( codcaract!=null && conexion!=null)
        {
            try
            {
                oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
                oConsulta.select("codcaract");
                oConsulta.from(TABLA);               	
                oConsulta.where("codcaract",Integer.toString(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);			
               	
                rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
                bDatos = rsDatos.next();
            }//try
            catch(java.lang.Exception e)
            {
                throw e;
            }//catch
            finally
            {
                if(rsDatos!=null) rsDatos.close();
                rsDatos = null;
                oConsulta = null;
            }//finally
        }//if
        return bDatos;
    }//exists codcaract
         
//******************************************************************************   
//******************************************************************************   
  /**
     * M�todo que inserta un registro en la base de datos.
     * @param conexion Conexion a la base de datos.
     * @return Numeros de registros afectados
     * @throws java.lang.Exception 
     */
   public Utilidades.Resultado insert(java.sql.Connection conexion) throws java.lang.Exception
   {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null; 
        Utilidades.Resultado oResultado = null;
       
        if(conexion!=null)
        {
               oResultado = new Utilidades.Resultado();
              
               oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
               oConsulta.from(TABLA);                                            
               oConsulta.insert("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.VARCHAR);      
               oConsulta.insert("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.INT); 
               oConsulta.insert("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.INT); 
               oConsulta.insert("valor",Utilidades.Cadenas.getValorSinBlancos(valor),Utilidades.Consultas.VARCHAR);                              
               oConsulta.insert("tipo",Utilidades.Cadenas.getValorSinBlancos(tipo),Utilidades.Consultas.INT);  
               
               iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(),conexion);
               oResultado.setNumeroRegistrosAfectados(iNumeroRegistrosAfectados);
               oResultado.setIntegerResultado(codcaract);
               oResultado.setStringResultado(valor);
               oConsulta = null;
        }//if        
        return oResultado;
   }//insert sin request  
   
//******************************************************************************
//******************************************************************************  
   /**
   * Metodo que hace insert a los datos del objecto en la base de datos
   * @param request 
   * @param conexion Conexi�n a la base de datos.
   * @return numeros de registros afectados
   * @throws java.lang.Exception - Excepci�n general capturada.
   */
     public static Utilidades.Resultado insert(javax.servlet.http.HttpServletRequest request,java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        Utilidades.Resultado oResultado = null;
        
        if(conexion!=null && request!=null && Utilidades.Request.getString(request,"numexp")!=null && Utilidades.Request.getInteger(request,"idnumero")!=null && Utilidades.Request.getInteger(request,"codcaract")!=null )
        {
               oResultado = new Utilidades.Resultado();
             
               oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
               oConsulta.from(TABLA);                                                    
                                        
               oConsulta.insert("numexp",Utilidades.Cadenas.getValorSinBlancos(request,"numexp"),Utilidades.Consultas.VARCHAR);                                    
               oConsulta.insert("idnumero",Utilidades.Cadenas.getValorSinBlancos(request,"idnumero"),Utilidades.Consultas.INT); 
               oConsulta.insert("codcaract",Utilidades.Cadenas.getValorSinBlancos(request,"codcaract"),Utilidades.Consultas.INT);           
               oConsulta.insert("valor",Utilidades.Cadenas.getValorSinBlancos(request,"valor"),Utilidades.Consultas.VARCHAR);
               oConsulta.insert("tipo",Utilidades.Cadenas.getValorSinBlancos(request,"tipo"),Utilidades.Consultas.INT);       
               
               iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(),conexion);
               oResultado.setNumeroRegistrosAfectados(iNumeroRegistrosAfectados);
               oResultado.setIntegerResultado(Utilidades.Request.getInteger(request,"codcaract"));
               oResultado.setStringResultado(Utilidades.Request.getString(request,"valor"));
        }//if
        oConsulta = null;
        return oResultado;
     }//insert con request
       
//******************************************************************************
//******************************************************************************       
 /**
     * Método que actualiza un registro en la base de datos.
     * @param conexion Conexión a la base de datos.
     * @return Números de registros afectados
     * @throws java.lang.Exception 
     */
   public Utilidades.Resultado update(java.sql.Connection conexion) throws java.lang.Exception
   {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        Utilidades.Resultado oResultado = null;
        
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && idnumero!=null && codcaract!=null)
        {
               oResultado = new Utilidades.Resultado();
               oConsulta = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
               oConsulta.from(TABLA);               
               
               oConsulta.set("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.VARCHAR);              
               oConsulta.set("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.INT);
               oConsulta.set("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.INT);             
               oConsulta.set("valor",Utilidades.Cadenas.getValorSinBlancos(valor),Utilidades.Consultas.VARCHAR);
               oConsulta.set("tipo",Utilidades.Cadenas.getValorSinBlancos(tipo),Utilidades.Consultas.INT);                    
               
               oConsulta.where("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);  
               oConsulta.where("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);    
               oConsulta.where("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);        
               
               iNumeroRegistrosAfectados = Utilidades.Conexion.update(oConsulta.getSql(),conexion);
               oResultado.setNumeroRegistrosAfectados(iNumeroRegistrosAfectados);
               oResultado.setIntegerResultado(codcaract);
               oResultado.setStringResultado(valor);
               oConsulta = null;
        }//if        
        return oResultado;
   }//update sin request
   
//******************************************************************************
//******************************************************************************       
 /**
     * Método que actualiza un registro en la base de datos.
     * @param conexion Conexión a la base de datos.
     * @return Números de registros afectados
     * @throws java.lang.Exception 
     */
   public static int update(String numexp,Integer idnumero,Integer codcaract,String valor,Integer tipo, java.sql.Connection conexion) throws java.lang.Exception
   {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && idnumero!=null && codcaract!=null)
        {
           oConsulta = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
           oConsulta.from(TABLA);               
           oConsulta.set("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.VARCHAR);              
           oConsulta.set("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.INT);
           oConsulta.set("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.INT);             
           oConsulta.set("valor",Utilidades.Cadenas.getValorSinBlancos(valor),Utilidades.Consultas.VARCHAR);
           oConsulta.set("tipo",Utilidades.Cadenas.getValorSinBlancos(tipo),Utilidades.Consultas.INT); 
           
           oConsulta.where("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);  
           oConsulta.where("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.AND,Utilidades.Consultas.INT);    
           oConsulta.where("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.AND,Utilidades.Consultas.INT);        
           iNumeroRegistrosAfectados = Utilidades.Conexion.update(oConsulta.getSql(),conexion);
           oConsulta = null;
        }//if        
        return iNumeroRegistrosAfectados;
   }//update
   
//******************************************************************************
//******************************************************************************  
   public static int insert(String numexp,Integer idnumero,Integer codcaract,String valor, Integer tipo, java.sql.Connection conexion) throws java.lang.Exception
   {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && idnumero!=null && codcaract!=null)
        {
           oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
           oConsulta.from(TABLA);               
           oConsulta.insert("numexp",Utilidades.Cadenas.getValorSinBlancos(numexp),Utilidades.Consultas.VARCHAR);              
           oConsulta.insert("idnumero",Utilidades.Cadenas.getValorSinBlancos(idnumero),Utilidades.Consultas.INT);
           oConsulta.insert("codcaract",Utilidades.Cadenas.getValorSinBlancos(codcaract),Utilidades.Consultas.INT);             
           oConsulta.insert("valor",Utilidades.Cadenas.getValorSinBlancos(valor),Utilidades.Consultas.VARCHAR);
           oConsulta.insert("tipo",Utilidades.Cadenas.getValorSinBlancos(tipo),Utilidades.Consultas.INT);
           
           iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(),conexion);
           oConsulta = null;
        }//if        
        return iNumeroRegistrosAfectados;
   }//update
   
//******************************************************************************
//******************************************************************************  
/**
   * Metodo que hace update a los datos del objecto en la base de datos
   * @param request 
   * @param conexion Conexión a la base de datos.
   * @return numeros de registros afectados
   * @throws java.lang.Exception - Excepciòn general capturada.
   */
    public static Utilidades.Resultado update(javax.servlet.http.HttpServletRequest request,java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        Utilidades.Consultas oConsulta = null;
        Utilidades.Resultado oResultado = null;
        
        if(conexion!=null && request!=null && Utilidades.Request.getString(request,"numexp")!=null && Utilidades.Request.getInteger(request,"idnumero")!=null && Utilidades.Request.getInteger(request,"codcaract")!=null)
        {
               oResultado = new Utilidades.Resultado();
               oConsulta = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
               oConsulta.from(TABLA);               
             
               oConsulta.set("numexp",Utilidades.Cadenas.getValorSinBlancos(request,"numexp"),Utilidades.Consultas.VARCHAR);             
               oConsulta.set("idnumero",Utilidades.Cadenas.getValorSinBlancos(request,"idnumero"),Utilidades.Consultas.INT);
               oConsulta.set("codcaract",Utilidades.Cadenas.getValorSinBlancos(request,"codcaract"),Utilidades.Consultas.INT);                            
               oConsulta.set("valor",Utilidades.Cadenas.getValorSinBlancos(request,"valor"),Utilidades.Consultas.VARCHAR);
               oConsulta.set("tipo",Utilidades.Cadenas.getValorSinBlancos(request,"tipo"),Utilidades.Consultas.INT);                            
               
               oConsulta.where("numexp",Utilidades.Cadenas.getValorSinBlancos(request,"numexp"),Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);
               oConsulta.where("idnumero",Utilidades.Cadenas.getValorSinBlancos(request,"idnumero"),Utilidades.Consultas.AND,Utilidades.Consultas.INT);
               oConsulta.where("codcaract",Utilidades.Cadenas.getValorSinBlancos(request,"codcaract"),Utilidades.Consultas.AND,Utilidades.Consultas.INT);	      
               iNumeroRegistrosAfectados = Utilidades.Conexion.update(oConsulta.getSql(),conexion);
               
               oResultado.setNumeroRegistrosAfectados(iNumeroRegistrosAfectados);
               oResultado.setIntegerResultado(Utilidades.Request.getInteger(request, "codcaract"));
               oResultado.setStringResultado(Utilidades.Request.getString(request, "valor"));
        }//if
        oConsulta = null;
        return oResultado;
     }//update con request   
   
//******************************************************************************
//******************************************************************************   
 /**
      * Metodo que encuentra  los datos del objecto en la base de datos
     * @param request Objeto Request con los campos a insertar en la tabla.
     * @param conexion Conexión a la base de datos.
     * @return ResultSet con los registros encontrados
     * @throws java.lang.Exception 
     */
    public static java.sql.ResultSet find(javax.servlet.http.HttpServletRequest request,java.sql.Connection conexion) throws java.lang.Exception
    {
        java.sql.ResultSet rsDatos = null;
        Utilidades.Consultas oConsulta = null;
        if(request!=null && conexion!=null)
        {
             oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
             oConsulta.select("*");
             oConsulta.from(TABLA);             
             if(!Utilidades.Cadenas.getStringAttribute(request,"numexp").equals("")) oConsulta.where("numexp",Utilidades.Cadenas.getValorSinBlancos(request,"numexp"),Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);
             if(!Utilidades.Cadenas.getStringAttribute(request,"idnumero").equals("")) oConsulta.where("idnumero",Utilidades.Cadenas.getValorSinBlancos(request,"idnumero"),Utilidades.Consultas.AND,Utilidades.Consultas.INT);
             if(!Utilidades.Cadenas.getStringAttribute(request,"codcaract").equals("")) oConsulta.where("codcaract",Utilidades.Cadenas.getValorSinBlancos(request,"codcaract"),Utilidades.Consultas.AND,Utilidades.Consultas.INT);
             if(!Utilidades.Cadenas.getStringAttribute(request,"valor").equals("")) oConsulta.where("valor",Utilidades.Cadenas.getValorSinBlancos(request,"valor"),Utilidades.Consultas.AND,Utilidades.Consultas.VARCHAR);
             if(!Utilidades.Cadenas.getStringAttribute(request,"tipo").equals("")) oConsulta.where("tipo",Utilidades.Cadenas.getValorSinBlancos(request,"tipo"),Utilidades.Consultas.AND,Utilidades.Consultas.INT);
                  
             rsDatos = Utilidades.Conexion.select(oConsulta.getSql(), conexion);
             oConsulta = null;
        }//if
        
        return rsDatos;
    }//find request 
 
//******************************************************************************
//******************************************************************************
    /**
     * Imprime en el flujo de salida los atributos del objeto con formato xml.
     * @param out Flujo de salida.
     */
    public void getDatosXML(java.io.PrintWriter out)
    {
        if(out!=null)
        {
            out.write("<Exptcaract>");
            out.write("<numexp>"+Utilidades.Cadenas.getValorMostrarWeb(numexp)+"</numexp>");
            out.write("<idnumero>"+Utilidades.Cadenas.getValorMostrarWeb(idnumero)+"</idnumero>");           
            out.write("<codcaract>"+Utilidades.Cadenas.getValorMostrarWeb(codcaract)+"</codcaract>");
            out.write("<valor>"+Utilidades.Cadenas.getValorMostrarWeb(valor)+"</valor>"); 
            out.write("<tipo>"+Utilidades.Cadenas.getValorMostrarWeb(tipo)+"</tipo>"); 
            out.write("</Exptcaract>");
        }//if
    }//getDatosXML
    
  //*********************************************************************************************************************************
  //*********************************************************************************************************************************
    public static int save(String numexp,Integer idnumero,Integer codcaract, String valor, Integer tipo, java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        if(conexion!=null && numexp!=null && !numexp.trim().equals("") && codcaract!=null && idnumero!=null && tipo!=null)
        {      
            if(Objetos.unidades.Exptcaract.exists(numexp,idnumero,codcaract,conexion)) iNumeroRegistrosAfectados = Objetos.unidades.Exptcaract.update(numexp,idnumero,codcaract,valor,tipo,conexion);
            else iNumeroRegistrosAfectados = Objetos.unidades.Exptcaract.insert(numexp,idnumero,codcaract,valor,tipo,conexion);
        }//if
        return iNumeroRegistrosAfectados;
    }//save
       
  //*********************************************************************************************************************************
  //*********************************************************************************************************************************
    public static boolean copy_data_from_expediente_to_exptcaract(String numexp,Integer idnumero,java.sql.Connection conexion) throws java.lang.Exception
    {
        boolean bCopy = false;
        int iNumeroRegistrosAfectados = 0;
        String valor_cli = null;
        Integer codcli = null; 
        Integer codcaract = null;
        Integer valor = 0;
        String tipoinmElmto = null;
        Objetos.v2.Solicitudes oSolicitudes = null;
        Objetos.Elemento1 oElemento1 = null;
        Objetos.Elemento2 oElemento2 = null;
        Objetos.Elemento4 oElemento4 = null;
        Objetos.Elemento3 oElemento3 = null;
        Objetos.Elemento5 oElemento5 = null;
        Objetos.Elemento6 oElemento6 = null;
        Objetos.Elemento8 oElemento8 = null;
        Objetos.v2.Elemento9 oElemento9 = null;
        Objetos.Sancion oSancion = null;
        Objetos.Sancioncons oSancioncons = null;
        Objetos.v2.Otrastas oOtrastas = null;
        Objetos.Datosreg oDatosreg = null;
        Objetos.Catastro oCatastro = null;
        Objetos.unidades.Elementos oElementos = null;
        Objetos.Documenta oDocumenta = null;
        String SqlGrupoCodcli = null;
        java.sql.ResultSet rsGrupoCodcli = null;
        //OJO tipo=3 ELEMENTOS; 2-EDIFICIOS; 1-SUELO;
        try
        {
             if(numexp!=null && idnumero!=null && !numexp.trim().equals("") && conexion!=null)
             {
                 oSolicitudes = new Objetos.v2.Solicitudes();
                 oElementos = new Objetos.unidades.Elementos();
                 if(oSolicitudes.load(numexp, conexion)==1 && oElementos.load(idnumero, conexion))
                 {                                              
                      //**************                                  
                      SqlGrupoCodcli = "SELECT CODGRUPO" 
                                     + " FROM CLIENTES"
                                     + " WHERE clientes.codcli='"+oSolicitudes.codcli+"'" ;                            
                                                       
                      rsGrupoCodcli = Utilidades.Conexion.select(SqlGrupoCodcli,conexion);     
                      if (rsGrupoCodcli!=null && rsGrupoCodcli.next()) codcli = rsGrupoCodcli.getInt("CODGRUPO");                                                      
                      //**************                                                                                    
                      // - DOCUMENTA ------------------------------------------------
                     oDocumenta = new Objetos.Documenta();
                     if(oDocumenta.load(numexp,conexion) == 1)
                     {
                        if(oDocumenta.vivvpo!=null && !oDocumenta.vivvpo.trim().equals(""))
                         {
                             codcaract = 155; //VPO
                             valor_cli = Objetos.unidades.Convcaract.getValor_cli(codcli,codcaract,oDocumenta.vivvpo,conexion);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             codcaract = 305;
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                             
                         }//if  
                         if(oDocumenta.cedhabit!=null && !oDocumenta.cedhabit.trim().equals(""))
                         {
                             codcaract = 167; //Cedula de habitabilidad
                             if (oDocumenta.cedhabit.equals("0"))valor_cli = "2";
                             else valor_cli = "1";
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if 
                                                  
                     }
                     
                     // - ELEMENTO1------------------------------------------------
                     oElemento1 = new Objetos.Elemento1();
                     if(oElemento1.load(numexp,conexion))
                     {                                                                       
                         if(oElemento1.numascens!=null && !oElemento1.numascens.trim().equals(""))
                         {
                             codcaract = 9;   //Ascensor 
                             if (Integer.parseInt(oElemento1.numascens) > 0) valor_cli = "1";
                             else valor_cli = "2";
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                             codcaract = 192; //nr de ascensores
                             valor_cli = oElemento1.numascens;
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         
                         codcaract = 18;   //Edificio                                               
                         if(oElemento1.usoedif!=null && !oElemento1.usoedif.trim().equals(""))
                         {
                             if (Integer.parseInt(oElemento1.usoedif) == 1) valor_cli = "1";
                             else if (Integer.parseInt(oElemento1.usoedif) == 3) valor_cli = "2";
                             else valor_cli = "";
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         
                         
                         if(oElemento1.usoedif!=null && !oElemento1.usoedif.trim().equals(""))
                         {
                             codcaract = 60; //Usos
                             valor_cli = Objetos.unidades.Convcaract.getValor_cli(codcli,codcaract,oElemento1.usoedif,conexion);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if      
                         
                         if(oElemento1.pltsbrras!=null && !oElemento1.pltsbrras.trim().equals(""))
                         {
                             codcaract = 180; //Plantas sobre rasante
                             valor_cli = oElemento1.pltsbrras.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if  
                         
                         if(oElemento1.pltbjoras!=null && !oElemento1.pltbjoras.trim().equals(""))
                         {
                             codcaract = 181; //Usos
                             valor_cli = oElemento1.pltbjoras.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if   
                         
                         
                         
                     }//if ELEMENTO1
                     
                      //DATOSREG
                     oDatosreg = new Objetos.Datosreg();
                     if (oDatosreg.load(numexp, oElementos.num_dr, conexion))
                     {
                         codcaract = 58;    //tipo de vivienda
                         if (oDatosreg.tipoinm.substring(0, 1).equals("V"))
                         {//viviendas
                             if (oDatosreg.tipoinm.substring(0, 2).equals("VP")) valor_cli = "4";
                             else if (oElemento1.tipoedif!=null && !oElemento1.tipoedif.equals(""))
                             {
                                 if (oElemento1.tipoedif.equals("1")) valor_cli = "1";
                                 else if (oElemento1.tipoedif.equals("3")) valor_cli = "2";
                                 else if (oElemento1.tipoedif.equals("2")) valor_cli = "3";  
                                 else valor_cli = "1";
                             }
                             else valor_cli = "1";
                         }
                         else if (oDatosreg.tipoinm.substring(0, 1).equals("L"))
                         {//locales
                             valor_cli = "13";
                         }
                         else if (oDatosreg.tipoinm.equals("FCR"))
                         {
                             valor_cli = "16";
                         }
                         else valor_cli = null;
                         
                         if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                         valor_cli = null;      
                         
                         if (oDatosreg.supregsuelo!=null && !oDatosreg.supregsuelo.equals(""))
                         {
                            codcaract = 91;//Superficie Solar
                            valor_cli = oDatosreg.supregsuelo.toString().replace(".", ",");
                            if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                            valor_cli = null;
                         }
                         
                         if (oDatosreg.supregutil != null && !oDatosreg.supregutil.equals(""))
                         {
                            codcaract = 179;//Sup. Util
                            valor_cli = oDatosreg.supregutil.toString().replace(".", ",");
                            if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                            valor_cli = null;
                         }
                         
                         codcaract = 183; //Superficie registral
                         if (oDatosreg.supregccom!= null && !oDatosreg.supregccom.equals("") && oDatosreg.supregccom > 0 ) valor_cli = oDatosreg.supregccom.toString();
                         else if (oDatosreg.supregcons!= null && !oDatosreg.supregcons.equals("") && oDatosreg.supregcons > 0) valor_cli = oDatosreg.supregcons.toString();
                         else if (oDatosreg.supregsdef!= null && !oDatosreg.supregsdef.equals("") && oDatosreg.supregsdef > 0) valor_cli = oDatosreg.supregsdef.toString();
                         else if (oDatosreg.supregutil!= null && !oDatosreg.supregutil.equals("") && oDatosreg.supregutil > 0) valor_cli = oDatosreg.supregutil.toString();
                         else valor_cli = null;                                                                           
                         if(valor_cli!=null && !valor_cli.trim().equals(""))
                         {
                             valor_cli = valor_cli.replace(".", ",");
                             Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                         }
                         valor_cli = null;
                         oCatastro = new Objetos.Catastro();
                         if (oCatastro.load(numexp, oElementos.num_dr, conexion))
                         {
                            codcaract = 307; //superficie catastral
                            if (oCatastro.scatascom!= null && oCatastro.scatascom > 0) valor_cli = oCatastro.scatascom.toString();
                            else if (oCatastro.scatasvlo!= null && oCatastro.scatasvlo > 0) valor_cli = oCatastro.scatasvlo.toString();
                            else if (oCatastro.scatastral!= null && oCatastro.scatastral > 0) valor_cli = oCatastro.scatastral.toString();
                            else if (oCatastro.scatasslo!= null && oCatastro.scatasslo > 0) valor_cli = oCatastro.scatasslo.toString();
                            else valor_cli = null;                                                                           
                            if(valor_cli!=null && !valor_cli.trim().equals("")) 
                            {
                                valor_cli = valor_cli.replace(".", ",");
                                Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                                
                            }
                            valor_cli = null;
                         }
                        
                         
                     }//DATOSREG
                     // - ELEMENTO2------------------------------------------------
                     oElemento2 = new Objetos.Elemento2();
                     if(oElemento2.load(numexp,conexion))
                     {
                         if(oElemento2.alturalibre!=null && !oElemento2.alturalibre.trim().equals(""))
                         {
                             codcaract = 3; //Altura Libre
                             valor_cli = oElemento2.alturalibre;
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             valor_cli = null;
                         }//if
                         if(oElemento2.numplta!=null)
                         {
                             codcaract = 4; //Altura piso sobre rasante
                             valor_cli = oElemento2.numplta.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {                                 
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             valor_cli = null;
                         }//if
                         if(oElemento2.salasext!=null)
                         {
                             codcaract = 24; //Situacion
                             if (oElemento2.salasext == 0) valor_cli = "1";
                             else valor_cli = "2";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         if(oElemento2.jardin!=null)
                         {
                             codcaract = 25; // Jardin
                             if (oElemento2.jardin.equals("0")) valor_cli = "2";
                             else valor_cli = "1";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         if(oElemento2.longfachada!=null && !oElemento2.longfachada.equals(""))
                         {
                             codcaract = 26; // Metros Ancho                            
                             valor_cli = oElemento2.longfachada;                          
                             if(valor_cli!=null && !valor_cli.trim().equals(""))
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             codcaract = 29; //Metros Fachada
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             
                             valor_cli = null;
                         }//if
                         if(oElemento2.fondo!=null && !oElemento2.fondo.equals(""))
                         {
                             codcaract = 30; // Metros Largo
                             valor_cli = oElemento2.fondo;                          
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             }
                             codcaract = 86;    //Metros Fondo
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             }
                             valor_cli = null;
                         }//if
                         if(oElemento2.montelmto!=null && !oElemento2.montelmto.equals(""))
                         {
                             codcaract = 31; // Montacargas
                             valor_cli = oElemento2.montelmto;                          
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                         codcaract = 34;    //Ba�os
                         if (oElemento2.banosext!=null && !oElemento2.banosext.equals("")) valor += oElemento2.banosext;
                         if (oElemento2.banosint!=null && !oElemento2.banosint.equals("")) valor += oElemento2.banosint;
                         if (oElemento2.banosmzn!=null && !oElemento2.banosmzn.equals("")) valor += oElemento2.banosmzn;
                         if (oElemento2.banospat!=null && !oElemento2.banospat.equals("")) valor += oElemento2.banospat;
                         if (oElemento2.aseosext!=null && !oElemento2.aseosext.equals("")) valor += oElemento2.aseosext;
                         if (oElemento2.aseosint!=null && !oElemento2.aseosint.equals("")) valor += oElemento2.aseosint;
                         if (oElemento2.aseosmzn!=null && !oElemento2.aseosmzn.equals("")) valor += oElemento2.aseosmzn;
                         if (oElemento2.aseospat!=null && !oElemento2.aseospat.equals("")) valor += oElemento2.aseospat;
                         valor_cli = Integer.toString(valor);
                         if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                         valor_cli = null;
                         valor = 0;
                         
                         if(oElemento2.plantas!=null && !oElemento2.plantas.equals(""))
                         {
                             codcaract = 36; // N� de plantas del local
                             valor_cli = oElemento2.plantas.toString();                 
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                         
                         codcaract = 38;    //Habitaciones
                         if (oElemento2.estanext!=null && !oElemento2.estanext.equals("")) valor += oElemento2.estanext;
                         if (oElemento2.estanint!=null && !oElemento2.estanint.equals("")) valor += oElemento2.estanint;
                         if (oElemento2.estanmzn!=null && !oElemento2.estanmzn.equals("")) valor += oElemento2.estanmzn;
                         if (oElemento2.estanpat!=null && !oElemento2.estanpat.equals("")) valor += oElemento2.estanpat;
                         if (oElemento2.salasext!=null && !oElemento2.salasext.equals("")) valor += oElemento2.salasext;
                         if (oElemento2.salasint!=null && !oElemento2.salasint.equals("")) valor += oElemento2.salasint;
                         if (oElemento2.salasmzn!=null && !oElemento2.salasmzn.equals("")) valor += oElemento2.salasmzn;
                         if (oElemento2.salaspat!=null && !oElemento2.salaspat.equals("")) valor += oElemento2.salaspat;
                         valor_cli = Integer.toString(valor);
                         if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                         valor_cli = null;
                         valor = 0;
                         
                         if(oElemento2.piscina!=null && !oElemento2.piscina.equals(""))
                         {
                             codcaract = 42; // Piscina
                             if (oElemento2.piscina.equals("0")) valor_cli = "2";
                             else valor_cli = "1";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                         
                         if(oElemento2.terrazas!=null && !oElemento2.terrazas.equals(""))
                         {
                             codcaract = 52; // Terraza
                             if (oElemento2.terrazas.equals("0")) valor_cli = "2";
                             else valor_cli = "1";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                         
                         if(oElemento2.polivalenc!=null && !oElemento2.polivalenc.equals(""))
                         {
                             codcaract = 620; // Uso Polivalente                             
                             valor_cli = Objetos.unidades.Convcaract.getValor_cli(codcli,codcaract,oElemento2.polivalenc,conexion);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                         
                     }//if ELEMENTO2
                     
                      // - ELEMENTO3 ------------------------------------------------
                     oElemento3 = new Objetos.Elemento3();
                     if(oElemento3.load(numexp,conexion))
                     {
                         if(oElemento3.pzagarage!=null && !oElemento3.pzagarage.equals(""))
                         {
                             codcaract = 150;    //Parking
                             if (oElemento3.pzagarage.equals("0")) valor_cli = "2";
                             else valor_cli = "1";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         
                         if(oElemento3.trastero!=null && !oElemento3.trastero.equals(""))
                         {
                             codcaract = 151;    //Trastero
                             if (oElemento3.trastero.equals("0")) valor_cli = "2";
                             else valor_cli = "1";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                                                  
                     }
                     
                     // - ELEMENTO4 ------------------------------------------------
                     oElemento4 = new Objetos.Elemento4();
                     if(oElemento4.load(numexp,conexion))
                     {
                         if(oElemento4.aireacond!=null && !oElemento4.aireacond.trim().equals(""))
                         {
                             codcaract = 1; //Aire acondicionado
                             valor_cli = Objetos.unidades.Convcaract.getValor_cli(codcli,codcaract,oElemento4.aireacond,conexion);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         if(oElemento4.antigelmto!=null && oSolicitudes.fchvis!=null)
                         {
                             codcaract = 6; //A�o de construccion
                             valor_cli = Integer.toString(Utilidades.Date.getAnyo(oSolicitudes.fchvis)-oElemento4.antigelmto);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                             
                         }//if
                         if(oElemento4.vidaelmto!=null && oElemento4.vidaelmto.intValue()>0 && oSolicitudes.fchvis!=null && oElemento4.vidauttot!=null)
                         {
                             codcaract = 8; //A�o de la ultima reforma
                             valor_cli = Integer.toString(Utilidades.Date.getAnyo(oSolicitudes.fchvis)-oElemento4.vidauttot);
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         if(oElemento4.calefaccion!=null && !oElemento4.calefaccion.equals(""))
                         {
                             codcaract = 10;    //Calefaccion
                             if (Integer.parseInt(oElemento4.calefaccion) > 0) valor_cli = "1";
                             else valor_cli = "2";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         codcaract = 19; //Estado de Conservacion
                         if(oElemento4.necerefelmt!=null && !oElemento4.necerefelmt.trim().equals("") && !oElemento4.necerefelmt.trim().equals("0")) valor_cli = "2";
                         else
                         {           
                             if(oElemento4.conserelmto!=null && !oElemento4.conserelmto.trim().equals(""))
                             {
                                 if (oElemento4.conserelmto.trim().equals("1") || oElemento4.conserelmto.trim().equals("2")) valor_cli = "1";
                                 else if (oElemento4.conserelmto.trim().equals("3")) valor_cli = "4";                                                                  
                             }                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         if(oElemento4.combustcalef!=null && !oElemento4.combustcalef.equals(""))
                         {
                             codcaract = 20;    //Gas Ciudad
                             if (oElemento4.combustcalef.equals("1")) valor_cli = "1";
                             else valor_cli = "2";                             
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             valor_cli = null;
                         }//if
                         
                     }//if ELEMENTO4
                     
                     // - ELEMENTO5 ------------------------------------------------
                     oElemento5 = new Objetos.Elemento5();
                     if(oElemento5.load(numexp,conexion))
                     {
                         if(oElemento5.cconparcom!=null && !oElemento5.cconparcom.equals(""))
                         {
                             codcaract = 27;    //Metros edificables
                             valor_cli = oElemento5.cconparcom.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             codcaract = 61;    //Superficie construida
                             if(valor_cli!=null && !valor_cli.trim().equals("")) 
                             {
                                 valor_cli = valor_cli.replace(".", ",");
                                 Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);
                             }
                             valor_cli = null;
                         }//if
                     }
                           
                     // - ELEMENTO6 ------------------------------------------------
                     oElemento6 = new Objetos.Elemento6();
                     if(oElemento6.load(numexp,conexion))
                     {
                         if(oElemento6.numprotecc!=null && !oElemento6.numprotecc.equals(""))
                         {
                             codcaract = 156;    //n� expediente VPO
                             valor_cli = oElemento6.numprotecc;
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                     }
                     
                     // - ELEMENTO8 y OTRASTAS  -------------------------------------
                     oElemento8 = new Objetos.Elemento8();
                     if(oElemento8.load(numexp,conexion))
                     {
                         if(oElemento8.porcenobra!=null && !oElemento8.porcenobra.equals(""))
                         {
                             codcaract = 188;    //n� expediente VPO
                             valor_cli = oElemento8.porcenobra.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                         }//if
                     }
                     else
                     {
                        oOtrastas = new Objetos.v2.Otrastas();
                        if(oOtrastas.load(numexp,conexion))
                        {
                            if(oOtrastas.totporcen!=null && !oOtrastas.totporcen.equals(""))
                            {
                             codcaract = 188;    //n� expediente VPO
                             valor_cli = oOtrastas.totporcen.toString();
                             if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                             valor_cli = null;
                            }//if
                        }
                     }
                     
                     if (oSolicitudes.equis !=null &&!oSolicitudes.equis.equals(""))
                     {
                            codcaract = 205;    //n� expediente VPO
                            valor_cli = oSolicitudes.equis.toString();
                            if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                            valor_cli = null;                         
                     }
                     if (oSolicitudes.ies !=null &&!oSolicitudes.ies.equals(""))
                     {
                            codcaract = 206;    //n� expediente VPO
                            valor_cli = oSolicitudes.ies.toString();
                            if(valor_cli!=null && !valor_cli.trim().equals("")) Objetos.unidades.Exptcaract.save(numexp,idnumero,codcaract,valor_cli,3,conexion);                             
                            valor_cli = null;                         
                     }
                     
                     bCopy = true;
                 }//if carga solicitudes
                 else bCopy = false;  //no carga el expediente
             }//if
             else bCopy = false;    //if(numexp!=null && idnumero!=null && !numexp.trim().equals("") && conexion!=null)
        }//try
        catch(Exception e)
        {
            bCopy = false;
            throw e;
        }//catch
        finally
        {
            valor_cli = null;
            oSancion = null;
            oSancioncons = null;
            oOtrastas = null;
            oElemento1 = null;
            oElemento2 = null;
            oElemento4 = null;
            oElemento5 = null;
            oElemento6 = null;
            oElemento8 = null;
            oElemento9 = null;
            oDatosreg = null;
            oElementos = null;
            oDocumenta = null;
            codcli = null;
            SqlGrupoCodcli = null;
            if (rsGrupoCodcli!=null)
            {
                rsGrupoCodcli.close();
                rsGrupoCodcli = null;
            }//if
            
            return bCopy;
        }//finally
       
    }//copy_data_from_expediente_to_exptcaract

    //******************************************************************************    
    //******************************************************************************
    public static int changeNumexp(String numexp_origen, String numexp_destino, java.sql.Connection conexion) throws java.lang.Exception
    {
        int iNumeroFilasAfectadas = 0;
        if(numexp_origen!=null && numexp_destino!=null && !numexp_origen.trim().equals("") && !numexp_destino.trim().equals("") && conexion!=null)
        {
            Utilidades.Consultas oConsulta = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
            oConsulta.from(TABLA);
            oConsulta.set("numexp",numexp_destino.trim().toUpperCase(),Utilidades.Consultas.VARCHAR);
            oConsulta.where("numexp",numexp_origen.trim().toUpperCase(),Utilidades.Consultas.VARCHAR);
            iNumeroFilasAfectadas = Utilidades.Conexion.update(oConsulta.getSql(),conexion);
            oConsulta = null;
        }//if
        return iNumeroFilasAfectadas;
    }//changeNumexp    
      
    //****************************************************************************************************
    //****************************************************************************************************
    public int copyAllToAnotherID_OLD(String numexp,Integer id_in,Integer id_out,java.sql.Connection connection) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        java.sql.ResultSet oResultSet = null;
        Utilidades.Resultado oResultado = null;
        Objetos.unidades.Exptcaract oExptcaract = null;
        try
        {
            if(id_in!=null && id_out!=null && connection!=null)
            {
                oResultSet = Objetos.unidades.Exptcaract.get(numexp,id_in,connection);
                while(oResultSet!=null && oResultSet.next())
                {
                    oExptcaract = new Objetos.unidades.Exptcaract();
                    if(oExptcaract.load(oExptcaract.numexp,oExptcaract.idnumero,oExptcaract.codcaract,connection))
                    {
                        oExptcaract.idnumero = id_out;
                        oResultado = oExptcaract.insert(connection);
                        if(oResultado!=null && oResultado.getNumeroRegistrosAfectados()!=null && oResultado.getNumeroRegistrosAfectados().intValue()==1) 
                            iNumeroRegistrosAfectados++;
                    }//if
                    oExptcaract = null;
                }//while
            }//if
        }//try
        catch(Exception oException)
        {
            throw oException;
        }//catch
        finally
        {
            if(oResultSet!=null) oResultSet.close();
            oResultSet = null;
            oExptcaract = null;
            oResultado = null;
        }//finally
        return iNumeroRegistrosAfectados;
    }//copyAllToAnotherID
    
    //****************************************************************************************************
    //****************************************************************************************************
  /**
  * Método estático de acceso a la clase.
  * @param args Array de cadenas como parámetros de entrada.
  */
    public static void main(String[] args)
    {
        Objetos.unidades.Exptcaract oExptcaract = new Objetos.unidades.Exptcaract();
    }//main

//******************************************************************************    
//******************************************************************************  
  
    
}//class Expcaract
