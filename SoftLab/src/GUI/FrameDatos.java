/*
 * FrameDatos.java
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
import com.softlab.liblab.DataBase.Producto;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrameDatos extends javax.swing.JFrame 
{
    private JTextArea txaDatos;
    private TablaAnalisis dtmDatos;
    private javax.swing.JPanel jPanel1;
    //private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDatos;
    private TextField txtCmd;
    private Connection c;
    private ProductList prods;
    protected Interfaz1 fr;
    private Aleatorios randoms;
    private String producto,lote,idAnalisis,cmd="";
    private final String[]mallas = {
        "M1","M2","M4","M10","M20","M50","M60","M80","M100","PAN"
    };
   
    FrameDatos(Interfaz1 fr, ProductList prods, Connection c)
    {
        this.fr = fr;  //interfaz principal
        this.prods = prods;
        this.c = c;
        this.setTitle("Analisis Quimico");
        randoms = new Aleatorios(this); // botones para generar numeros alaeatorios
        this.dtmDatos = fr.getDtmAnalisis1();  // dtm donde se muestras los analisis
        initComponents();
        this.setAlwaysOnTop(true);
    }
    
    private void initComponents()
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(200, 500);
        jPanel1 = new javax.swing.JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));   
        txaDatos = new JTextArea();
        //jScrollPane1 = new javax.swing.JScrollPane();
        tbDatos = new javax.swing.JTable(dtmDatos);
        txtCmd = new TextField();
        //jScrollPane1.setViewportView(tbDatos);
        jPanel1.add(tbDatos);
        jPanel1.add(txtCmd);
        jPanel1.add(txaDatos);
        add(jPanel1);
        //pack();
        setVisible(false);
        
        txtCmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae)
            {
                if (!(txtCmd.getText().trim().equals("")) )
                    cmd = txtCmd.getText();
                txtCmd.setText(null);
                if (cmd.equals("")) { 
                    return;
                } else if (cmd.equalsIgnoreCase("g")) { // guarda
                    cmd = "g";
                    txaDatos.append("Guardando..\n");
                    fr.okAnalisis(idAnalisis, producto);
                    txaDatos.append("Guardado..\n");
                } else if(cmd.equalsIgnoreCase("a")) { // muestra anterior
                    cmd = "a";
                    fr.antAnalisisEnDtm(Integer.parseInt(idAnalisis));
                } else if(cmd.equalsIgnoreCase("s")) { // muestra sieguiente
                    cmd = "s";
                    fr.sgtAnalisisEnDtm(Integer.parseInt(idAnalisis));
                } else if(cmd.equalsIgnoreCase("rand")) { // rellena
                    cmd = "rand";
                    randoms.llenarDtm(false);
                } else if(cmd.equalsIgnoreCase("randd")) { // remplaza
                    cmd = "randd";
                    randoms.llenarDtm(true);
                } else if(cmd.equalsIgnoreCase("sum")) { // suma granolumetria
                    cmd = "sum";
                    int t = 0;
                    Float r;
                    for (String m: mallas) { 
                        r = dtmDatos.getDato(m);
                        if (r == null) {
                            JOptionPane.showMessageDialog(FrameDatos.this,"Datos no validos");
                            return;
                        }
                        t = (int)(t + r);
                    }
                    txaDatos.append("sum gran = "+t+"\n");
                } else if (cmd.equalsIgnoreCase("check")) { // cierra
                    Producto p = prods.getProducto(producto);
                    try {
                        String info = p.check(dtmDatos.getDatos(), c);
                        JOptionPane.showMessageDialog(FrameDatos.this, info);
                    } catch(SQLException e) {
                        JOptionPane.showMessageDialog(FrameDatos.this, "Error SQL: "+e);
                    }
                } else if (cmd.equals("c")) { // cierra
                    setVisible(false);
                }
            }
        });
          
    }
    
    public void mostrar(String noAnalisis, String nombre, String lote)
    {
        idAnalisis  = noAnalisis;
        producto = nombre;
        this.lote = lote;
        setTxaDatos();
        this.setVisible(true);
    }
    
    private void setTxaDatos()
    {
        txaDatos.setText("");
        txaDatos.append("Nombre: "+producto+"\n");
        txaDatos.append("N° Analisis: "+idAnalisis+"\n");
        txaDatos.append("Lote: "+lote+"\n");       
    }
    
    /* ACCIONES
    private void mapeoTeclas()
    {
        ActionMap mapaAccion = jPanel1.getActionMap();
        InputMap map = jPanel1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // CTRL + S
        KeyStroke ctrl_s = KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK,true);
        // CTRL + A
        KeyStroke ctrl_a = KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK,true);
        // CTRL + G
        KeyStroke ctrl_g = KeyStroke.getKeyStroke(KeyEvent.VK_G,Event.CTRL_MASK,true);
        // CTRL + R
        KeyStroke ctrl_r = KeyStroke.getKeyStroke(KeyEvent.VK_R,Event.CTRL_MASK,true);
        // CTRL + f
        KeyStroke ctrl_f = KeyStroke.getKeyStroke(KeyEvent.VK_F,Event.CTRL_MASK,true);

        
        //Key Actions
        map.put(ctrl_s , "accion_ctrl_s");
        mapaAccion.put("accion_ctrl_s",Accion_CTRLS()); // analisis siguiente

        map.put(ctrl_a , "accion_ctrl_a");
        mapaAccion.put("accion_ctrl_a",Accion_CTRLA()); // analisis anaterior
        
        map.put(ctrl_g , "accion_ctrl_g");
        mapaAccion.put("accion_ctrl_g",Accion_CTRLG()); // guarda analisis
        
        map.put(ctrl_r , "accion_ctrl_r");
        mapaAccion.put("accion_ctrl_r",Accion_CTRLR()); // muestra panel para generacion de numeros aleatorios

        map.put(ctrl_f , "accion_ctrl_f");
        mapaAccion.put("accion_ctrl_f",Accion_CTRLF()); // muestra panel para generacion de numeros aleatorios

    }
*/

    DefaultTableModel getDtm()
    {
        return dtmDatos;
    }
    
    public String getMaterial()
    {
        return producto;
    }

}


