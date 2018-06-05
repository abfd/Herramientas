/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

/**
 *
 * @author Administrador
 */
public class Pretasaciones 
{
    //***********************************************************************************************************
    //***********************************************************************************************************
    public Integer numpet = null;
    public String numpreta = null;
    public Integer codcli = null;
    public Integer coddel = null;
    public String oficina = null;
    public Integer numexpc = null;
    public String ag_obj = null;
    public String objeto = null;
    public String num_tas = null;
    public String naturbien = null;
    public String ubica = null;
    public String acabslo = null;
    public String acabpd = null;
    public String carpext = null;
    public Integer planta = null;
    public Integer antig = null;
    public Integer ndormit = null;
    public Integer nbanos = null;
    public String ascens = null;
    public String calefacc = null;
    public String estcons = null;
    public String calcons = null;
    public Double suputil = null;
    public Double supcons = null;
    public Double supparc = null;
    public String tipopretasa = null;
    public String tipologia = null;
    public String esvpo = null;
    public String exptevpo = null;
    public java.util.Date fchvpo = null;
    public String autopromocion = null;
    public String finca = null;
    public Integer registro = null;
    public String localireg = null;
    public String titulareg = null;
    public String fcatastral = null;
    public String vivnueva = null;
    public Integer plantassras = null;
    public Integer sotanos = null;
    public Integer nplantas = null;
    public String reforbloq = null;
    public java.util.Date fchreforbloq = null;
    public String ite = null;
    public java.util.Date fchite = null;
    public String reforviv = null;
    public java.util.Date fchreforviv = null;
    public String caliacabados = null;
    public String aireacond = null;
    public String telefonia = null;
    public String otrasinstal = null;
    public String anxpiscina = null;
    public String anxgaraje = null;
    public String anxtrastero = null;
    public String anxporche = null;
    public String anxotro = null;
    public String piscinaurb = null;
    public String jardinesurb = null;
    public String otrosurb = null;
    public Integer constanio = null;
    public String comentc = null;
    public Double valtotpretaviv = null;
    public Double valunitpretaviv = null;
    public Double valtotpretatrt = null;
    public Double valunitpretatrt = null;
    public Double valtotpretapz = null;
    public Double valunitpretapz = null;
    public Double valtotrentviv = null;
    public Double valunitrentviv = null;
    public Double valtotrentrt = null;
    public Double valunitrentrt = null;
    public Double valtotrentpz = null;
    public Double valunitrentpz = null;
    public Double valorpretasa = null;
    public String estado = null;
    public java.util.Date fchuci = null;
    public String horauci = null;
    public java.util.Date fchval = null;
    public String horaval = null;
    public Integer postalv = null;
    public String provinv = null;
    public String municiv = null;
    public String localiv = null;
    public String codsituv = null;
    public String situacionv = null;
    public String tipoviav = null;
    public String callev = null;
    public String numerov = null;
    public String escalerav = null;
    public String plantav = null;
    public String puertav = null;
    public Double vventar = null;
    public String otratas = null;
    public String fiabilidad = null;
    public String numexpv = null;
 
   
    //***********************************************************************************************************
    //***********************************************************************************************************
    public  int setPretacionesFromB23xt(java.sql.Connection conexion) throws java.sql.SQLException,Exception
    {
        int iNumeroRegistrosAfectados = 0;
        int iContadorTotal = 0;
        Utilidades.Consultas oConsulta = null;
        String seq_preta = null;
        
        
            oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
            oConsulta.from("PRETASACIONES");
            oConsulta.insert("coddel",Integer.toString(coddel),Utilidades.Consultas.INT); 
            seq_preta = Integer.toString(Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion));
            oConsulta.insert("numpet",seq_preta,Utilidades.Consultas.INT);
            oConsulta.insert("numpreta",Integer.toString(codcli).trim()+"."+seq_preta.trim()+"-12",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("codcli",Integer.toString(codcli),Utilidades.Consultas.INT);            
            oConsulta.insert("oficina",oficina,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("numexpc",Integer.toString(numexpc),Utilidades.Consultas.INT);
            oConsulta.insert("ag_obj",ag_obj,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("objeto",objeto,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("num_tas",num_tas,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("naturbien",naturbien,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("ubica",ubica,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("acabslo",acabslo,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("acabpd",acabpd,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("carpext",carpext,Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("planta",Integer.toString(planta),Utilidades.Consultas.INT);
            //oConsulta.insert("antig",Integer.toString(antig),Utilidades.Consultas.INT);
            //oConsulta.insert("ndormit",Integer.toString(ndormit),Utilidades.Consultas.INT);
            //oConsulta.insert("nbanos",Integer.toString(nbanos),Utilidades.Consultas.INT);
            oConsulta.insert("ascens",ascens,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("calefacc",calefacc,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("estcons",estcons,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("calcons",calcons,Utilidades.Consultas.VARCHAR);
            if (suputil != null) oConsulta.insert("suputil",Utilidades.Cadenas.getValorDecimalBBDD(Double.toString(suputil)),Utilidades.Consultas.INT);
            if (supcons != null) oConsulta.insert("supcons",Utilidades.Cadenas.getValorDecimalBBDD(Double.toString(supcons)),Utilidades.Consultas.INT);
            //oConsulta.insert("supparc",Double.toString(supparc),Utilidades.Consultas.INT);
            oConsulta.insert("tipopretasa",tipopretasa,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("tipologia",tipologia,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("esvpo",esvpo,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("exptevpo",exptevpo,Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("fchvpo",fchvpo,Utilidades.Consultas.DATE);
            oConsulta.insert("autopromocion",autopromocion,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("finca",finca,Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("registro",registro,Utilidades.Consultas.INT);
            oConsulta.insert("localireg",localireg,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("titulareg",titulareg,Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("fcatastral",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"s4tx"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("vivnueva",vivnueva,Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("plantassras",plantassras,Utilidades.Consultas.INT);
            //oConsulta.insert("sotanos",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eda"),Utilidades.Consultas.INT);
            //oConsulta.insert("nplantas",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eea"),Utilidades.Consultas.INT);
            //oConsulta.insert("reforbloq",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33efa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("fchreforbloq",Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"e33ega"),Utilidades.Consultas.DATE);
            //oConsulta.insert("ite",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eha"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchite",Utilidades.Cadenas.getValorMostrarWeb(fchite),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("reforviv",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eja"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("fchreforviv",Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"e33eka"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("caliacabados",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33ela"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("aireacond",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33ema"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("telefonia",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33ena"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("otrasinstal",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eoa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("anxpiscina",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33epa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("anxgaraje",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"f115qa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("anxtrastero",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"f115pa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("anxporche",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eqa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("anxotro",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33era"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("piscinaurb",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33esa"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("jardinesurb",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eta"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("otrosurb",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eua"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("constanio",Utilidades.Cadenas.getValorSinBlancos(rsDatos,"e33eva"),Utilidades.Consultas.INT);
            oConsulta.insert("comentc",comentc,Utilidades.Consultas.VARCHAR);
            
            oConsulta.insert("valtotpretaviv","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretaviv","0",Utilidades.Consultas.INT);
            oConsulta.insert("valtotpretatrt","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretatrt","0",Utilidades.Consultas.INT);
            oConsulta.insert("valtotpretapz","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretapz","0",Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentviv","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentviv","0",Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentrt","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentrt","0",Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentpz","0",Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentpz","0",Utilidades.Consultas.INT);
            oConsulta.insert("valorpretasa","0",Utilidades.Consultas.INT);
            
            oConsulta.insert("estado","2",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchuci","23022012",Utilidades.Consultas.DATE);
            oConsulta.insert("horauci","13:00:00",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchval","01011940",Utilidades.Consultas.DATE);
            oConsulta.insert("horaval","00:00:00",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("postalv",Integer.toString(postalv),Utilidades.Consultas.INT);
            oConsulta.insert("provinv",provinv,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("municiv",municiv,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("localiv",localiv,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("codsituv",codsituv,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("situacionv",situacionv,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("tipoviav",tipoviav,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("callev",callev,Utilidades.Consultas.VARCHAR);            
            oConsulta.insert("numerov",numerov,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("escalerav",escalerav,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("plantav",plantav,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("puertav",puertav,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("VVENTAR",Double.toString(vventar),Utilidades.Consultas.INT);            
            oConsulta.insert("OTRATAS",otratas,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fiabilidad",fiabilidad,Utilidades.Consultas.VARCHAR);
            oConsulta.insert("numexpv",numexpv,Utilidades.Consultas.VARCHAR);
            iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(), conexion);
            if(iNumeroRegistrosAfectados==1)
            {
            
            
            }
        
        
        return iNumeroRegistrosAfectados;
    }//setPretacionesFromB23xt
    
    //***********************************************************************************************************
    //***********************************************************************************************************
    private static int maximoNumeroPeticion(java.sql.Connection conexion) throws java.sql.SQLException
    {
        /*
        int iMaximo = -1;
        Utilidades.Consultas oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
        oConsulta.select("MAX(NUMPET) AS MAXIMO");
        oConsulta.from("PRETASACIONES");
        java.sql.ResultSet rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
        if(rsDatos.next()) iMaximo = rsDatos.getInt("MAXIMO")+1;
        if(rsDatos!=null) rsDatos.close();
        rsDatos = null;
        oConsulta = null;
        return iMaximo;
         * 
         */
        //27/10/2011 se crea una secuencia ya que bankia tambien va a encargar por esta via las
        //pretasaciones
        int iMaximo = 0;
        String sMaximo = Funciones.Fcomunes.obtenerSecuencia(conexion,"129");
        if (sMaximo != null)
        {
            iMaximo = Integer.parseInt(sMaximo);
        }
        return iMaximo;
        
        
    }//maximoNumeroPeticion
    
    //***********************************************************************************************************
    //***********************************************************************************************************
    public static void main(String[] args)
    {
        java.sql.Connection cConexion = null;
        try
        {
            cConexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = yes) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = vtn) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
            int iRegistrosAfectados = Objetos.Pretasaciones.setPretacionesFromB23xt(cConexion);
            System.out.println(iRegistrosAfectados);
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//cath
        finally
        {
            try
            {
                if(cConexion!=null)
                {
                    cConexion.commit();
                    cConexion.close();
                }//if
            }//try
            catch(Exception e)
            {
               System.out.println(e); 
            }
        }//finally
    }//main
    
    //***********************************************************************************************************
    //***********************************************************************************************************
}//Pretasaciones
