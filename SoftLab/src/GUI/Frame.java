/*
 * Frame.java
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

import com.softlab.liblab.DataBase.LaboratorioDB;
import com.softlab.liblab.DataBase.ProductList;
import com.softlab.liblab.DataBase.Session;
import com.softlab.liblab.tools.Fecha;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class Frame extends JFrame
{
     private LaboratorioDB laboratorio;
    //private GenReportes genReportes;
    private FrameDatos frDatos;    
    private final Tabla dtmRegistro = new TablaRegistro(
        new Object [][] {},
        new String []{"No Analisis", "Lote", "Cantidad","Cliente/Proveedor",
                "Producto","Certificado"} );
    private final TablaAnalisis dtmAnalisis1 = new TablaAnalisis(
        new Object [][] {}, new String []{"Parametro", "Resultado"});
    private final Tabla dtmEmbarques1 = new Tabla();
    private final Tabla dtmGrupos = new Tabla(
        new Object [][] {},
        new String []{"Nombre", "Incluir(s/n)"} );    
    private DefaultComboBoxModel itemsAgregar;
    private JTextComponent cmbTxt;
    private Fecha fecha;
    private javax.swing.JFrame thiss = this;
    private JPopupMenu popup = new JPopupMenu();
    private Session m_s;
    private ProductList prods;
    private FrameAnalisisTipicos frAnalisisTipicos;
    /** Creates new form Interfaz1 */
    
    public Frame(Session m_s) 
    {
        super("PROSID - Laboratorio ");
        setLocation(80, 80);
        setSize(600,600);
        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        setVisible(true);
        initComponents();
        this.m_s = m_s;
        //try{
            //laboratorio = new LaboratorioDB(m_s);
            //loadProds();     
            //
            //frDatos = new FrameDatos(this,prods,m_s.getConnection());
            //frAnalisisTipicos =  new FrameAnalisisTipicos(m_s.getConnection());
            //reInitComponents();
        //}catch(SQLException e){}
        //genReportes = new GenReportes();
    }

    private void initComponents()
    {
        JPanel panelSuperior = new JPanel();
        JButton btEntrada = new JButton("Entrada");
        JButton btEmbarques = new JButton("Embarques");
        JButton btRegistro = new JButton("Registro de productos");
        panelSuperior.add(btEntrada,0,0);
        panelSuperior.add(btEmbarques);
        panelSuperior.add(btRegistro);   
        this.add(panelSuperior);
        this.add(panelSuperior);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public static void main(String args[])
    {
          try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame(null);
            }
        });
        }


    
}