// clase que genera numeros aleatorios dentro del rango de especificacion de cada
// materaial

class Aleatorios
{   
    private FrameDatos frd;
    private LaboratorioDB lab;
    
    public Aleatorios(FrameDatos frd)
    {
        this.frd = frd;
	lab = this.frd.fr.getLaboratorio();
        iniComponets();
    }

    private void iniComponets()
    {   
        
    }
    
    // llena dtm con numeros aleatorios
    // ramplza los datos dependiendo de su parametro remplazar
    public void llenarDtm(boolean remplazar)
    {
        DefaultTableModel dtm = frd.getDtm();
        String elemento;
        float ran;
        HashMap randoms = getAleatorios(frd.getMaterial());
        int noElementos = dtm.getRowCount();
        //**** llenado de dtm con valores aleatorios no incluye granolumtria
        for (int i = 0; i < noElementos; i++ ){
            // obtiene el nombre del elemento actual
            elemento = String.valueOf(dtm.getValueAt(i,0));
            /*si no se remplaza y el elemento actual tiene valor continua 
             con el siguiente elemento*/
            if (!remplazar && ( dtm.getValueAt(i,1)) != null)
                continue;
            /*si se remplaza o no hay valor en el elemento actual
            busca en el HasMap el valor del elemento correspondiente al 
            elemento acutal del dtm*/
            try { 
                ran = (Float)randoms.get(elemento);
                //System.out.println("remplazando dato: "+elemento+" = "+ran);
                dtm.setValueAt(ran,i,1);
             } catch(Exception e) {
                 System.out.println("error en funcion llenarDtm: "+e);
             }
        }
        //busca si hay datos en el dtm en los epacios de granolumetria en caso de no replazar
        if (!remplazar) {
            for (int i = 0; i < noElementos; i++){
                // primero busca malla 1 para ver si hay o no valor 
                if ( String.valueOf(dtm.getValueAt(i,0)).equals("M1") ) {
                    // si no hay valor rompe el ciclo para comenzar el llenado 
                    // si lo hay entonces termina la funcion
                    if ( dtm.getValueAt(i,1) == null ) {
                        break;
                    } else { 
                        return;
                    }
                }  
            }
        }
        
        // llenado de granolumetria
        int []sup = getLim(frd.getMaterial(),1);// limite inferirior
        int []inf = getLim(frd.getMaterial(),0);// limite superior
        int [] aleatorios = null;
        // genera numeros aleatorios hasta que la suma sea igual a 100
        int total = 0, rTotal = 0;
        int aTotal;
        while (total != 100) {
            aleatorios = getAleatoriosG(sup,inf);
            aTotal = total;
            total = sumaVec(aleatorios);
            if (aTotal == total)
                rTotal++;
            if (rTotal > 3)
                break; // previene ciclo infinito 
            if(total > 100) {
                sup = aleatorios;
            } else if (total < 100) { 
                inf = aleatorios;
            }
	}
      
        String malla;
        for (int i = 0; i < noElementos; i++) {
            // busca la primera malla suficiente para encontrar las demas 
            // ya que vienen juntas todas 
            // obtiene el nombre de la malla actual
            malla = String.valueOf(dtm.getValueAt(i,0));
            if (malla.equals("M1")) {
                // 10 es el numero de mallas
                // llenado de el dtm apartir de la malla 1 encontrada
                for(int j = 0; j < 10; j++){ 
                    dtm.setValueAt(aleatorios[j], i + j-1, 1);
                }
                return; // termina funcion 
            }
   
        }
    }
    
