/*
 * GenReportes.java
 * This file is part of products-control-Prosid
 *
 * Copyright (C) 2015 J.Patricio Hijuitl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package com.softlab.liblab.reports;

import com.softlab.liblab.DataBase.ProductList;
import com.softlab.liblab.tools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class GenReportes
{
    private ProductList prodList;
    private ArrayList<String> productos; //guarda los nombres de los productos
    private ArrayList<String> materiaPrima; //guarda los nombres de la materia prima
    private HashMap nameReport = new HashMap(); //relacion nombre del producto y la plantilla de ireports
    //private HashMap mpReport = new HashMap(); 

    private static final String mallas[] = {
                "PAN","M100","M80","M60","M50","M20","M10","M4","M2","M1"
            };
    
    public GenReportes(ProductList prods)
    {
        this.prodList = prods;
        productos = prodList.getPt();
        materiaPrima = prodList.getMp();
        // relacion nombre del producto y la plantilla de ireports
        nameReport.put("ESPUMAG G","espumag_g");
        nameReport.put("LUGITEC F/T","lugitec_ft");
        nameReport.put("LUGITEC AL","lugitec_al");
        nameReport.put("SIDOX PIEDRA EP","sidox_piedra_ep");
        nameReport.put("CALCIFER A","calcifer_a");
        nameReport.put("SIDOX LFG/C","sidox_lfgc");
        nameReport.put("SIDOX BLOCK EP","sidox_block_ep");     
        
        nameReport.put("MATERIA PRIMA","archivo_jasper");
    }
    
// reporte mensual de producto terminado, recibe año y mes 	
    private JasperPrint[] getReporteMensualPt(int año, int mes, Connection cnn, HashMap datos)
    {
        JasperPrint reporte[] = new JasperPrint[2];
        auxReporteMensual(año, mes,"MPT",datos);
        try {
            System.out.println("creando reporte mensual de producto terminado 1ra parte...");
            JasperReport masterReport1 = (JasperReport)JRLoader.loadObject(
                                    Paths.sPathReporteMensualPt+"analisis_pt1.jasper");
            reporte[0] = JasperFillManager.fillReport(masterReport1,datos,cnn);
            System.out.println("creando reporte mensual de producto terminado 2da parte...");
            JasperReport masterReport0 = (JasperReport)JRLoader.loadObject(
                                    Paths.sPathReporteMensualPt+"analisis_pt2.jasper");
            reporte[1] = JasperFillManager.fillReport(masterReport0,datos,cnn);
        } catch (Exception e){
            System.out.println("error al crear reporte mensual de producto terminado: " + e);
        }
        return reporte;
    }

// reporte mensual de materia prima, recibe año y mes
    private JasperPrint[] getReporteMensualMp(int año, int mes, Connection cnn, HashMap datos)
    {
        JasperPrint reporte[] = new JasperPrint[2];
        auxReporteMensual(año, mes, "MMP",datos);
        try {
            JasperReport masterReport0 = (JasperReport)JRLoader.loadObject(
                                    Paths.sPathReporteMensualMp+"analisis_mp1.jasper");
            reporte[0] = JasperFillManager.fillReport(masterReport0,datos,cnn);            
            
            JasperReport masterReport1 = (JasperReport)JRLoader.loadObject(
                                    Paths.sPathReporteMensualMp+"analisis_mp2.jasper");
            reporte[1] = JasperFillManager.fillReport(masterReport1,datos,cnn);
        } catch (Exception e){
            System.out.println("error al crear reporte mensual de materia prima: " + e);
        }
        return reporte; 
    }
     
// funcion auxiliar para reporte mensula de producto terminado y materia prima
    private void auxReporteMensual(int año, int mes, String tipo, HashMap datos)
    {
        Fecha fecha = new Fecha(año,mes,31);
        datos.put("mes", Fecha.intToMes(mes));
        datos.put("folio", tipo+fecha.getMesS()+ fecha.getAñoS());
        fecha.decrementarMes(1);
        datos.put("fechaInicial",fecha.getFormMySQL());
        System.out.println("fecha inicial: " + fecha.getFormMySQL());
        fecha.setDia(1);
        fecha.incrementarMes(2);
        System.out.println("fecha de final: " + fecha.getFormMySQL());
        datos.put("fechaFinal",fecha.getFormMySQL());
        
    }
  
// "reporte individual de producto terminado anual o materia prima " segun la variable tipo, 
//  para todos los materiales si es null su segundo Parametro.
    public void runReporteIndividual(int año, String material, String tipo, Connection cnn)
    {
        configReport graficos = new configReport();
        HashMap graf = graficos.getGrafs();
        ArrayList<String> lisGraf = null;
        ArrayList<String> nombres = null;
        if(tipo.equals("PT")) nombres = productos;
        if(tipo.equals("MP")) nombres = materiaPrima;  
        // reporte para un solo material
        if(material != null) {
            lisGraf =  (ArrayList)graf.get(material);
            reporteIndividual(año, material, lisGraf, tipo,cnn);
            return;
        }
	for ( String nameProd : nombres) {
            lisGraf =  (ArrayList)graf.get(nameProd);
            if(lisGraf != null) {
                reporteIndividual(año, nameProd,lisGraf,tipo,cnn);
            }
        } // fin for
    }
   
// reporte individual de producto terminado o materia prima anual 
    private String reporteIndividual(int año, String material, 
            ArrayList<String> sGraficas, String tipo, Connection cnn){
        String fileJasper = null, fileDestino = null;
        JasperPrint print;
        HashMap datos = auxReporteIndividual(año);
        
        if(tipo.equals("PT")) {
            fileJasper = Paths.sPathReporteIndividualPt+"analisis_pt.jasper";
            //fileDestino = Rutas.dPathReporteIndividualPt+"calc/"+corregirNombre(material)+" "+año;
            fileDestino = Paths.pathTmp+corregirNombre(material)+" "+año;
        }
        if(tipo.equals("MP")) {
            fileJasper = Paths.sPathReporteIndividualMp+"analisis_mp.jasper"; 
            fileDestino = Paths.pathTmp+corregirNombre(material)+" "+año;
            //fileDestino = Rutas.dPathReporteIndividualMp+"calc/"+corregirNombre(material)+" "+año;        
        }
        datos.put("producto", nameReport.get(material));
        try {
                JasperReport masterReport = (JasperReport)JRLoader.loadObject(fileJasper);
                print = JasperFillManager.fillReport(masterReport,datos,cnn);
                Reportes.runReporte(print, fileDestino, "xls");
                if (sGraficas != null)
                    addGrafs(fileDestino+".xls", graficos(material, año, sGraficas));
                return fileDestino+".xls";
        } catch(Exception e){
            System.out.println("error al crear reporte individual "
                    + "de producto terminado : "+e);
        }
        return null;
    }
    
// agrega grficos los graficos que se encuentran en la carpeta tem al archivo .xls recibido
    private void addGrafs(String fileXls, ArrayList<byte[]> graficas)
    {
        final int ANCHO = 10;
        final int LARGO = 20;
        System.out.println("agregando grafica...");
        File file = new File(fileXls);
        HSSFClientAnchor anchor;
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet sheet = wb.createSheet("graficos");
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            int x = 1,y = 1;
            for (byte[]grafica: graficas) {
                anchor = new HSSFClientAnchor(500,100,600,200,
                        (short)((x*ANCHO)-ANCHO), ((y*LARGO)-LARGO),
                        (short)((x*ANCHO)),(y*LARGO) );
                anchor.setAnchorType( 2 );
                int pictureIndex;
                pictureIndex = wb.addPicture(grafica, HSSFWorkbook.PICTURE_TYPE_PNG );
                HSSFPicture picture =  patriarch.createPicture(anchor, pictureIndex);
                
                if (x == 2 ){
                    x = 1;
                    y++;
                }
                else x++;
            }
                //HSSFPicture picture =  patriarch.createPicture(anchor, loadPicture(new File("/home/arch/prueba.png"), wb ));
                wb.write(new FileOutputStream(file));
        } catch(Exception e) {
            System.out.println("error al agregar garfico al xls: "+e);
        } 
    }
     
    private HashMap auxReporteIndividual(int año)
    {
        HashMap datos = new HashMap();
	Fecha fechaIni = new Fecha(año-1,12,31);
	datos.put("fechaInicial",fechaIni.getFormMySQL());
	
        Fecha fechaFin = new Fecha(año+1,1,1);
	datos.put("fechaFinal",fechaFin.getFormMySQL());
        System.out.println("año: "+año);
        System.out.println("fecha Inicial: "+fechaIni.getFormMySQL());
        System.out.println("fecha final: "+fechaFin.getFormMySQL());
        return datos;
    }
    
    // genera reporte segun su parametro reporte
    public void genReporte(String reporte, HashMap datos, String dir, 
            String formato, Connection cnn)
    {
        JasperPrint jasperPrint = getJasperPrint(datos, reporte,cnn);
        try {
            Reportes.runReporte(jasperPrint, dir+"/"+Reportes.corregirNombreFile(reporte), formato);
        }
        catch (JRException ex) {
            System.out.println("Error en funcion genReporte: "+ex);
        }
    }

    public JasperPrint getJasperPrint(HashMap datos, String reporte, Connection cnn)
    {
        try {
            String fileJasper = null;
            if(reporte.equals("Reporte Producto Terminado"))
                fileJasper = "src/master_report/analisis_pt/general/analisis_pt.jasper";
            else if(reporte.equals("Reporte Materia Prima"))
                fileJasper = "src/master_report/analisis_mp/general/analisis_mp.jasper";
            else {
                String tipo = null;
                String nombre = (String)datos.get("producto");
                /*para conocer a que tipo pertenece el material, dato que sera utilizado
                *para saber el archivo .jasper al que se hara referencia */
                tipo =  prodList.getProducto(nombre).getTipo();
                if(tipo.equals("PT")) {
                    if(nombre.equals("LUGITEC AL"))
                        fileJasper = "src/master_report/reporte_analisis_pt_individual.jasper";
                    else if(nombre.equals("SIDOX BLOCK EP"))
                        fileJasper = "src/master_report/reporte_analisis_sidox_block_ep.jasper";
                    else
                        fileJasper = "src/master_report/reporte_analisis_pt_individual.jasper";
                } else if(tipo.equals("MP")) {
                    //fileJasper = "src/master_report/reporte_analisis_mp.jasper";
                }
            }
            JasperReport masterReport = (JasperReport)JRLoader.loadObject(fileJasper);
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport,datos,cnn);
            return jasperPrint;
            
        } catch (Exception ex) {
            System.out.println("Error en funcion genRAPT: "+ex); return null;
        }
    }
    
    // genera el reporte mensual primera y segunda parte concatena pdf's 
    // y guarda en el directorio correspondiente
    public String runPt(int mes, int año, Connection cnn, HashMap datos)
    {
        String fileRes; 
        Fecha fecha = new Fecha(año,mes,0);
        JasperPrint print[] = getReporteMensualPt(año,mes,cnn,datos); 
        try {
                // guarda el reporte con nombre: año y mes
            String nameFile = fecha.getMesS()+fecha.getAñoS();
            /*Reportes.concatPdf(print,
            Rutas.dPathReporteMensualPt
                +"pdf/"+fecha.getMesS()+fecha.getAñoS());*/
            Reportes.concatPdf(print,Paths.dPathReporteMensualPt+nameFile);
            System.out.println("reporte mensual de producto terminado generado\n");
            return nameFile;
        } catch(Exception e) {
            System.out.println("error al crear reporte mensual de producto terminado : "+e);
        }
        return "";
    }
    
    // genera el reporte primera y segunda parte concatena pdf's 
    // y guarda en el directorio correspondiente
    public String runMp(int mes, int año,Connection cnn,HashMap datos)
    {
            Fecha fecha = new Fecha(año,mes,0);
            JasperPrint print[] = getReporteMensualMp(año,mes,cnn,datos);
            try{
                String nameFile = fecha.getMesS()+fecha.getAñoS(); 
                // guarda el reporte con nombre: año y mes
                Reportes.concatPdf(print, Paths.dPathReporteMensualMp+nameFile);
                System.out.println("reporte mensual de materia prima generado\n");
                return nameFile;
            }catch(Exception e){System.out.println("error al crear reporte mensual"
                    + " de materia prima : "+e);}
            return "";
    }

    /******************* Cotrol de calidad *****************/
    public ArrayList <byte[]> graficos(String material, int año,ArrayList<String> graf)
    {
        System.out.println("Creando graficos..");
        
        ArrayList<byte[]> grs= new ArrayList();
    /*    Fecha fIni = new Fecha(año - 1, 12, 31);
        Fecha fFin = new Fecha(año + 1, 1, 1);
        ResultSet r,r1;
        Object des[];
        
        for (String caracteristica:graf)
        {    
            des = descomponer(caracteristica);
            if(((String)des[1]).equals("GRAN")){
                System.out.println("preparando para crear graficas de granolumetria");
                r = dataBase.getAnalisis(año, material,"FISICO");
                int t = (Integer)des[0];
                int desInt[] = new int[t-2];
                for(int i = 0; i < t-2; i++){ 
                    desInt[i] = Integer.parseInt((String)des[i+2]);
                    System.out.println(desInt[i]);
                }
                try{
                    ArrayList<Double> res[]  = getSumaGran(r,desInt);
                    //for (int i=0; i <sum1.length; i++)System.out.println(sum1[i]);
                    for(int i = 0; i<res.length; i++)
                    grs.add( calidad.creatHistograma(convertArray(res[i]), material, mallas[desInt[i]], Paths.pathTmp) );
                }catch(Exception e){System.out.println("error al obtener suma de mallas"+e);}
            }
            else
            {
                System.out.println("preparando para crear graficas");
                try {
                    r = dataBase.query("SELECT TABLA FROM RELACION WHERE CARACTERISTICA = '"+caracteristica+"'");
                    r.next();
                    String tabla = r.getString("TABLA"); // obtencion de la tabla donde se encuentra la caracteristica deseada
           
                // obtencion de los tados a graficar  
                    String q = "SELECT "+caracteristica+" FROM "+tabla
                    + " WHERE ID_ANALISIS IN(SELECT ID_ANALISIS FROM REGISTRO "
                    + " WHERE NOMBRE = '"+material+"' AND FECHA > '" +fIni.getFormMySQL()+"' AND "
                    + "FECHA < '"+fFin.getFormMySQL()+"')";
                // System.out.println(q);
                    r1 = dataBase.query(q);
            
                    grs.add( calidad.creatHistograma(r1, material, caracteristica, Paths.pathTmp) );
                    r1.beforeFirst();
                
                    grs.add( calidad.crearDispersion(r1, material, caracteristica, Paths.pathTmp) );             
                }catch (SQLException ex) {System.out.println("error en funcion crearHistograma: "+ex);}
            } // fin else
            
        }// fin for*/
        return grs;
    }
    
    private double[] convertArray(ArrayList<Double> r)
    {
        int t = r.size();
        double n[] = new double[t]; 
        for(int i = 0; i<t; i++){
            n[i] = r.get(i);
        }
        return n;
    }
       
    private ArrayList[] getSumaGran(ResultSet r, int[] m) throws SQLException
    {
        //String mallas[] = {"M1","M2","M4","M10","M20","M50","M60","M80","M100","PAN"};
        double sum;
        double t[] = new double[m.length-1];
        ArrayList<Double> []lista = new ArrayList[m.length-1];
        for (int i = 0; i<m.length-1; i++)  lista[i] = new ArrayList();
        while(r.next()){
            for(int k = 0; k < m.length-1; k++){
                t[k] = 0;
                for (int l = m[k]; l < m[k+1];l++){
                    t[k] = t[k] + r.getDouble(mallas[l]);
                }   
                //System.out.println(m[k]+" : "+t[k]);
            }
            sum = 0;
            for(int s = 0; s < t.length; s++)sum = sum +t[s];
            if(sum > 0){ 
                lista[0].add(t[0]);
                lista[1].add(t[1]);
                lista[2].add(t[2]);
            } 
        } // fin while
        return lista;
    }
    
    private Object[] descomponer(String s)
    {
        Object des[] = new Object[10];
        StringTokenizer str = new StringTokenizer(s,":",false);
        int i = 1;
        while(str.hasMoreTokens() && i <= 10 ) des[i++] =  str.nextToken();
        des[0] = new Integer(i); // en la poscion 0 se encuentra el tamño
        return des;
    }
    
    public void createReports(Connection cnn)
    {
            int año = 2011;
            int mes =  6
                    ;
            //graficos("LUGITEC AL", "Al_met", año);
            //int mes = fecha.getMes();
            //int año = fecha.getAño();
            System.out.println("---REPORTE MENSUAL DE PRODUCTO TERMINADO---");
            runPt(mes, año,cnn, new HashMap()); // mes y año actual
            System.out.println("---REPORTE MENSUAL DE MATERIA PRIMA---");
            runMp(mes, año,cnn, new HashMap()); // mes y año actual
            //System.out.println("---REPORTE ANUAL DE PRODUCTO TERMINADO---");
            //runReporteIndividualPtAll(año); // todos los productos
            //runReporteIndividualPt(año, "LUGITEC AL");
            //System.out.println("---REPORTE ANUAL DE MATERIA PRIMA---");
            //runReporteIndividualMpAll(año); // todos los productos
    }
    
    public static String corregirNombre(String nombre)
    {
        StringTokenizer str = new StringTokenizer(nombre,"/",false);
        String newNombre = ""; 
        while (str.hasMoreTokens()){
            String token = str.nextToken();
            newNombre = newNombre + token;
        }
        return newNombre;
    }
    
}

