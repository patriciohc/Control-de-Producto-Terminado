/*
 * Reportes.java
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

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.view.JasperViewer;

public class Reportes 
{
   // concatena lista de pdf's
    public static void concatPdf(PdfReader[] readers, String outputFile) 
            throws FileNotFoundException, DocumentException, IOException
    {
        FileOutputStream outputStream =  new FileOutputStream(outputFile);
        PdfCopyFields copy = new PdfCopyFields(outputStream);
        for (int i = 0; i < readers.length; i++) 
            copy.addDocument(readers[i]);
        copy.close();
        outputStream.flush();
        outputStream.close();
    } 
    
     // concatena lista de pdf's
    public static void concatPdf(JasperPrint[] prints, String outputFile) 
            throws FileNotFoundException, DocumentException, IOException, Exception
    {
        FileOutputStream outputStream =  new FileOutputStream(outputFile+".pdf");
        PdfCopyFields copy = new PdfCopyFields(outputStream);
        for (int i = 0; i < prints.length; i++) { 
            System.out.println("concatenando pdf no: " + i);
            PdfReader reader = creatPdf(prints[i]);
            copy.addDocument(reader);
        }
        copy.close();
        outputStream.flush();
        outputStream.close();
    } 

      // concatena lista de pdf's
    public static void concatPdf(ArrayList<JasperPrint> prints, String outputFile) 
            throws FileNotFoundException, DocumentException, IOException, Exception
    {
        FileOutputStream outputStream =  new FileOutputStream(outputFile+".pdf");
        PdfCopyFields copy = new PdfCopyFields(outputStream);
        for (int i = 0; i < prints.size(); i++) { 
            System.out.println("concatenando pdf no: " + i);
            PdfReader reader = creatPdf(prints.get(i));
            copy.addDocument(reader);
        }
        copy.close();
        outputStream.flush();
        outputStream.close();
    } 
       
    public static PdfReader creatPdf(JasperPrint report) throws Exception
    {
        byte[] bytes = JasperExportManager.exportReportToPdf(report);
        return new PdfReader(bytes);
        //PdfReader reader1 = new PdfReader("1PDF.pdf");
    }
    
        // genera reporte apartir de los parametros recibidos
    public static void runReporte(JasperPrint jasperPrint, String archivo, String formato) throws JRException
    {
        if (formato.equals("ver")) {
            JasperViewer jViewer = new JasperViewer(jasperPrint,false);
            jViewer.setTitle(" Reporte de Analisis de Producto Terminado ");
            jViewer.setVisible(true);
            return;
        }
        if(formato.equals("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint,archivo+".pdf");
            return;
        }
        if(formato.equals("xls")) {
            JExcelApiExporter xlsExporter = new JExcelApiExporter();
            //JRXlsExporter exporterXLS = new JRXlsExporter();

            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,
                                         jasperPrint);
            
           /* xlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
                                         Boolean.FALSE);*/
            xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                                         Boolean.TRUE);
            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                                         archivo+".xls");
            xlsExporter.exportReport();
        }
        
    }
    
 /*   
    public static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate) 
    {

        Document document = new Document();
	try 
        {
            
            // pasa el List de ImputStream en List de PdfReader
            List<InputStream> pdfs = streamOfPDFFiles;
	    List<PdfReader> readers = new ArrayList<PdfReader>();
	    int totalPages = 0;
	    Iterator<InputStream> iteratorPDFs = pdfs.iterator();
	 
            while (iteratorPDFs.hasNext()) 
            {
                InputStream pdf = iteratorPDFs.next();
	        PdfReader pdfReader = new PdfReader(pdf);
	        readers.add(pdfReader);
	        totalPages += pdfReader.getNumberOfPages();
            } // termina de pasar List de ImputStream en List de PdfReader
	 
	    PdfWriter writer = PdfWriter.getInstance(document, outputStream);	 
	    document.open();
	    PdfContentByte cb = writer.getDirectContent();
	    PdfImportedPage page;
	    int currentPageNumber = 0;
	    int pageOfCurrentReaderPDF = 0;
            
            
	    Iterator<PdfReader> iteratorPDFReader = readers.iterator();
	    while (iteratorPDFReader.hasNext()) 
            {
                PdfReader pdfReader = iteratorPDFReader.next();
	 
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) 
                {
                    Rectangle rectangle = pdfReader.getPageSizeWithRotation(1);
	            document.setPageSize(rectangle);
	            document.newPage();
	 
	            pageOfCurrentReaderPDF++;
	            currentPageNumber++;
	            page = writer.getImportedPage(pdfReader,pageOfCurrentReaderPDF);
	            switch (rectangle.getRotation()) 
                    {
                        case 0:
                            cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
	                    break;
                        case 90:
                            cb.addTemplate(page, 0, -1f, 1f, 0, 0, pdfReader.getPageSizeWithRotation(1).getHeight());
	                    break;
                        case 180:
                            cb.addTemplate(page, -1f, 0, 0, -1f, 0, 0);
	                    break;
                        case 270:
                            cb.addTemplate(page, 0, 1.0F, -1.0F, 0, pdfReader
	                                .getPageSizeWithRotation(1).getWidth(), 0);
	                    break;
	                default:
                            break;
                    }
                    if (paginate) 
                    {
                        cb.beginText();
                        cb.getPdfDocument().getPageSize();
	                cb.endText();
	            }
                }
	        pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
	    document.close();
	    outputStream.close();
        } 
        catch (Exception e) 
        {} 
        finally 
        {
            if (document.isOpen())
	    document.close();
	    try 
            {
                if (outputStream != null)
                    outputStream.close();
	    } 
            catch (IOException ioe) 
            {}
        }
    } // fin concarPDFs
   */ 
    
    
 // elimina las "/" y "\" del String recibido
    public static String corregirNombreFile(String nombre)
    {
        //String n = nombre;
        char n[] = new char[nombre.length()];
        int j = 0;
        for (int i = 0; i<nombre.length(); i++) {
            char c = nombre.charAt(i);
            if(c != '/' && c != '\\' ) n[j++] = c;            
        }
        return String.valueOf(n).substring(0, j);
    }
    
    
    
}
 

