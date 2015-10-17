/*
 * Paths.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Paths
{

    //rutas donde se encuentras las plantillas para los reportes
    public static final String path = getRaiz();
    public static final String sPathReporteMensualPt = path
            +"master_report/analisis_pt/general/"; 
    public static final String sPathReporteMensualMp = path
            +"master_report/analisis_mp/general/";
    public static final String sPathReporteIndividualPt = path
            +"master_report/analisis_pt/individual/";
    public static final String sPathReporteIndividualMp = path
            +"master_report/analisis_mp/individual/";  
    public static final String sPathCertificados = path+"master_report/certificados/"; 
    
// rutas destino de los reportes
    public static final String dPathReporteMensualPt = path
            +"reportes/reporte_mensual_pt/";
    //public static final String dPathReporteMensualPt = path+"/tmp/reporte_mensual_pt/";
    public static final String dPathReporteMensualMp = path
            +"reportes/reporte_mensual_mp/";
    public static final String dPathReporteIndividualPt = path
            +"reportes/reporte_individual_pt/";
    public static final String dPathReporteIndividualMp = path
            +"reportes/reporte_individual_mp/";
    public static final String dPathCertificados = path
            +"reportes/certificados/";
    public static final String pathTmp = path+"reportes/tmp/";
    
    
    private static String getRaiz()
    {
        File archivo = new File (new File(".").getAbsoluteFile()+"/raiz");
        BufferedReader br;
        try {
            FileReader fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            return br.readLine();
        } catch (Exception e){System.out.println("error : "+e); return "";}
           
    }
    
    public static String getPathPartePt(String material)
    {
        return sPathReporteMensualPt+"partes/"+material+".jasper";
    }
    
    public static String getPathParteMp(String material)
    {
        return sPathReporteMensualMp+"partes/"+material+".jasper";
    }
    
    public static String getPathAnualMp(String material)
    {
        return sPathReporteIndividualMp+material+".jasper";
    }
    
    public static String getPathAnualPt(String material)
    {
        return sPathReporteIndividualPt+material+".jasper";
    }

    public static String getPathImage(String image)
    {
        String p = path+"image/";
        if (image.equals("firma")) {
            p =  p + "firma.png";
        }
        return p;
    }
    public static String getPathTmp() 
    {
        return pathTmp;
    }
    
}