    private int[] getLim(String material, int sup_inf)
    {
        int inf[] = new int[10];  
        ResultSet rsMin = lab.query("SELECT * FROM ESP_GRAN WHERE MATERIAL = '"
                + material +"' AND MIN_MAX ="+sup_inf);
        try {
            if (rsMin.next()) {
                for (int i = 1; i <= 10; i++){
                    inf[i] = rsMin.getInt(i+2); 
                }
            }
        } catch(Exception e) {
            System.out.println("error al ejecutar consulta: "+e);
        }
        return inf;
    }
    
// genera numeros aleatorios dentro del rango inf - sup
    private int[] getAleatoriosG(int []inf ,int[]sup)
    {
        int tam = inf.length, dif;
	int [] aleatorios = new int[tam];
	for (int i = 0; i < tam; i++ ) {
	// generar alelatorios dentro del rango
            dif = sup[i] - inf[i];
            aleatorios[i] = (int) Math.floor(Math.random()*dif + inf[i]); 
	}
	return aleatorios;
    }

    private int sumaVec(int V[]) {
	int sum = 0;
	for(int i = 0; i < V.length; i++) {
		sum = sum + V[i];
	}
	return sum;
    }
             
    private HashMap getAleatorios(String material)
    {
        HashMap aleatorios =  new HashMap();
        try {	
            ResultSet rsMin = lab.query("SELECT * FROM ESP_RANGOS WHERE MATERIAL = '"
                    + material +"' AND MIN_MAX = 0");
            ResultSet rsMax = lab.query("SELECT * FROM ESP_RANGOS WHERE MATERIAL = '"
                    + material +"' AND MIN_MAX = 1");
            ResultSetMetaData rsMD = rsMin.getMetaData();
            
            float dif, r;
            if (rsMin.next() && rsMax.next()) {
                int nc = rsMD.getColumnCount();
                for (int i = 3; i <= nc; i++) {
                    if (!rsMin.getString(i).equals("null")) {
                        dif = rsMax.getFloat(i) - rsMin.getFloat(i); 
                        // calcula un numero aleatorio dentro del rango minimo y mnximo de la BD
                        r = (float) (Math.random() * dif + rsMin.getFloat(i)); 
                        //System.out.println("agregando nuevo dato: "+rsMD.getColumnName(i)+" = "+r);
                        aleatorios.put( rsMD.getColumnName(i),redondear(r) );
                    }
                } // fin for
            }        
            return aleatorios;
        } catch (SQLException ex){
            System.out.println("error en funcion getAleatorios: "+ex);
        }
        return aleatorios;
    }
    
    public static float redondear(float numero)
    {
      return (float)(Math.rint(numero*100)/100);
    }
    
    
}
