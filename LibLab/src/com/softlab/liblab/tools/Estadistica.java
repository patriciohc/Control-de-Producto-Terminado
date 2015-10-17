/*
 * Estadistica.java
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

package com.softlab.liblab.tools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import org.jfree.data.statistics.HistogramDataset;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Estadistica
{
    public Estadistica()
    {
       
    }
    
    public byte[] crearDispersion(ResultSet datos, String material, 
        String caracteristica, String destino) throws SQLException
    {
        
        final XYSeries serie1 = new XYSeries(material);
        int i = 1;
        float maximo = 0;
        float minimo = 100;
        float dato;
        while (datos.next()) {
            dato = datos.getFloat(caracteristica);
            if(dato == 0 )
                continue;
            if(dato > maximo)
                maximo = dato;
            if(dato < minimo)
                minimo = dato;
            serie1.add(i++, dato);
        }

        final XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(serie1);
      //  collection.addSeries(serie2);
        try {
            JFreeChart gr = Charts.crearDispersion(collection,caracteristica, minimo,maximo);
            return Charts.chartToBytes(gr);
            //Charts.save(gr, Rutas.pathTmp+"/dispersion/"+caracteristica+".png");
            //ChartUtilities.saveChartAsPNG(new File(), gr, ANCHO_GRAFICA, ALTO_GRAFICA);
        } catch (Exception e){
            e.printStackTrace(); 
            return null;
        }
    }
       
   // crea grafico de dispersion para granolumetria 
    public byte[] crearDispersion(int[] datos, String material, String malla, 
        String destino) throws SQLException
    {
        final XYSeries serie1 = new XYSeries(material);
        int i = 1;
        float maximo = 0;
        float minimo = 100;
        int dato;
        while (i <= datos.length) {
            dato = datos[i-1];
            //if(dato == 0 )continue;
            if(dato > maximo)
                maximo = dato; // obtencion de maximo
            if(dato < minimo)
                minimo = dato; // obtencion de minimo
            serie1.add(i++, dato);
        }
        final XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(serie1);
      //  collection.addSeries(serie2);
        try {    
            JFreeChart gr = Charts.crearDispersion(collection,malla, minimo,maximo);
            return Charts.chartToBytes(gr);
            //Charts.save(gr, Rutas.pathTmp+"/dispersion/"+malla+".png");
            //ChartUtilities.saveChartAsPNG(new File(), gr, ANCHO_GRAFICA, ALTO_GRAFICA);
        } catch (Exception e) {
            e.printStackTrace(); 
            return null;
        }
    }
    // crea un histograma de frecuencias
    public byte[] creatHistograma(ResultSet datos, String material, 
        String caracteristica, String destino) throws SQLException
    {
        int noI = 0; // numero de intervalos
        int noDatos = noDatos(datos,false); // regresa el numero de datos, excluye ceros
        // definicion deintervalos
        if (noDatos <= 100) {
            noI = 12;
        } else if (noDatos <= 500) {
            noI = 16;
        } else if (noDatos > 500) { 
            noI = 19;
        }
        double[] vector = new double[noDatos];
        System.out.println(noDatos);
        int index = 0;
        while (datos.next()) {
            double d = datos.getDouble(1);
            if((int)d != 0) {
                vector[index++] = d;
                //System.out.println(d);
            }
        }
        JFreeChart h = Charts.crearHistograma(noI,vector,"Histograma de Frecuencia "
                + caracteristica,"% "+caracteristica);
        return Charts.chartToBytes(h);
        //datos.beforeFirst();
        //Charts.save(h,destino+"/histograma/"+caracteristica+".png");
        //return h.getImageIcon();
    }
    
    
    
        // crea un histograma de frecuencias para granolumetria
    public byte[] creatHistograma(double []datos, String material, String malla, 
        String destino) throws SQLException
    {
        int noI = 0; // numero de intervalos
        int noDatos = datos.length;
        // definicion deintervalos
        if(noDatos <= 100) { 
            noI = 12;
        } else if (noDatos <= 500) {
            noI = 16;
        } else if(noDatos > 500) { 
            noI = 19;
        }
        //double[] vector = new double[noDatos];
        System.out.println(noDatos);
        if(!malla.equals("PAN")) malla =  "mayor que "+malla;
        JFreeChart h = Charts.crearHistograma(noI,datos,"Histograma de Frecuencias",malla);
        return Charts.chartToBytes(h);
        //datos.beforeFirst();
        //Charts.save(h,destino+"/histograma/"+malla+".png");
        //return h.getImageIcon();   
    }
    
    // funcion que regresa menor y mayor de los datos recibidos, escluye ceros
    private float[] extremos(ResultSet datos) throws SQLException
    {
        // posicion 0 valor minimo
        //posicion 1 valor maximo
        float ex[] =  new float[2];
        datos.next();
        ex[0] = datos.getFloat(1);
        ex[1] = ex[0];
        while (datos.next()) {
            float dato = datos.getFloat(1);
            if (dato != 0 ) {
                if(dato < ex[0])
                    ex[0] = dato;
                if(dato > ex[1])
                    ex[1] = dato;
            }
        }
        datos.beforeFirst();
        return ex;
    }

    // regresa el numero de datos que hay en el ResultSet,
    // incluye ceros o excluye sengun la variable incluirCeros
    private int noDatos(ResultSet datos, boolean incluirCeros) throws SQLException
    {
        int noDatos = 0;
        while (datos.next()) {          
            if (incluirCeros) {
                noDatos = noDatos + 1;
            } else {
                if(datos.getInt(1) != 0)
                    noDatos = noDatos + 1;
            }
        }
        datos.beforeFirst();
        return noDatos;
    }

    //************************ medidas de tendencia central ********************

    public static float  promedio(ResultSet datos) throws SQLException
    {
        int noDatos = 0;
        float suma = 0,d;
        while(datos.next()) {
            d = datos.getFloat(1);
            if((int)d != 0) {
                noDatos++;
                suma = suma + datos.getFloat(1);
            }
        }
        datos.beforeFirst();
        return suma/noDatos;
    }

    public static float  moda(ResultSet datos) throws SQLException
    {
        int frec = 0;
       // int noDatos = 0;
        float aux;
        float moda[] =  new float[2];
        moda[1] = 0;  //en posicion 1 se guardara la frecuencia
        datos.next();
        for(int i = 1; datos.absolute(i); i++) {
            aux = datos.getFloat(1);
            if (aux != 0) {
                datos.beforeFirst();
                while (datos.next()) {
                    if(aux == datos.getFloat(1))
                        frec = frec + 1;
                }
                if(frec > moda[1]) {
                    moda[0] = aux;
                    moda[1] = frec;
                }
            }
        }
        datos.beforeFirst();
        return moda[0];
    }

    //************************ medida de dispersion **************************

    public float rango(ResultSet datos) throws SQLException
    {
        float ex[] = extremos(datos);
        return ex[1] - ex[0];
    }
    
    public float desviacionStd(ResultSet datos) throws SQLException
    {
        float promedio = promedio(datos);
        float suma = 0,x;
        int noDatos = noDatos(datos,false);
        while (datos.next()) {
            x = datos.getFloat(1);
            if(x != 0)
                suma = suma + (float)Math.pow(2, x - promedio);
                //suma = suma + cuadrado(x - promedio);
        }
        datos.beforeFirst();
        return (float)Math.sqrt(suma/(noDatos - 1));
    }


    /************* Descripcion de conceptos  **************************
     *
     */

    public static String promedio()
    {
        return "";
    }

    public static String moda()
    {
        return "";
    }

    public static String rango()
    {
        return "";
    }

    public static String desviacionStd()
    {
        return "";
    }
    
}


