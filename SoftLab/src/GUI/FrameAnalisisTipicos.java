/*
 * FrameAnalisisTipicos.java
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

package GUI;

import com.softlab.liblab.DataBase.Grupo;
import com.softlab.liblab.DataBase.Sentence;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class FrameAnalisisTipicos extends javax.swing.JFrame 
{
    private JComboBox cmbNombres;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDatos;
    private JButton btGuardar;
    protected Connection cnn;
    private final DefaultTableModel dtmDatos = new DefaultTableModel(
            new Object [][] {},
            new String []{"Analisis", "Minimo", "Maximo"} );
    
    FrameAnalisisTipicos(Connection cnn)
    {
        this.cnn = cnn; 
        this.setTitle("Analisis Tipicos");
        initComponents();
    }
    
    private void initComponents()
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(190, 280);
        jPanel1 = new javax.swing.JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));     
        cmbNombres= new JComboBox();
        cmbNombres.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie)
            {
                    llenarTabla(cmbNombres.getSelectedItem().toString());
            }
        });
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDatos = new javax.swing.JTable(dtmDatos);
        btGuardar = new JButton("Guardar");
        jScrollPane1.setViewportView(tbDatos);
        
        btGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
            try{
                String sqlMin = "UPDATE esp_rangos SET ";
                String sqlMax = "UPDATE esp_rangos SET ";
                String nombre = cmbNombres.getSelectedItem().toString();
                for (int i = 0; i<dtmDatos.getRowCount(); i++) {
                    String elemento = (String)dtmDatos.getValueAt(i, 0);
                    String minimo = (String)dtmDatos.getValueAt(i, 1);
                    String maximo = (String)dtmDatos.getValueAt(i, 2);
                    sqlMin = sqlMin+elemento+" = "+minimo+" ,";
                    sqlMax= sqlMax+elemento+" = "+maximo+" ,";
                }
                sqlMin =  sqlMin.substring(0,sqlMin.length()-1) 
                        + " WHERE MATERIAL = '"+nombre+ "' AND MIN_MAX = 0";
                sqlMax =  sqlMax.substring(0,sqlMax.length()-1) 
                        + " WHERE MATERIAL = '"+nombre+ "' AND MIN_MAX = 1";
                //System.out.println(query);
                new Sentence(sqlMin,cnn).exec();
                new Sentence(sqlMax,cnn).exec();
            } catch(Exception e){
                JOptionPane.showMessageDialog( FrameAnalisisTipicos.this,
                        "Error al guardar en DB: "+e);
                return;
            }
            JOptionPane.showMessageDialog(FrameAnalisisTipicos.this,
                    "Se guardo correctamente ");
            }
        });
        
        //jPanel1.add(tbDatos);
        jPanel1.add(jScrollPane1);
        jPanel1.add(cmbNombres);
        jPanel1.add(btGuardar);
        add(jPanel1);      
        //pack();
        setVisible(false);       
    }
    
    public void mostrar(String[] nombres)
    {
        DefaultComboBoxModel items = new DefaultComboBoxModel(nombres);
        cmbNombres.setModel(items);
        llenarTabla(nombres[0]);
        this.setVisible(true);
    }
    
    public void llenarTabla(String nombre)
    {
        try {
            limpiarTableModel(dtmDatos);
            String sql = String.format("SELECT SiO2, Al2O3, Al_met, FeO, CaO, MgO, C, S, H2O "
                        +"FROM esp_rangos WHERE MATERIAL = '%s' ORDER BY MIN_MAX",nombre);
                ResultSet rs1 = new Sentence(sql, cnn).openExec();
                addDatosTableModel(dtmDatos,rs1);
         } catch(Exception e) {
             JOptionPane.showMessageDialog(null, "error: "+e);
         }
    }
    
    private void addDatosTableModel(DefaultTableModel dtm, ResultSet rs)
    {
        if(rs == null || dtm == null)
            return; // retorna 2  si rs o dtm es null
        try {
            ResultSetMetaData rsMD = rs.getMetaData();
            int nc = rsMD.getColumnCount(); // numero de columnas
            String[] nuevaFila = new String[3];
            for (int i = 0 ; i < nc; i++) {
                nuevaFila[0] = rsMD.getColumnName(i+1); // agregar nombre elemento
                dtm.addRow(nuevaFila);
            }
            //dtm.setColumnIdentifiers(labels);
            int j = 1;
            while(rs.next()) {
                for(int i = 0;  i< nc; i++){
                    dtm.setValueAt(rs.getString(i+1), i, j);
                }
                j++;
            }
            //dtm.addRow(filaVacia);         
        } catch(Exception e) {
            System.out.println("error en funcion llenarTableModel: "+e);
        }
    }
        
    private void limpiarTableModel(javax.swing.table.DefaultTableModel dtm)
    {
        int filas = dtm.getRowCount();
        for(int i = 1; i<=filas; i++) dtm.removeRow(0);
    }

    DefaultTableModel getDtm()
    {
        return dtmDatos;
    }
    

}

