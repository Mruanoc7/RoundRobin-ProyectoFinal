import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class RoundRobinSimulator extends JFrame {
    private JTextField quantumField, rafagaField, llegadaField;
    private JButton agregarButton, iniciarButton, reiniciarButton;
    private JLabel lblTME, lblTMR, lblDiagramaGantt;
    private JTextArea txtOperacionesTME, txtOperacionesTMR;
    private DefaultTableModel modeloTablaTiempos;
    private JTable tablaTiempos;
    private ArrayList<Proceso> listaProcesos = new ArrayList<>();
    private String diagramaGantt = "";

    public RoundRobinSimulator() {
        // Configurar la ventana
        setTitle("Simulador Round Robin");
        setSize(900, 700); // Ajustar tamaño
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Etiquetas para los campos
        JLabel quantumLabel = new JLabel("Quantum:");
        quantumLabel.setBounds(50, 0, 100, 25);
        add(quantumLabel);

        JLabel rafagaLabel = new JLabel("Ráfaga:");
        rafagaLabel.setBounds(50, 40, 100, 25);
        add(rafagaLabel);

        JLabel llegadaLabel = new JLabel("Tiempo de Llegada:");
        llegadaLabel.setBounds(50, 80, 150, 25);
        add(llegadaLabel);

        // Campos de texto
        quantumField = new JTextField();
        quantumField.setBounds(170, 0, 100, 25);
        add(quantumField);

        rafagaField = new JTextField();
        rafagaField.setBounds(170, 40, 100, 25);
        add(rafagaField);

        llegadaField = new JTextField();
        llegadaField.setBounds(170, 80, 100, 25);
        add(llegadaField);

        // Botones
        agregarButton = new JButton("Agregar");
        agregarButton.setBounds(280, 40, 100, 25);
        iniciarButton = new JButton("Iniciar");
        iniciarButton.setBounds(390, 40, 100, 25);
        reiniciarButton = new JButton("Reiniciar");
        reiniciarButton.setBounds(500, 40, 100, 25);
        add(agregarButton);
        add(iniciarButton);
        add(reiniciarButton);

        // Tabla para tiempos (ahora con columna para ráfaga)
        modeloTablaTiempos = new DefaultTableModel();
        modeloTablaTiempos.addColumn("Proceso");
        modeloTablaTiempos.addColumn("Ráfaga");
        modeloTablaTiempos.addColumn("Tiempo de Espera");
        modeloTablaTiempos.addColumn("Tiempo de Retorno");
        tablaTiempos = new JTable(modeloTablaTiempos);
        JScrollPane scrollTiempos = new JScrollPane(tablaTiempos);
        scrollTiempos.setBounds(50, 120, 500, 100);
        add(scrollTiempos);

        // Etiquetas para TME y TMR
        lblTME = new JLabel("TME = ");
        lblTME.setBounds(50, 240, 500, 25);
        lblTMR = new JLabel("TMR = ");
        lblTMR.setBounds(50, 270, 500, 25);
        add(lblTME);
        add(lblTMR);

        // Diagrama de Gantt
        lblDiagramaGantt = new JLabel("Diagrama de Gantt:");
        lblDiagramaGantt.setBounds(50, 300, 800, 50);
        add(lblDiagramaGantt);

        // Áreas de texto para mostrar las operaciones paso a paso
        JLabel lblOperacionesTME = new JLabel("Operaciones TME:");
        lblOperacionesTME.setBounds(50, 360, 200, 25);
        add(lblOperacionesTME);

        txtOperacionesTME = new JTextArea();
        JScrollPane scrollOperacionesTME = new JScrollPane(txtOperacionesTME);
        scrollOperacionesTME.setBounds(50, 390, 350, 100);
        add(scrollOperacionesTME);

        JLabel lblOperacionesTMR = new JLabel("Operaciones TMR:");
        lblOperacionesTMR.setBounds(50, 500, 200, 25);
        add(lblOperacionesTMR);

        txtOperacionesTMR = new JTextArea();
        JScrollPane scrollOperacionesTMR = new JScrollPane(txtOperacionesTMR);
        scrollOperacionesTMR.setBounds(50, 530, 350, 100);
        add(scrollOperacionesTMR);

        // Acción del botón agregar
        agregarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarProceso();
            }
        });

        // Acción del botón iniciar
        iniciarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarSimulacion();
            }
        });
    }

    // Definición de la clase Proceso como clase interna
    private class Proceso {
        String id; // Ahora el ID será una letra
        int tiempoLlegada;
        int rafaga;
        int tiempoRestante;
        int tiempoFinal;
        int tiempoEspera;
        int tiempoRetorno;

        public Proceso(String id, int tiempoLlegada, int rafaga) {
            this.id = id;
            this.tiempoLlegada = tiempoLlegada;
            this.rafaga = rafaga;
            this.tiempoRestante = rafaga;
        }
    }

    private void agregarProceso() {
        // Asignar letras a los procesos: A, B, C, ...
        String idProceso = String.valueOf((char) ('A' + listaProcesos.size()));

        // Obtener los valores ingresados
        int rafaga = Integer.parseInt(rafagaField.getText());
        int tiempoLlegada = Integer.parseInt(llegadaField.getText());
        Proceso nuevoProceso = new Proceso(idProceso, tiempoLlegada, rafaga);
        listaProcesos.add(nuevoProceso);

        // Añadir el proceso a la tabla con su ráfaga
        modeloTablaTiempos.addRow(new Object[]{idProceso, rafaga, 0, 0});
    }

    private void iniciarSimulacion() {
        // Resetear diagrama de Gantt y áreas de texto
        diagramaGantt = "";
        txtOperacionesTME.setText("");
        txtOperacionesTMR.setText("");

        int quantum = Integer.parseInt(quantumField.getText());
        int tiempoActual = 0;
        ArrayList<Proceso> cola = new ArrayList<>(listaProcesos);

        // Variables para calcular TME y TMR
        int totalTiempoEspera = 0;
        int totalTiempoRetorno = 0;

        // Simulación Round Robin
        while (!cola.isEmpty()) {
            Proceso proceso = cola.get(0);
            if (proceso.tiempoLlegada <= tiempoActual) {
                diagramaGantt += proceso.id + " ";// Mostrar la letra del proceso
                //Mostrar rafaga abajo de la letra del proceso y la suma de las rafagas
                txtOperacionesTME.append(proceso.rafaga + " + ");
                txtOperacionesTMR.append(proceso.rafaga + " + ");


                if (proceso.tiempoRestante > quantum) {
                    proceso.tiempoRestante -= quantum;
                    tiempoActual += quantum;
                } else {
                    tiempoActual += proceso.tiempoRestante;
                    proceso.tiempoRestante = 0;
                    proceso.tiempoFinal = tiempoActual;
                    proceso.tiempoRetorno = proceso.tiempoFinal - proceso.tiempoLlegada;
                    proceso.tiempoEspera = proceso.tiempoRetorno - proceso.rafaga;

                    // Sumar los tiempos para calcular TME y TMR
                    totalTiempoEspera += proceso.tiempoEspera;
                    totalTiempoRetorno += proceso.tiempoRetorno;

                    // Actualizar la tabla con los tiempos de espera y retorno
                    int index = listaProcesos.indexOf(proceso);
                    modeloTablaTiempos.setValueAt(proceso.tiempoEspera, index, 2);
                    modeloTablaTiempos.setValueAt(proceso.tiempoRetorno, index, 3);

                    // Eliminar el proceso terminado de la cola
                    cola.remove(0);
                }

                // Si el proceso aún no ha terminado, volver a agregarlo al final de la cola
                if (proceso.tiempoRestante > 0) {
                    cola.add(proceso); // Volver a insertar al final de la cola
                    cola.remove(0); // Eliminar la versión antigua del proceso
                }
            } else {
                tiempoActual++;  // Si ningún proceso ha llegado, avanzar el tiempo
            }
        }

        // Calcular TME y TMR
        double tme = (double) totalTiempoEspera / listaProcesos.size();
        double tmr = (double) totalTiempoRetorno / listaProcesos.size();

        // Finalizar las operaciones de TME y TMR
        txtOperacionesTME.append("\b\b= " + totalTiempoEspera + " / " + listaProcesos.size() + " = " + tme + "\n");
        txtOperacionesTMR.append("\b\b= " + totalTiempoRetorno + " / " + listaProcesos.size() + " = " + tmr + "\n");

        // Mostrar TME y TMR
        lblTME.setText("TME = " + tme + " ut");
        lblTMR.setText("TMR = " + tmr + " ut");

        // Mostrar diagrama de Gantt
        lblDiagramaGantt.setText("Diagrama de Gantt: " + diagramaGantt+ " = " + tiempoActual + " ut");




    }

    public static void main(String[] args) {
        new RoundRobinSimulator().setVisible(true);
    }
}