// histrograma de frecuencias
class Charts 
{
    public static final int ANCHO_GRAFICA = 600;
    public static final int ALTO_GRAFICA = 400;
    
   // private static Color COLOR_SERIE_1 = new Color(255, 128, 64);
   // private static Color COLOR_SERIE_2 = new Color(28, 84, 140);
    private static Color COLOR_RECUADROS_GRAFICA = new Color(31, 87, 4);
    private static Color COLOR_FONDO_GRAFICA = Color.white;

    
    public static JFreeChart crearHistograma(int noI, double []datos, 
        String nombreGrafico, String nombreEjeX)
    {       
 
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Histograma de Frecuencias", datos, noI);
        JFreeChart chart = ChartFactory.createHistogram(nombreGrafico, nombreEjeX,
                            "Frecuencia", dataset, PlotOrientation.VERTICAL, 
                            false, true, false);
       // ChartFactory.createXYLineChart(nombreEjeX, nombreEjeX, nombreEjeX, dataset, PlotOrientation.HORIZONTAL, true, true, true)

        return chart;
    }
    

    public static ImageIcon getImageIcon(JFreeChart chart)
    {
        try {
            //String curDir = System.getProperty("user.dir");
            //ChartUtilities.saveChartAsJPEG(new File("C:grafico.jpg"), chart, 500, 300);
            //ChartUtilities.saveChartAsJPEG(new File("/home/phc/Escritorio/grafico.jpg"), chart, 500, 300);
            //ChartUtilities.saveChartAsJPEG(new File(curDir+"/grafico.jpg"), chart, 500, 300);
            java.awt.image.BufferedImage b = chart.createBufferedImage(ANCHO_GRAFICA, ALTO_GRAFICA);
            
            return  new ImageIcon(b);
        } catch (Exception e){
            System.err.println("Error creando grafico."+e);
        }
        return null;
    }
    