class configReport
{
    private BufferedReader br;
    private HashMap prod;
    
    configReport(){
        File archivo = new File (new File(".").getAbsoluteFile()+"/reportes.conf");
        try {
            FileReader fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            prod = new HashMap();
        } catch(Exception e) {
            System.out.println("error : "+e);
        }
    }
    
    public HashMap getGrafs()
    {
        while(nextProd());
        return prod;
    }
      
    private boolean nextProd() 
    {
        String linea,nombre = null;
        //HashMap prod = new HashMap();
        ArrayList<String> graf = new ArrayList();
        try {
           // System.out.println("empezando analisis de archivo");
            while( (linea = br.readLine()) != null && !linea.trim().equals("BEGIN") )
                System.out.println(linea);
            if(linea == null ){
                System.out.println("no se encontro 'BEGIN'");
                return false;
            }
            
            StringTokenizer str;
            if( (linea = br.readLine()) != null) {
                str = new StringTokenizer(linea, "=",false);
                if(str.nextToken().equals("NOMBRE")) {
                    nombre = str.nextToken();
                    System.out.println("agregando nombre: "+nombre);
                    //prod.put("NOMBRE",str.nextToken());
                } else {
                    System.out.println("error de sintaxis");
                    return  false;
                }
            } else {
                System.out.println("no se encontro 'NOMBRE' ");
                return  false;
            }
            
            while( (linea = br.readLine()) != null && !linea.trim().equals("END") ){
                    str = new StringTokenizer(linea.trim(),"=",false);
                    String tipo = str.nextToken();
                    if(tipo.equals("GRAFICA")){
                        graf.add(str.nextToken());
                        System.out.println("Agregando grafica...");
                    }                        
            }
            prod.put(nombre.trim(), graf);
            return true;
        }catch(Exception e){System.out.println("error: "+e);return false;}
     }
     
}