    public static byte[] chartToBytes(JFreeChart chart)
    {
        try {
            java.awt.image.BufferedImage b = chart.createBufferedImage(ANCHO_GRAFICA, ALTO_GRAFICA);
            //BufferedImage originalImage =  ImageIO.read(new File("c:\\image\\mypic.jpg"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( b, "png", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (Exception e) {
            System.err.println("Error al convertir chart a bytes[] : "+e);
        }
        return null;
    }
    
    
    public static void save(JFreeChart chart, String archivo)
    {
        try {
            ChartUtilities.saveChartAsPNG(new File(archivo), chart, ANCHO_GRAFICA, ALTO_GRAFICA);
        } catch (IOException ex) { System.out.println("error al guardar imagen"+ex); }
    }
    
    
    
    public static JFreeChart crearDispersion(XYSeriesCollection dataset, 
        String nombreElemento, float minimo, float maximo) 
    {
        final JFreeChart chart = ChartFactory.createXYLineChart("Dispersion "
                +nombreElemento, null, nombreElemento+" en % ",
                dataset,
                PlotOrientation.VERTICAL,
                true, // uso de leyenda
                false, // uso de tooltips 
                false // uso de urls
                );
        // color de fondo de la gráfica
        chart.setBackgroundPaint(COLOR_FONDO_GRAFICA);
 
        final XYPlot plot = (XYPlot) chart.getPlot();
        configurarPlot(plot);
 
        final NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis();
        configurarDomainAxis(domainAxis);
         
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        float rango = maximo - minimo;
        float margen = rango * 0.5F;
        
        rangeAxis.setTickUnit(new NumberTickUnit(rango/5));
        rangeAxis.setRange(minimo-margen, maximo+margen);
        //final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
       // configurarRendered(renderer);
        return chart;
    }
     
    // configuramos el contenido del gráfico (damos un color a las líneas que sirven de guía)
    private static  void configurarPlot (XYPlot plot)
    {
        plot.setDomainGridlinePaint(COLOR_RECUADROS_GRAFICA);
        plot.setRangeGridlinePaint(COLOR_RECUADROS_GRAFICA);
    }
     
    // configuramos el eje X de la gráfica (se muestran números enteros y de uno en uno)
    private static void configurarDomainAxis (NumberAxis domainAxis)
    {
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setTickUnit(new NumberTickUnit(1));
    }    
    // configuramos las líneas de las series (añadimos un círculo en los puntos y asignamos el color de cada serie)
/*    private void configurarRendered (XYLineAndShapeRenderer renderer) {
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesPaint(0, COLOR_SERIE_1);
        renderer.setSeriesPaint(1, COLOR_SERIE_2);
    }
  */  
}
